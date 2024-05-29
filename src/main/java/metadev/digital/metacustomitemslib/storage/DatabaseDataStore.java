package metadev.digital.metacustomitemslib.storage;

import metadev.digital.metacustomitemslib.Core;
import metadev.digital.metacustomitemslib.PlayerSettings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public abstract class DatabaseDataStore implements IDataStore {

	private Plugin plugin;

	public DatabaseDataStore(Plugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Connection to the Database
	 */
	// protected Connection mConnection;

	/**
	 * Args: player name
	 */
	protected PreparedStatement mGetPlayerUUID;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement mGetPlayerSettings;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement mInsertPlayerSettings;

	/**
	 * Args: player player_id
	 */
	protected PreparedStatement mGetPlayerByPlayerId;

	/**
	 * Args: player name, player uuid
	 */
	protected PreparedStatement mUpdatePlayerName;

	/**
	 * Establish initial connection to Database
	 */
	protected abstract Connection setupConnection() throws SQLException, DataStoreException;

	/**
	 * Setup / Create database version 3 tables for BagOfGold
	 */
	protected abstract void setupV3Tables(Connection connection) throws SQLException;

	/**
	 * Open a connection to the Database and prepare a statement for executing.
	 * 
	 * @param connection
	 * @param preparedConnectionType
	 * @throws SQLException
	 */
	protected abstract void openPreparedStatements(Connection connection, PreparedConnectionType preparedConnectionType)
			throws SQLException;

	public enum PreparedConnectionType {
		GET_PLAYER_UUID, GET_PLAYER_SETTINGS, INSERT_PLAYER_SETTINGS, UPDATE_PLAYER_NAME, GET_PLAYER_BY_PLAYER_ID,
	};

	/**
	 * Initialize the connection. Must be called after Opening of initial
	 * connection. Open Prepared statements for batch processing large selections of
	 * players. Batches will be performed in batches of 10,5,2,1
	 */
	@Override
	public void initialize() throws DataStoreException {
		Core.getMessages().debug("Initialize database");
		try {

			Connection mConnection = setupConnection();

			// Find current database version
			if (Core.getConfigManager().databaseVersion < 1) {
				Statement statement = mConnection.createStatement();
				try {
					ResultSet rs = statement.executeQuery("SELECT UUID FROM mh_PlayerSettings LIMIT 0");
					rs.close();
					Core.getConfigManager().databaseVersion = 1;
				} catch (SQLException e1) {
					Core.getConfigManager().databaseVersion = 0;
				}
				statement.close();
				Core.getConfigManager().saveConfig();
				Bukkit.getConsoleSender().sendMessage(
						Core.PREFIX + " Database version " + Core.getConfigManager().databaseVersion + " detected.");
			}

			switch (Core.getConfigManager().databaseVersion) {
			case 0:
			case 1:
				setupV3Tables(mConnection);
				break;
			}

			Core.getStoreManager().createRandomBountyPlayer();

			Core.getConfigManager().databaseVersion = 1;
			Core.getConfigManager().saveConfig();

			// Enable FOREIGN KEY for Sqlite database
			if (!Core.getConfigManager().databaseType.equalsIgnoreCase("MySQL")) {
				Statement statement = mConnection.createStatement();
				statement.execute("PRAGMA foreign_keys = ON");
				statement.close();
			}
			mConnection.commit();
			mConnection.close();

		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * Rollback of last transaction on Database.
	 * 
	 * @throws DataStoreException
	 */
	protected void rollback(Connection mConnection) throws DataStoreException {

		try {
			mConnection.rollback();
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * Shutdown: Commit and close database connection completely.
	 */
	@Override
	public void shutdown() throws DataStoreException {
		int n = 0;
		do {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			n++;
		} while (Core.getDataStoreManager().isRunning() && n < 40);
		Bukkit.getConsoleSender().sendMessage(Core.PREFIX + " Closing database connection.");
	}

	// ******************************************************************
	// Player Settings
	// ******************************************************************

	/**
	 * getPlayerSettings
	 * 
	 * @param offlinePlayer :OfflinePlayer
	 * @return PlayerData
	 * @throws DataStoreException
	 * @throws SQLException
	 * 
	 */
	// @Override
	public PlayerSettings loadPlayerSettings(OfflinePlayer offlinePlayer)
			throws UserNotFoundException, DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_SETTINGS);
			mGetPlayerSettings.setString(1, offlinePlayer.getUniqueId().toString());
			ResultSet result;
			result = mGetPlayerSettings.executeQuery();
			if (result.next()) {
				PlayerSettings ps = new PlayerSettings(offlinePlayer, result.getString("LAST_WORLDGRP"),
						result.getBoolean("LEARNING_MODE"), result.getBoolean("MUTE_MODE"), result.getString("TEXTURE"),
						result.getString("SIGNATURE"), result.getLong("LAST_LOGON"), result.getLong("LAST_INTEREST"));
				ps.setPlayerId(result.getInt("PLAYER_ID"));
				Core.getMessages().debug("Player %s loaded. Texture=%s", ps.getPlayer().getName(), ps.getTexture());
				result.close();
				mGetPlayerSettings.close();
				mConnection.close();
				return ps;
			}
			mGetPlayerSettings.close();
			mConnection.close();
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
		throw new UserNotFoundException(
				Core.PREFIX + " User " + offlinePlayer.toString() + " is not present in database");
	}

	// @Override
	public void savePlayerSettings(Set<PlayerSettings> playerDataSet, boolean removeFromCache)
			throws DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			try {
				openPreparedStatements(mConnection, PreparedConnectionType.INSERT_PLAYER_SETTINGS);
				for (PlayerSettings playerSettings : playerDataSet) {
					mInsertPlayerSettings.setString(1, playerSettings.getPlayer().getUniqueId().toString());
					if (playerSettings.getPlayerId() == 0)
						mInsertPlayerSettings.setString(2, null);
					else
						mInsertPlayerSettings.setInt(2, playerSettings.getPlayerId());
					mInsertPlayerSettings.setString(3, playerSettings.getPlayer().getName());
					mInsertPlayerSettings.setString(4, playerSettings.getLastKnownWorldGrp());
					mInsertPlayerSettings.setInt(5, playerSettings.isLearningMode() ? 1 : 0);
					mInsertPlayerSettings.setInt(6, playerSettings.isMuted() ? 1 : 0);
					mInsertPlayerSettings.setString(7, playerSettings.getTexture());
					mInsertPlayerSettings.setString(8, playerSettings.getSignature());
					mInsertPlayerSettings.setLong(9, playerSettings.getLast_logon());
					mInsertPlayerSettings.setLong(10, playerSettings.getLast_interest());

					mInsertPlayerSettings.addBatch();
				}
				mInsertPlayerSettings.executeBatch();
				mInsertPlayerSettings.close();
				mConnection.commit();
				mConnection.close();

				Core.getMessages().debug("PlayerSettings saved.");

				if (removeFromCache)
					for (PlayerSettings playerData : playerDataSet) {
						if (Core.getPlayerSettingsManager().containsKey(playerData.getPlayer())
								&& !playerData.getPlayer().isOnline() && playerData.getPlayer().hasPlayedBefore())
							Core.getPlayerSettingsManager().removePlayerSettings(playerData.getPlayer());
					}

			} catch (SQLException e) {
				rollback(mConnection);
				mConnection.close();
				throw new DataStoreException(e);
			}
		} catch (SQLException e1) {
			throw new DataStoreException(e1);
		}
	}

	/**
	 * getPlayerByName - get the player
	 * 
	 * @param name : String
	 * @return player
	 */
	@Override
	public OfflinePlayer getPlayerByName(String name) throws DataStoreException {
		if (name.equals("Random Bounty"))
			return null; // used for Random Bounties
		try {
			Connection mConnection = setupConnection();

			openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_UUID);
			mGetPlayerUUID.setString(1, name);
			ResultSet set = mGetPlayerUUID.executeQuery();

			if (set.next()) {
				UUID uid = UUID.fromString(set.getString("UUID"));
				set.close();
				mGetPlayerUUID.close();
				mConnection.close();
				return Bukkit.getOfflinePlayer(uid);
			}
			mGetPlayerUUID.close();
			mConnection.close();
			throw new UserNotFoundException(Core.PREFIX + " User " + name + " is not present in database");
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * updatePlayerName - update the players name in the Database
	 * 
	 * @param offlinePlayer : OfflinePlayer
	 * @throws SQLException
	 * @throws DataStoreException
	 */
	protected void updatePlayerName(OfflinePlayer offlinePlayer) throws DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			try {
				openPreparedStatements(mConnection, PreparedConnectionType.UPDATE_PLAYER_NAME);
				mUpdatePlayerName.setString(1, offlinePlayer.getName());
				mUpdatePlayerName.setString(2, offlinePlayer.getUniqueId().toString());
				mUpdatePlayerName.executeUpdate();
				mUpdatePlayerName.close();
				mConnection.commit();
				mConnection.close();
			} catch (SQLException e) {
				rollback(mConnection);
				throw new DataStoreException(e);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * getPlayerByPlayerId - get the player
	 * 
	 * @param name : String
	 * @return player
	 */
	@Override
	public OfflinePlayer getPlayerByPlayerId(int playerId) throws DataStoreException {
		if (playerId == 0)
			return null; // Used for Random Bounty
		try {
			Connection mConnection = setupConnection();
			openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_BY_PLAYER_ID);
			mGetPlayerByPlayerId.setInt(1, playerId);
			ResultSet set = mGetPlayerByPlayerId.executeQuery();

			if (set.next()) {
				UUID uuid = UUID.fromString(set.getString("UUID"));
				set.close();
				mGetPlayerByPlayerId.close();
				mConnection.close();
				return Bukkit.getOfflinePlayer(uuid);
			}
			mGetPlayerByPlayerId.close();
			mConnection.close();
			return null;
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * getPlayerID. get the player ID and check if the player has change name
	 * 
	 * @param offlinePlayer
	 * @return PlayerID: int
	 * @throws SQLException
	 * @throws DataStoreException
	 */
	@Override
	public int getPlayerId(OfflinePlayer offlinePlayer) throws DataStoreException {
		if (offlinePlayer == null)
			return 0;
		int playerId = 0;
		PlayerSettings ps = Core.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
		if (ps != null) {
			playerId = ps.getPlayerId();
		}
		if (playerId == 0) {
			Connection mConnection;
			try {
				ArrayList<OfflinePlayer> changedNames = new ArrayList<OfflinePlayer>();

				mConnection = setupConnection();
				openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_SETTINGS);
				mGetPlayerSettings.setString(1, offlinePlayer.getUniqueId().toString());
				ResultSet result = mGetPlayerSettings.executeQuery();
				if (result.next()) {
					String name = result.getString("NAME");
					UUID uuid = UUID.fromString(result.getString("UUID"));
					if (name != null && uuid != null && offlinePlayer.getName() != null
							&& !offlinePlayer.getName().isEmpty())
						if (offlinePlayer.getUniqueId().equals(uuid) && !offlinePlayer.getName().equals(name)) {
							Core.getMessages().debug("Name change detected(2): " + name + " -> "
									+ offlinePlayer.getName() + " UUID=" + offlinePlayer.getUniqueId().toString());
							changedNames.add(offlinePlayer);
						}
					playerId = result.getInt(3);
					result.close();

				}
				result.close();
				mGetPlayerSettings.close();
				mConnection.close();

				Iterator<OfflinePlayer> itr = changedNames.iterator();
				while (itr.hasNext()) {
					OfflinePlayer p = itr.next();
					Core.getMessages().debug("Updating playername in database and in memory (%s)", p.getName());
					updatePlayerName(p.getPlayer());
				}
			} catch (SQLException e) {
				throw new DataStoreException(e);
			}
		}
		return playerId;
	}

	/**
	 * create a RandomBountyPlayer if not exist in mh_PlayerSettings
	 * 
	 * @param connection
	 * @throws DataStoreException
	 */
	public void createRandomBountyPlayer() throws DataStoreException {
		// added because BOUNTYOWNER_ID is null for Random bounties.
		try {
			Connection mConnection = setupConnection();
			Statement query = mConnection.createStatement();
			ResultSet rs = query.executeQuery("SELECT PLAYER_ID from mh_PlayerSettings WHERE PLAYER_ID=0");
			if (!rs.next()) {
				Statement create = mConnection.createStatement();
				Bukkit.getConsoleSender()
						.sendMessage(Core.PREFIX + " Adding RandomBounty Player to CustomItemsLibs Database.");
				create.executeUpdate(
						"insert into mh_PlayerSettings (UUID,PLAYER_ID,NAME,LAST_WORLDGRP,LEARNING_MODE,MUTE_MODE) values ('"
								+ DataStoreManager.RANDOM_PLAYER_UUID + "',0,'RandomBounty','default',0,0)");
				create.executeUpdate("update mh_PlayerSettings set Player_id=0 where name='RandomBounty'");
				create.close();
				mConnection.commit();
			}
			rs.close();
			query.close();
			mConnection.close();
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	@Override
	public void databaseDeleteOldPlayers() {
		Core.getMessages().debug("Deleting players not known on this server.");
		int n = 0;
		try {
			Connection mConnection = setupConnection();
			Statement statement = mConnection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT UUID, NAME FROM mh_PlayerSettings");
			while (rs.next()) {
				String uuid = rs.getString("UUID");
				String playername = rs.getString("NAME");
				Core.getMessages().debug("Player:%s (%s) - hasplayedbefore:%s", playername, uuid,
						Bukkit.getOfflinePlayer(UUID.fromString(uuid)).hasPlayedBefore());
				if (!Bukkit.getOfflinePlayer(UUID.fromString(uuid)).hasPlayedBefore()) {
					Core.getMessages().debug("Deleting %s (%s) from mh_PlayerSettings", playername, uuid);
					statement.executeUpdate("DELETE FROM mh_PlayerSettings WHERE UUID='" + uuid + "'");
					n++;
				}
			}
			rs.close();
			statement.close();
			mConnection.close();
			Bukkit.getConsoleSender()
					.sendMessage(Core.PREFIX + " " + n + " players was deleted from the CustomItemsLib database.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package one.lindegaard.CustomItemsLib.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import one.lindegaard.CustomItemsLib.Core;

public class SQLiteDataStore extends DatabaseDataStore {

	private Plugin plugin;

	public SQLiteDataStore(Plugin plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	// *******************************************************************************
	// SETUP / INITIALIZE
	// *******************************************************************************

	@Override
	protected Connection setupConnection() throws DataStoreException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection(
					"jdbc:sqlite:" + plugin.getDataFolder().getPath() + Core.getConfigManager().databaseName + ".db");
			connection.setAutoCommit(false);
			return connection;
		} catch (ClassNotFoundException classNotFoundEx) {
			throw new DataStoreException("SQLite not present on the classpath", classNotFoundEx);
		} catch (SQLException sqlEx) {
			throw new DataStoreException("Error creating sql connection", sqlEx);
		}
	}

	@Override
	protected void openPreparedStatements(Connection connection, PreparedConnectionType preparedConnectionType)
			throws SQLException {
		switch (preparedConnectionType) {
		case GET_PLAYER_UUID:
			mGetPlayerUUID = connection.prepareStatement("SELECT UUID FROM mh_PlayerSettings WHERE NAME=?;");
			break;
		case GET_PLAYER_SETTINGS:
			mGetPlayerSettings = connection.prepareStatement("SELECT * FROM mh_PlayerSettings WHERE UUID=?;");
			break;
		case INSERT_PLAYER_SETTINGS:
			mInsertPlayerSettings = connection.prepareStatement(
					"INSERT OR REPLACE INTO mh_PlayerSettings (UUID,PLAYER_ID,NAME,LAST_WORLDGRP,LEARNING_MODE,MUTE_MODE,TEXTURE,SIGNATURE,LAST_LOGON,LAST_INTEREST) "
							+ "VALUES(?,?,?,?,?,?,?,?,?,?);");
			break;
		case UPDATE_PLAYER_NAME:
			mUpdatePlayerName = connection.prepareStatement("UPDATE mh_PlayerSettings SET NAME=? WHERE UUID=?;");
			break;
		case GET_PLAYER_BY_PLAYER_ID:
			mGetPlayerByPlayerId = connection.prepareStatement("SELECT UUID FROM mh_PlayerSettings WHERE PLAYER_ID=?;");
			break;
		}
	}

	@Override
	public void databaseConvertToUtf8(String database_name) throws DataStoreException {
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(Core.PREFIX + ChatColor.RED + " this command is only for MySQL");
	}

	// *******************************************************************************
	// V3 DATABASE SETUP
	// *******************************************************************************

	@Override
	protected void setupV3Tables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();

		// Create new empty tables if they do not exist
		String lm = Core.getConfigManager().learningMode ? "1" : "0";
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_PlayerSettings" //
				+ "(UUID TEXT," //
				+ " PLAYER_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + " NAME TEXT, " //
				+ " LAST_WORLDGRP NOT NULL DEFAULT 'default'," //
				+ " LEARNING_MODE INTEGER NOT NULL DEFAULT " + lm + "," //
				+ " MUTE_MODE INTEGER NOT NULL DEFAULT 0," //
				+ " TEXTURE TEXT, " //
				+ " SIGNATURE TEXT, " //
				+ " LAST_LOGON INTEGER, " //
				+ " LAST_INTEREST INTEGER, " //
				+ " UNIQUE(UUID))");
		create.close();
		connection.commit();

	}

}

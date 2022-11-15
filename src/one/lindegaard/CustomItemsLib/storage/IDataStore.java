package one.lindegaard.CustomItemsLib.storage;

import java.sql.SQLException;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import one.lindegaard.CustomItemsLib.PlayerSettings;

public interface IDataStore {
	/**
	 * Initialize - opening a connection to the Database and initialize the
	 * connection.
	 * 
	 * @throws DataStoreException
	 */
	void initialize() throws DataStoreException;

	/**
	 * Closing all connections to the Database
	 * 
	 * @throws DataStoreException
	 */
	void shutdown() throws DataStoreException;

	/**
	 * Get the players Settings from the Database
	 * 
	 * @param player
	 * @return
	 * @throws DataStoreException
	 * @throws SQLException
	 */
	PlayerSettings loadPlayerSettings(OfflinePlayer player) throws UserNotFoundException, DataStoreException;

	/**
	 * Update the players Settings in the Database
	 * 
	 * @param playerDataSet
	 * @throws DataStoreException
	 */
	void savePlayerSettings(Set<PlayerSettings> ps, boolean cleanCache) throws DataStoreException;

	/**
	 * Insert all PlayerData for one player into the Database
	 * 
	 * @param ps
	 * @throws DataStoreException
	 */
	//void insertPlayerSettings(PlayerSettings ps) throws DataStoreException;

	/**
	 * Get the player by his name from the Database. ings @param name
	 * 
	 * @return
	 * @throws DataStoreException
	 */
	OfflinePlayer getPlayerByName(String name) throws DataStoreException;

	/**
	 * Get the OfflinePlayer from the internal playerId
	 * 
	 * @param playerId
	 * @return
	 * @throws DataStoreException
	 */
	OfflinePlayer getPlayerByPlayerId(int playerId) throws DataStoreException;

	/**
	 * Get the player ID directly from the database
	 * 
	 * @param player
	 * @return
	 * @throws DataStoreException
	 * @throws UserNotFoundException
	 */
	int getPlayerId(OfflinePlayer player) throws DataStoreException;

	/**
	 * Convert all tables to use UTF-8 character set.
	 * 
	 * @param database_name
	 * @throws DataStoreException
	 */
	void databaseConvertToUtf8(String database_name) throws DataStoreException;

	/**
	 * create a RandomBountyPlayer if not exist in mh_PlayerSettings
	 * 
	 * @param connection
	 * @throws DataStoreException 
	 */
	void createRandomBountyPlayer() throws DataStoreException;
	
	/**
	 * Delete all players which is not known on the server.
	 */
	void databaseDeleteOldPlayers() throws DataStoreException;
}

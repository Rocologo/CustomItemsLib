package one.lindegaard.CustomItemsLib.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import com.mysql.cj.jdbc.MysqlDataSource;

import one.lindegaard.CustomItemsLib.Core;

public class MySQLDataStore extends DatabaseDataStore {

	private Plugin plugin;

	public MySQLDataStore(Plugin plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	// *******************************************************************************
	// SETUP / INITIALIZE
	// *******************************************************************************

	@Override
	protected Connection setupConnection() throws DataStoreException {
		try {
			Locale.setDefault(new Locale("us", "US"));
			Class.forName("com.mysql.jdbc.Driver");
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUser(Core.getConfigManager().databaseUsername);
			dataSource.setPassword(Core.getConfigManager().databasePassword);
			if (Core.getConfigManager().databaseHost.contains(":")) {
				dataSource.setServerName(Core.getConfigManager().databaseHost.split(":")[0]);
				dataSource.setPort(Integer.valueOf(Core.getConfigManager().databaseHost.split(":")[1]));
			} else {
				dataSource.setServerName(Core.getConfigManager().databaseHost);
			}
			dataSource.setDatabaseName(Core.getConfigManager().databaseName);
			//+ "?autoReconnect=true&useSSL="
			//		+ Core.getConfigManager().databaseUseSSL);
			Connection c = dataSource.getConnection();
			Statement statement = c.createStatement();
			statement.executeUpdate("SET NAMES 'utf8'");
			statement.executeUpdate("SET CHARACTER SET 'utf8'");
			statement.close();
			c.setAutoCommit(false);
			return c;
		} catch (ClassNotFoundException classNotFoundEx) {
			throw new DataStoreException("MySQL not present on the classpath", classNotFoundEx);
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
					"REPLACE INTO mh_PlayerSettings (UUID,PLAYER_ID,NAME,LAST_WORLDGRP,LEARNING_MODE,MUTE_MODE,TEXTURE,SIGNATURE,LAST_LOGON,LAST_INTEREST) "
							+ "VALUES(?,?,?,?,?,?,?,?,?,?);");
			break;
		case GET_PLAYER_BY_PLAYER_ID:
			mGetPlayerByPlayerId = connection.prepareStatement("SELECT UUID FROM mh_PlayerSettings WHERE PLAYER_ID=?;");
			break;
		case UPDATE_PLAYER_NAME:
			mUpdatePlayerName = connection.prepareStatement("UPDATE mh_PlayerSettings SET NAME=? WHERE UUID=?;");
			break;
		}

	}

	@Override
	public void databaseConvertToUtf8(String database_name) throws DataStoreException {

		// reference
		// http://stackoverflow.com/questions/6115612/how-to-convert-an-entire-mysql-database-characterset-and-collation-to-utf-8

		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(ChatColor.GREEN + "[BagOfGold] Converting BagOfGold Database to UTF8");

		Connection connection = setupConnection();

		try {
			Statement create = connection.createStatement();

			create.executeUpdate(
					"ALTER DATABASE " + database_name + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			create.executeUpdate("ALTER TABLE mh_Balance CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			create.executeUpdate(
					"ALTER TABLE mh_PlayerSettings CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			console.sendMessage(ChatColor.GREEN + "[BagOfGold] Done.");

		} catch (SQLException e) {
			console.sendMessage(
					ChatColor.RED + "[BagOfGold] Something went wrong when converting database tables to UTF8MB4.");
			e.printStackTrace();
		}

	}

	// *******************************************************************************
	// V3 DATABASE SETUP
	// *******************************************************************************

	@Override
	protected void setupV3Tables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();

		// Create new empty tables if they do not exist
		String lm = Core.getConfigManager().learningMode ? "1" : "0";
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_PlayerSettings "//
				+ "(UUID CHAR(40),"//
				+ " PLAYER_ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,"//
				+ " NAME VARCHAR(20),"//
				+ " LAST_WORLDGRP VARCHAR(20) NOT NULL DEFAULT 'default'," //
				+ " LEARNING_MODE INTEGER NOT NULL DEFAULT " + lm + ","//
				+ " MUTE_MODE INTEGER NOT NULL DEFAULT 0, "//
				+ " TEXTURE TEXT, " //
				+ " SIGNATURE TEXT, " //
				+ " LAST_LOGON BIGINT, " //
				+ " LAST_INTEREST BIGINT, "
				+ " UNIQUE(UUID))");
		create.close();
		connection.commit();
	}

}

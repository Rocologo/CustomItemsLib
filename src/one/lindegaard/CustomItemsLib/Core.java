package one.lindegaard.CustomItemsLib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import one.lindegaard.CustomItemsLib.commands.CommandDispatcher;
import one.lindegaard.CustomItemsLib.commands.DebugCommand;
import one.lindegaard.CustomItemsLib.commands.ReloadCommand;
import one.lindegaard.CustomItemsLib.commands.UpdateCommand;
import one.lindegaard.CustomItemsLib.commands.VersionCommand;
import one.lindegaard.CustomItemsLib.compatibility.ActionAnnouncerCompat;
import one.lindegaard.CustomItemsLib.compatibility.ActionBarAPICompat;
import one.lindegaard.CustomItemsLib.compatibility.ActionbarCompat;
import one.lindegaard.CustomItemsLib.compatibility.BagOfGoldCompat;
import one.lindegaard.CustomItemsLib.compatibility.BarAPICompat;
import one.lindegaard.CustomItemsLib.compatibility.BossBarAPICompat;
import one.lindegaard.CustomItemsLib.compatibility.CMICompat;
import one.lindegaard.CustomItemsLib.compatibility.CompatPlugin;
import one.lindegaard.CustomItemsLib.compatibility.CompatibilityManager;
import one.lindegaard.CustomItemsLib.compatibility.MobHuntingCompat;
import one.lindegaard.CustomItemsLib.compatibility.ProtocolLibCompat;
import one.lindegaard.CustomItemsLib.compatibility.TitleAPICompat;
import one.lindegaard.CustomItemsLib.compatibility.TitleManagerCompat;
import one.lindegaard.CustomItemsLib.config.ConfigManager;
import one.lindegaard.CustomItemsLib.messages.Messages;
import one.lindegaard.CustomItemsLib.rewards.CoreRewardManager;
import one.lindegaard.CustomItemsLib.rewards.RewardBlockManager;
import one.lindegaard.CustomItemsLib.storage.DataStoreException;
import one.lindegaard.CustomItemsLib.storage.DataStoreManager;
import one.lindegaard.CustomItemsLib.storage.IDataStore;
import one.lindegaard.CustomItemsLib.storage.MySQLDataStore;
import one.lindegaard.CustomItemsLib.storage.SQLiteDataStore;
import one.lindegaard.CustomItemsLib.update.SpigetUpdater;

public class Core extends JavaPlugin {

	private static Core plugin;
	private File mFile = new File(getDataFolder(), "config.yml");

	private static ConfigManager mConfig;
	private static Messages mMessages;
	private static RewardBlockManager mRewardBlockManager;
	private static WorldGroupManager mWorldGroupManager;
	private static IDataStore mStore;
	private static DataStoreManager mDataStoreManager;
	private static PlayerSettingsManager mPlayerSettingsManager;
	private static CoreRewardManager mCoreRewardManager;
	private static CompatibilityManager mCompatibilityManager;
	private CommandDispatcher mCommandDispatcher;
	private SpigetUpdater mSpigetUpdater;

	// Public Placeholders used in BagOfGold and MobHunting
	public static final String PH_PLAYERNAME = "playername";
	public static final String PH_MONEY = "money";
	public static final String PH_REWARDNAME = "rewardname";
	public static final String PH_COMMAND = "command";
	public static final String PH_PERMISSION = "perm";

	public boolean disabling = false;

	public static final String PREFIX = ChatColor.GOLD + "[CustomItemsLib] " + ChatColor.RESET;
	public static final String PREFIX_DEBUG = ChatColor.GOLD + "[CustomItemsLib][Debug] " + ChatColor.RESET;
	public static final String PREFIX_WARNING = ChatColor.GOLD + "[CustomItemsLib][Warning] " + ChatColor.RED;
	public static final String PREFIX_ERROR = ChatColor.GOLD + "[CustomItemsLib][Error] " + ChatColor.RED;

	@Override
	public void onLoad() {
	}

	@Override
	public void onEnable() {

		disabling = false;
		plugin = this;

		if (!mFile.exists()) {
			mFile.mkdir();
			// Copy config and database from old place
			File mFileOldConfig = new File(getDataFolder().getParent() + "/BagOfGold", "/bagofgoldcore.yml");
			File mFileOldDb = new File(getDataFolder().getParent() + "/BagOfGold", "/bagofgoldcore.db");
			File mFileNewDb = new File(getDataFolder(), "/bagofgoldcore.db");
			if (mFileOldConfig.exists()) {
				try {
					Files.move(mFileOldConfig.toPath(), mFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (mFileOldDb.exists()) {
				try {
					Files.move(mFileOldDb.toPath(), mFileNewDb.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		int config_version = ConfigManager.getConfigVersion(mFile);

		mConfig = new ConfigManager(mFile);
		if (mConfig.loadConfig()) {
			mConfig.saveConfig();
		} else
			throw new RuntimeException("[CustomItemsLib] Could not load config.yml");

		mMessages = new Messages(plugin);
		mMessages.setLanguage(mConfig.language + ".lang");
		mMessages.debug("Loading config.yml file, version %s", config_version);

		List<String> itemtypes = Arrays.asList("SKULL", "ITEM", "KILLER", "KILLED", "GRINGOTTS_STYLE");
		if (!itemtypes.contains(mConfig.rewardItemtype)) {
			Bukkit.getConsoleSender().sendMessage(PREFIX + ChatColor.RED
					+ "The type define with reward_itemtype in your config is unknown: " + mConfig.rewardItemtype);
		}

		mWorldGroupManager = new WorldGroupManager(plugin);
		mRewardBlockManager = new RewardBlockManager(plugin);

		// Register commands
		mCommandDispatcher = new CommandDispatcher(this, "customitemslib",
				Core.getMessages().getString("core.command.base.description") + getDescription().getVersion());
		getCommand("customitemslib").setExecutor(mCommandDispatcher);
		getCommand("customitemslib").setTabCompleter(mCommandDispatcher);
		mCommandDispatcher.registerCommand(new ReloadCommand(this));
		mCommandDispatcher.registerCommand(new UpdateCommand(this));
		mCommandDispatcher.registerCommand(new VersionCommand(this));
		mCommandDispatcher.registerCommand(new DebugCommand(this));

		if (mConfig.databaseType.equalsIgnoreCase("mysql"))
			mStore = new MySQLDataStore(plugin);
		else
			mStore = new SQLiteDataStore(plugin);

		try {
			mStore.initialize();
		} catch (DataStoreException e) {
			e.printStackTrace();
			try {
				mStore.shutdown();
			} catch (DataStoreException e1) {
				e1.printStackTrace();
			}
			return;
		}

		mDataStoreManager = new DataStoreManager(plugin, mStore);
		mPlayerSettingsManager = new PlayerSettingsManager(plugin);
		mCoreRewardManager = new CoreRewardManager(plugin);

		mCompatibilityManager = new CompatibilityManager(plugin);

		mCompatibilityManager.registerPlugin(ProtocolLibCompat.class, CompatPlugin.ProtocolLib);

		mCompatibilityManager.registerPlugin(TitleManagerCompat.class, CompatPlugin.TitleManager);
		mCompatibilityManager.registerPlugin(TitleAPICompat.class, CompatPlugin.TitleAPI);
		mCompatibilityManager.registerPlugin(ActionAnnouncerCompat.class, CompatPlugin.ActionAnnouncer);
		mCompatibilityManager.registerPlugin(ActionBarAPICompat.class, CompatPlugin.ActionBarApi);
		mCompatibilityManager.registerPlugin(ActionbarCompat.class, CompatPlugin.Actionbar);
		mCompatibilityManager.registerPlugin(BossBarAPICompat.class, CompatPlugin.BossBarApi);
		mCompatibilityManager.registerPlugin(BarAPICompat.class, CompatPlugin.BarApi);
		mCompatibilityManager.registerPlugin(CMICompat.class, CompatPlugin.CMI);

		mCompatibilityManager.registerPlugin(BagOfGoldCompat.class, CompatPlugin.BagOfGold);
		mCompatibilityManager.registerPlugin(MobHuntingCompat.class, CompatPlugin.MobHunting);

		// Check for new updates
		mSpigetUpdater = new SpigetUpdater(this);
		mSpigetUpdater.setCurrentJarFile(this.getFile().getName());
		mSpigetUpdater.hourlyUpdateCheck(getServer().getConsoleSender(), mConfig.updateCheck, false);

	}

	@Override
	public void onDisable() {
		disabling = true;
		try {
			getMessages().debug("Saving all rewardblocks to disk.");
			mRewardBlockManager.save();
			getMessages().debug("Saving worldgroups.");
			mWorldGroupManager.save();
			getMessages().debug("Shutdown StoreManager");
			mDataStoreManager.shutdown();
			getMessages().debug("Shutdown Store");
			mStore.shutdown();
		} catch (DataStoreException e) {
			e.printStackTrace();
		}
	}

	public static Core getInstance() {
		return plugin;
	}

	public static ConfigManager getConfigManager() {
		return mConfig;
	}

	public static Messages getMessages() {
		return mMessages;
	}

	public static WorldGroupManager getWorldGroupManager() {
		return mWorldGroupManager;
	}

	public static RewardBlockManager getRewardBlockManager() {
		return mRewardBlockManager;
	}

	public static IDataStore getStoreManager() {
		return mStore;
	}

	public static DataStoreManager getDataStoreManager() {
		return mDataStoreManager;
	}

	public static PlayerSettingsManager getPlayerSettingsManager() {
		return mPlayerSettingsManager;
	}

	public static CoreRewardManager getCoreRewardManager() {
		return mCoreRewardManager;
	}

	public SpigetUpdater getSpigetUpdater() {
		return mSpigetUpdater;
	}

	/**
	 * setMessages
	 * 
	 * @param messages
	 */
	public static void setMessages(Messages messages) {
		mMessages = messages;
	}

}

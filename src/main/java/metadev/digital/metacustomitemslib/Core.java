package metadev.digital.metacustomitemslib;

import com.google.common.io.Files;

import metadev.digital.metacustomitemslib.commands.CommandDispatcher;
import metadev.digital.metacustomitemslib.commands.DebugCommand;
import metadev.digital.metacustomitemslib.commands.ReloadCommand;
import metadev.digital.metacustomitemslib.commands.UpdateCommand;
import metadev.digital.metacustomitemslib.commands.VersionCommand;
// TODO: ACTIONANNOUNCER Deprecated? import metadev.digital.metacustomitemslib.compatibility.ActionAnnouncerCompat;
import metadev.digital.metacustomitemslib.compatibility.ActionBarAPICompat;
import metadev.digital.metacustomitemslib.compatibility.ActionbarCompat;
import metadev.digital.metacustomitemslib.compatibility.BagOfGoldCompat;
import metadev.digital.metacustomitemslib.compatibility.BarAPICompat;
import metadev.digital.metacustomitemslib.compatibility.BossBarAPICompat;
import metadev.digital.metacustomitemslib.compatibility.CMICompat;
import metadev.digital.metacustomitemslib.compatibility.CompatPlugin;
import metadev.digital.metacustomitemslib.compatibility.CompatibilityManager;
import metadev.digital.metacustomitemslib.compatibility.MobHuntingCompat;
import metadev.digital.metacustomitemslib.compatibility.ProtocolLibCompat;
import metadev.digital.metacustomitemslib.compatibility.TitleAPICompat;
import metadev.digital.metacustomitemslib.compatibility.TitleManagerCompat;
import metadev.digital.metacustomitemslib.config.ConfigManager;
import metadev.digital.metacustomitemslib.messages.Messages;
import metadev.digital.metacustomitemslib.rewards.CoreRewardManager;
import metadev.digital.metacustomitemslib.rewards.RewardBlockManager;
import metadev.digital.metacustomitemslib.storage.DataStoreException;
import metadev.digital.metacustomitemslib.storage.DataStoreManager;
import metadev.digital.metacustomitemslib.storage.IDataStore;
import metadev.digital.metacustomitemslib.storage.MySQLDataStore;
import metadev.digital.metacustomitemslib.storage.SQLiteDataStore;
import metadev.digital.metacustomitemslib.update.UpdateManager;
// import metadev.digital.metacustomitemslib.update.SpigetUpdater;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Core extends JavaPlugin {

	private static Core plugin;
	private File mFile = new File(getDataFolder(), "config.yml");

	private static ConfigManager mConfig;
	private static Messages mMessages;
	private static EconomyManager mEconomyManager;
	private static RewardBlockManager mRewardBlockManager;
	private static WorldGroupManager mWorldGroupManager;
	private static IDataStore mStore;
	private static DataStoreManager mDataStoreManager;
	private static PlayerSettingsManager mPlayerSettingsManager;
	private static CoreRewardManager mCoreRewardManager;
	private static CompatibilityManager mCompatibilityManager;
	private UpdateManager mUpdateManager;
	private CommandDispatcher mCommandDispatcher;

	// Public Placeholders used in BagOfGold and MobHunting
	public static final String PH_PLAYERNAME = "playername";
	public static final String PH_MONEY = "money";
	public static final String PH_REWARDNAME = "rewardname";
	public static final String PH_COMMAND = "command";
	public static final String PH_PERMISSION = "perm";

	public boolean disabling = false;

	//TODO: Move logs to messages
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
			File mFileOldConfig = new File(getDataFolder() , "../BagOfGold/bagofgoldcore.yml");
			File mFileOldDb = new File(getDataFolder(), "../BagOfGold/bagofgoldcore.db");
			File mFileNewDb = new File(getDataFolder(), "bagofgoldcore.db");
			if (mFileOldConfig.exists()) {
				try {
					Files.move(mFileOldConfig, mFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (mFileOldDb.exists()) {
				try {
					Files.move(mFileOldDb, mFileNewDb);
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
		// TODO: ACTIONANNOUNCER Deprecated? mCompatibilityManager.registerPlugin(ActionAnnouncerCompat.class, CompatPlugin.ActionAnnouncer);
		mCompatibilityManager.registerPlugin(ActionBarAPICompat.class, CompatPlugin.ActionBarApi);
		mCompatibilityManager.registerPlugin(ActionbarCompat.class, CompatPlugin.Actionbar);
		mCompatibilityManager.registerPlugin(BossBarAPICompat.class, CompatPlugin.BossBarApi);
		mCompatibilityManager.registerPlugin(BarAPICompat.class, CompatPlugin.BarApi);
		mCompatibilityManager.registerPlugin(CMICompat.class, CompatPlugin.CMI);

		mCompatibilityManager.registerPlugin(BagOfGoldCompat.class, CompatPlugin.BagOfGold);
		mCompatibilityManager.registerPlugin(MobHuntingCompat.class, CompatPlugin.MobHunting);

		// Hook into Vault or Reserve
		mEconomyManager = new EconomyManager(this);
		if (!mEconomyManager.isActive())
			return;
		
		// Check for new updates
		mUpdateManager = new UpdateManager(plugin);
		mUpdateManager.handleUpdateCheck();

		/** mSpigetUpdater = new SpigetUpdater(this);
		mSpigetUpdater.setCurrentJarFile(this.getFile().getName());
		mSpigetUpdater.hourlyUpdateCheck(getServer().getConsoleSender(), mConfig.updateCheck, false); */

	}

	@Override
	public void onDisable() {
		disabling = true;
		try {
			getMessages().debug("Saving all rewardblocks to disk.");
			mRewardBlockManager.saveData();
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

	/** - TODO: SpigetUpdater crashing */
	/** public SpigetUpdater getSpigetUpdater() {
		return mSpigetUpdater;
	}*/

	public static EconomyManager getEconomyManager() {
		return mEconomyManager;
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

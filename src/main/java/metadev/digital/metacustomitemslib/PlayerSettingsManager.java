package metadev.digital.metacustomitemslib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import metadev.digital.metacustomitemslib.storage.DataStoreException;
import metadev.digital.metacustomitemslib.storage.IDataCallback;
import metadev.digital.metacustomitemslib.storage.UserNotFoundException;

public class PlayerSettingsManager implements Listener {

	private HashMap<UUID, PlayerSettings> mPlayerSettings = new HashMap<UUID, PlayerSettings>();

	private Plugin plugin;

	/**
	 * Constructor for the PlayerSettingsmanager
	 */
	PlayerSettingsManager(Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * Get playerSettings from memory
	 * 
	 * @param offlinePlayer
	 * @return PlayerSettings
	 */
	public PlayerSettings getPlayerSettings(OfflinePlayer offlinePlayer) {
		if (mPlayerSettings.containsKey(offlinePlayer.getUniqueId()))
			return mPlayerSettings.get(offlinePlayer.getUniqueId());
		else {
			PlayerSettings ps;
			try {
				ps = Core.getStoreManager().loadPlayerSettings(offlinePlayer);
			} catch (UserNotFoundException e) {
				String worldgroup = offlinePlayer.isOnline()
						? Core.getWorldGroupManager().getCurrentWorldGroup(offlinePlayer)
						: Core.getWorldGroupManager().getDefaultWorldgroup();
				Core.getMessages().debug("Insert new PlayerSettings for %s to database.", offlinePlayer.getName());
				ps = new PlayerSettings(offlinePlayer, worldgroup, Core.getConfigManager().learningMode, false, null,
						null, System.currentTimeMillis(), System.currentTimeMillis());
				setPlayerSettings(ps);
				return ps;
			} catch (DataStoreException e) {
				Core.getMessages().debug("Error reading %s's data from the database", offlinePlayer.getName());
				return new PlayerSettings(offlinePlayer);
			}
			mPlayerSettings.put(offlinePlayer.getUniqueId(), ps);
			return ps;
		}

	}

	/**
	 * Store playerSettings in memory
	 * 
	 * @param playerSettings
	 */
	public void setPlayerSettings(PlayerSettings playerSettings) {
		mPlayerSettings.put(playerSettings.getPlayer().getUniqueId(), playerSettings);
		Core.getDataStoreManager().insertPlayerSettings(playerSettings);
	}

	/**
	 * Remove PlayerSettings from Memory
	 * 
	 * @param player
	 */
	public void removePlayerSettings(OfflinePlayer player) {
		Core.getMessages().debug("Removing %s from PlayerSettings cache", player.getName());
		mPlayerSettings.remove(player.getUniqueId());
	}

	/**
	 * Read PlayerSettings From database into Memory when player joins
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (!containsKey(player)) {
			Core.getMessages().debug("Loading %s 's player data into memory", player);
			load(player);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		PlayerSettings ps = mPlayerSettings.get(player.getUniqueId());
		ps.setLastKnownWorldGrp(Core.getWorldGroupManager().getCurrentWorldGroup(player));
		setPlayerSettings(ps);
		Core.getMessages().debug("Saving lastKnownWorldGroup: %s",
				Core.getWorldGroupManager().getCurrentWorldGroup(player));
	}

	/**
	 * Load PlayerSettings asynchronously from Database
	 * 
	 * @param offlinePlayer
	 */
	public void load(final OfflinePlayer offlinePlayer) {
		Core.getMessages().debug("Requesting %s's information from the database", offlinePlayer.getName());
		Core.getDataStoreManager().requestPlayerSettings(offlinePlayer, new IDataCallback<PlayerSettings>() {
			@Override
			public void onCompleted(PlayerSettings ps) {
				ps.setLast_logon(System.currentTimeMillis());
				mPlayerSettings.put(offlinePlayer.getUniqueId(), ps);
				if (ps.getTexture() == null || ps.getTexture().equals("")) {
					Core.getMessages().debug("Store %s's skin in CustomItemsLib Skin Cache", offlinePlayer.getName());
				}
			}

			@Override
			public void onError(Throwable error) {
				Bukkit.getConsoleSender().sendMessage(Core.PREFIX + ChatColor.RED + "[ERROR] " + offlinePlayer.getName()
						+ " is new, creating user in database.");
				mPlayerSettings.put(offlinePlayer.getUniqueId(), new PlayerSettings(offlinePlayer));
			}
		});
	}

	/**
	 * Test if PlayerSettings contains data for Player
	 * 
	 * @param player
	 * @return true if player exists in PlayerSettings in Memory
	 */
	public boolean containsKey(final OfflinePlayer player) {
		return mPlayerSettings.containsKey(player.getUniqueId());
	}

	public UUID getPlayerByID(int player_id) {

		Iterator<Entry<UUID, PlayerSettings>> itr = mPlayerSettings.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<UUID, PlayerSettings> set = (Entry<UUID, PlayerSettings>) itr.next();
			if (set.getValue().getPlayerId() == player_id)
				return set.getKey();
		}

		OfflinePlayer offlinePlayer = null;

		try {
			offlinePlayer = Core.getDataStoreManager().getPlayerByPlayerId(player_id);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		}

		if (offlinePlayer != null)
			return offlinePlayer.getUniqueId();
		else {
			// Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BagOfGoldCore][ERROR]
			// Player_ID=" + player_id
			// + " was not found in the database table: mh_PlayerSettings.");

			return null;
		}
	}

}

package one.lindegaard.CustomItemsLib.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import one.lindegaard.CustomItemsLib.Core;

public class BossBarAPICompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	public BossBarAPICompat() {
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender()
					.sendMessage(Core.PREFIX + "Compatibility with BossBarAPI is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.BossBarApi.getName());

			Bukkit.getConsoleSender().sendMessage(Core.PREFIX + "Enabling compatibility with BossBarAPI ("
					+ getBossBarAPI().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getBossBarAPI() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isEnabledInConfig() {
		return Core.getConfigManager().enableIntegrationBossBarAPI;
	}

	public static void setSupported(boolean b) {
		supported = b;
	}

	public static void addBar(Player player, String text) {
		BossBarAPICompatHelper.addBar(player, text);
	}
}

package one.lindegaard.CustomItemsLib.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import one.lindegaard.CustomItemsLib.Core;

public class MobHuntingCompat {

	private Plugin mPlugin;
	private static boolean supported = false;

	public MobHuntingCompat() {
		mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.MobHunting.getName());

		if (mPlugin != null) {
			if (mPlugin.getDescription().getVersion().compareTo("8.1.9") >= 0) {
				Bukkit.getServer().getConsoleSender().sendMessage(Core.PREFIX
						+ "Enabling compatibility with MobHunting (" + mPlugin.getDescription().getVersion() + ")");
				supported = true;
			} else {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(Core.PREFIX_WARNING + "Your current version of MobHunting ("
								+ mPlugin.getDescription().getVersion()
								+ ") is outdated. Please upgrade to 8.1.9 or newer.");
				Bukkit.getPluginManager().disablePlugin(mPlugin);
			}
		} else {
			Bukkit.getServer().getConsoleSender()
					.sendMessage(Core.PREFIX + " MobHunting is not installed on this server");
		}

	}

	public boolean isSupported() {
		return supported;
	}

}

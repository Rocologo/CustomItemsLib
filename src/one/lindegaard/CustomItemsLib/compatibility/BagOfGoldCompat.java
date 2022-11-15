package one.lindegaard.CustomItemsLib.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import one.lindegaard.CustomItemsLib.Core;

public class BagOfGoldCompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	public BagOfGoldCompat() {
		mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.BagOfGold.getName());

		if (mPlugin != null) {
			if (mPlugin.getDescription().getVersion().compareTo("3.0.0.") >= 0) {
				Bukkit.getServer().getConsoleSender().sendMessage(Core.PREFIX
						+ "Enabling compatibility with BagOfGold (" + mPlugin.getDescription().getVersion() + ")");
				supported = true;
			} else {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(Core.PREFIX_ERROR + "Your current version of BagOfGold ("
								+ mPlugin.getDescription().getVersion()
								+ ") is outdated. Please upgrade to 3.1.3 or newer.");
				Bukkit.getPluginManager().disablePlugin(mPlugin);
			}
		} else {
			Bukkit.getServer().getConsoleSender()
					.sendMessage(Core.PREFIX + "CustomItemsLib is not installed on this server");
		}

	}

	public static boolean isSupported() {
		return supported;
	}

	public static Plugin getInstance() {
		return mPlugin;
	}

}

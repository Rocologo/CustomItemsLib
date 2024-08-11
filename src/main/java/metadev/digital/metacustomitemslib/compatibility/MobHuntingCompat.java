package metadev.digital.metacustomitemslib.compatibility;

import metadev.digital.metacustomitemslib.Core;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class MobHuntingCompat {

	private Plugin mPlugin;
	private static boolean supported = false;

	public MobHuntingCompat() {
		mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.MobHunting.getName());

		if (mPlugin != null) {
			if (mPlugin.getDescription().getVersion().compareTo("9.0.7") >= 0) {
				Bukkit.getServer().getConsoleSender().sendMessage(Core.PREFIX
						+ "Enabling compatibility with MobHunting (" + mPlugin.getDescription().getVersion() + ")");
				supported = true;
			} else {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(Core.PREFIX_ERROR + "Your current version of MobHunting ("
								+ mPlugin.getDescription().getVersion()
								+ ") is outdated. Please upgrade to 9.0.7 or newer.");
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

package metadev.digital.metacustomitemslib.compatibility;

import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import metadev.digital.metacustomitemslib.Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TitleManagerCompat {

	private static Plugin mPlugin;
	private static TitleManagerAPI api;
	private static boolean supported = false;

	// https://www.spigotmc.org/resources/titlemanager.1049/

	public TitleManagerCompat() {
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING
					+ "Compatibility with TitleManager is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.TitleManager.getName());
			Bukkit.getConsoleSender().sendMessage(Core.PREFIX
					+ "Enabling compatibility with TitleManager ("
					+ mPlugin.getDescription().getVersion() + ")");
			if (mPlugin.getDescription().getVersion().compareTo("2.2") >= 0)
				api = getTitleManagerAPI();
			else {
				Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING
						+ "You are using an old version of TitleManager. Consider updating.");
			}
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public TitleManagerAPI getTitleManagerAPI() {
		return (TitleManagerAPI) mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isEnabledInConfig() {
		return Core.getConfigManager().enableIntegrationTitleManager;
	}

	public static void setActionBar(Player player, String message) {
		if (supported) {

			if (api != null) {
				api.sendActionbar(player, message);
			} 
			
			//else {
			//	ActionbarTitleObject actionbar = new ActionbarTitleObject(message);
			//	actionbar.send(player);
			//}
		}
	}

	public static void sendTitles(Player player, String title, String subtitle, int fadein, int stay, int fadeout) {
		if (supported) {

			if (api != null) {
				api.sendTitles(player, title, subtitle, fadein, stay, fadeout);

			} 
			
			//else {
			//	TitleObject titleObject = new TitleObject(title, subtitle);
			//	titleObject.send(player);
			//}
		}
	}
}

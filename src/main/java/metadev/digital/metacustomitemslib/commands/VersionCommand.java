package metadev.digital.metacustomitemslib.commands;

import metadev.digital.metacustomitemslib.Core;
import metadev.digital.metacustomitemslib.update.UpdateStatus;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VersionCommand implements ICommand {

	private Core plugin;

	public VersionCommand(Core plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "version";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "ver", "-v" };
	}

	@Override
	public String getPermission() {
		return "customitemslib.version";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { ChatColor.GOLD + label + ChatColor.GREEN + " version" + ChatColor.WHITE
				+ " - to get the version number" };
	}

	@Override
	public String getDescription() {
		return Core.getMessages().getString("core.commands.version.description");
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		Core.getMessages().senderSendMessage(sender,
				ChatColor.GREEN + Core.getMessages().getString("core.commands.version.currentversion",
						"currentversion", Core.getInstance().getDescription().getVersion()));
		if (plugin.getSpigetUpdater().getUpdateAvailable() == UpdateStatus.AVAILABLE)
			Core.getMessages().senderSendMessage(sender,
					ChatColor.GREEN + Core.getMessages().getString("core.commands.version.newversion",
							"newversion", plugin.getSpigetUpdater().getNewDownloadVersion()));
		else if (sender.hasPermission("bagofgold.update"))
			plugin.getSpigetUpdater().checkForUpdate(sender, true, false);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}

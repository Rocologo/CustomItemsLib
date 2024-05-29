package metadev.digital.metacustomitemslib.commands;

import metadev.digital.metacustomitemslib.Core;
import metadev.digital.metacustomitemslib.update.UpdateStatus;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UpdateCommand implements ICommand {

	private Core plugin;

	public UpdateCommand(Core plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "update";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "customitemslib.update";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { ChatColor.GOLD + label + ChatColor.WHITE + " - to download and update CustomItemsLib." };
	}

	@Override
	public String getDescription() {
		return Core.getMessages().getString("bagofgold.commands.update.description");
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
		if (plugin.getSpigetUpdater().getUpdateAvailable() == UpdateStatus.AVAILABLE)
			plugin.getSpigetUpdater().checkForUpdate(sender, false, true);
		else if (plugin.getSpigetUpdater().getUpdateAvailable() == UpdateStatus.RESTART_NEEDED)
			Core.getMessages().senderSendMessage(sender,
					ChatColor.GREEN + Core.getMessages().getString("core.commands.update.complete"));
		else
			plugin.getSpigetUpdater().checkForUpdate(sender, false, true);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}

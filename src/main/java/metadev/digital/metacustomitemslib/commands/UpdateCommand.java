package metadev.digital.metacustomitemslib.commands;

import metadev.digital.metacustomitemslib.Core;

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
		return new String[] { ChatColor.GOLD + label };
	}

	@Override
	public String getDescription() {
		return Core.getMessages().getString("core.commands.update.description");
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		Core.getMessages().senderSendMessage(sender, ChatColor.GREEN + plugin.getUpdater().processCheckResultInChat());
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}

package metadev.digital.metacustomitemslib.commands;

import metadev.digital.metacustomitemslib.Core;

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
		return new String[] { ChatColor.GOLD + label };
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
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}

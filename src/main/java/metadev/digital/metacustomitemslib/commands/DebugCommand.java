package metadev.digital.metacustomitemslib.commands;

import metadev.digital.metacustomitemslib.Core;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DebugCommand implements ICommand {

	private Core plugin;

	public DebugCommand(Core plugin) {
		this.plugin = plugin;
	}

	// Used case
	// /cil debug - args.length = 0 || arg[0]=""

	@Override
	public String getName() {
		return "debug";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "debugmode" };
	}

	@Override
	public String getPermission() {
		return "customitemslib.debug";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { ChatColor.GOLD + label};
	}

	@Override
	public String getDescription() {
		return Core.getMessages().getString("core.commands.debug.description");
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
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		if (args.length == 0) {
			toggledebugMode(sender);
			return true;
		}
		return false;
	}

	private void toggledebugMode(CommandSender sender) {
		boolean debug = Core.getConfigManager().debug;
		if (debug) {
			Core.getConfigManager().debug = false;
			Core.getMessages().senderSendMessage(sender,
					Core.PREFIX + Core.getMessages().getString("core.commands.debug.disabled"));
			Core.getConfigManager().saveConfig();
		} else {
			Core.getConfigManager().debug = true;
			Core.getMessages().senderSendMessage(sender,
					Core.PREFIX + Core.getMessages().getString("core.commands.debug.enabled"));
			Core.getConfigManager().saveConfig();
		}

	}

}

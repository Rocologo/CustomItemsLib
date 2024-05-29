package metadev.digital.metacustomitemslib.commands;

import metadev.digital.metacustomitemslib.Core;
import metadev.digital.metacustomitemslib.Tools;
import metadev.digital.metacustomitemslib.messages.Messages;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand implements ICommand {

	private Core plugin;

	public ReloadCommand(Core plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "customitemslib.reload";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { ChatColor.GOLD + label + ChatColor.WHITE + " - to reload CustomItemLibs configuration." };
	}

	@Override
	public String getDescription() {
		return Core.getMessages().getString("core.commands.reload.description");
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

		long starttime = System.currentTimeMillis();
		int i = 1;
		while (Core.getDataStoreManager().isRunning() && (starttime + 10000 > System.currentTimeMillis())) {
			if (((int) (System.currentTimeMillis() - starttime)) / 1000 == i) {
				Core.getMessages().debug("saving data (%s)");
				i++;
			}
		}

		Core.setMessages(new Messages(plugin));

		if (Core.getConfigManager().loadConfig()) {
			Core.getWorldGroupManager().load();

			int n = Tools.getOnlinePlayersAmount();
			if (n > 0) {
				Core.getMessages().debug("Reloading %s PlayerSettings from the database", n);
				for (Player player : Tools.getOnlinePlayers()) {
					Core.getPlayerSettingsManager().load(player);
				}
			}

			Core.getMessages().senderSendMessage(sender,
					ChatColor.GREEN + Core.getMessages().getString("core.commands.reload.reload-complete"));

		} else
			Core.getMessages().senderSendMessage(sender,
					ChatColor.RED + Core.getMessages().getString("core.commands.reload.reload-error"));

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}

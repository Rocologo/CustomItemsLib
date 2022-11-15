package one.lindegaard.CustomItemsLib.messages;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.entity.Player;

public class MessageManager {

	private HashMap<Player, Long> lastMessage = new HashMap<Player, Long>();
	private HashMap<Player, LinkedList<String>> messageLog = new HashMap<Player, LinkedList<String>>();

	public MessageManager() {

	}

	public long getLastMessageTime(Player player) {
		if (lastMessage.containsKey(player))
			return lastMessage.get(player);
		else
			return 0;
	}

	public void setLastMessageTime(Player player, long time) {
		lastMessage.put(player, time);
	}

	public void clearMessageLog(Player player) {
		messageLog.put(player, new LinkedList<>());
	}

	public LinkedList<String> getMessages(Player player) {
		return messageLog.get(player);
	}

	public void addMessage(Player player, String message) {
		LinkedList<String> messages = getMessages(player);
		messages.addLast(message);
		if (messages.size() > 9)
			messages.removeFirst();
		messageLog.put(player, messages);
	}

}

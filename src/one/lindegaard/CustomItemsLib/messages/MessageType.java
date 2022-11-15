package one.lindegaard.CustomItemsLib.messages;

import one.lindegaard.CustomItemsLib.messages.MessageType;

public enum MessageType {
	Chat("Chat"), ActionBar("ActionBar"), BossBar("BossBar"), Title("Title"), Subtitle("Subtitle"), None("None");

	private final String name;

	private MessageType(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName != null) && name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public MessageType valueOf(int id) {
		return MessageType.values()[id];
	}

	public String getName() {
		return name;
	}

}

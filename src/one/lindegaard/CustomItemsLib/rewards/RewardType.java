package one.lindegaard.CustomItemsLib.rewards;

import java.util.UUID;

public enum RewardType {

	BAGOFGOLD(1, "BagOfGold", "b3f74fad-429f-4801-9e31-b8879cbae96f"),
	KILLED(2, "Killer", "2351844f-f400-4fa4-9642-35169c5b048a"),
	KILLER(3, "Killed", "3ffe9c3b-0445-4c35-a952-c2aaf5aeac76"),
	ITEM(4, "Item", "d81f1076-c91c-44c0-98c3-02a2ee88aa97");

	private int n;
	private String rewardType;
	UUID uuid;

	private RewardType(int n, String rewardType, String uuid) {
		this.n = n;
		this.rewardType = rewardType;
		this.uuid = UUID.fromString(uuid);
	}

	public int getInt() {
		return n;
	}

	public String getType() {
		return name();
	}

	public String getRewardType() {
		return rewardType;
	}

	public String getUUID() {
		return uuid.toString();
	}

	public RewardType fromString(String rewardType) {
		for (RewardType type : values()) {
			if (type.getType().equalsIgnoreCase(rewardType))
				return type;
		}
		return null;
	}

}

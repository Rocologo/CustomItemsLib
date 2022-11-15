package one.lindegaard.CustomItemsLib.rewards;

import org.bukkit.Location;

public class RewardBlock {

	Location location;
	Reward reward;
	
	public RewardBlock(Location location, Reward reward) {
		this.location=location;
		this.reward=reward;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return the reward
	 */
	public Reward getReward() {
		return reward;
	}

	/**
	 * @param reward the reward to set
	 */
	public void setReward(Reward reward) {
		this.reward = reward;
	}
	
	public boolean equals(RewardBlock rewardBlock) {
		return reward.equals(rewardBlock.getReward()) && location.equals(rewardBlock.getLocation());
	}
	
	public String toString() {
		return "{RewardBlock: {Location=" + location.toString() + ", Reward=" + reward.toString()+ "}}";
	}
}

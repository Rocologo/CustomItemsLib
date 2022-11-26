package one.lindegaard.CustomItemsLib.rewards;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import one.lindegaard.CustomItemsLib.Core;
import one.lindegaard.CustomItemsLib.Tools;
import one.lindegaard.CustomItemsLib.mobs.MobType;

public class CoreRewardManager {

	Plugin plugin;
	private HashMap<Integer, Double> droppedMoney = new HashMap<Integer, Double>();

	public CoreRewardManager(Plugin plugin) {
		this.plugin = plugin;
	}

	public HashMap<Integer, Double> getDroppedMoney() {
		return droppedMoney;
	}

	public boolean isBagOfGoldStyle() {
		return Core.getConfigManager().rewardItemtype.equalsIgnoreCase("SKULL")
				|| Core.getConfigManager().rewardItemtype.equalsIgnoreCase("ITEM")
				|| Core.getConfigManager().rewardItemtype.equalsIgnoreCase("KILLED")
				|| Core.getConfigManager().rewardItemtype.equalsIgnoreCase("KILLER");
	}

	public boolean isGringottsStyle() {
		return Core.getConfigManager().rewardItemtype.equals("GRINGOTTS_STYLE");
	}

	public boolean canPickupMoney(Player player) {
		if (player.getGameMode() == GameMode.SPECTATOR)
			return false;
		else if (player.getInventory().firstEmpty() != -1)
			return true;
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward rewardInSlot = Reward.getReward(is);
				int amount = is.getAmount();
				if (rewardInSlot.isMoney()) {
					if (rewardInSlot.getMoney() * amount < Core.getConfigManager().limitPerBag)
						return true;
				}
			}
		}
		return false;
	}

	public double addBagOfGoldMoneyToPlayer(Player player, double amount) {
		boolean found = false;
		double moneyLeftToGive = amount;
		double addedMoney = 0;
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward rewardInSlot = Reward.getReward(is);
				if (rewardInSlot.isMoney()) {
					if (rewardInSlot.checkHash()) {
						if (rewardInSlot.getMoney() < Core.getConfigManager().limitPerBag) {
							double space = Core.getConfigManager().limitPerBag - rewardInSlot.getMoney();
							if (space > moneyLeftToGive) {
								addedMoney = addedMoney + moneyLeftToGive;
								rewardInSlot.setMoney(rewardInSlot.getMoney() + moneyLeftToGive);
								moneyLeftToGive = 0;
							} else {
								addedMoney = addedMoney + space;
								rewardInSlot.setMoney(Core.getConfigManager().limitPerBag);
								moneyLeftToGive = moneyLeftToGive - space;
							}
							if (rewardInSlot.getMoney() == 0)
								player.getInventory().clear(slot);
							else {
								is = Reward.setDisplayNameAndHiddenLores(is, rewardInSlot);
							}
							Core.getMessages().debug(
									"Added %s to %s's item in slot %s, new value is %s (addBagOfGoldPlayer)",
									Tools.format(amount), player.getName(), slot,
									Tools.format(rewardInSlot.getMoney()));
							if (moneyLeftToGive <= 0) {
								found = true;
								break;
							}
						}
					} else {
						// Hash is wrong
						Bukkit.getConsoleSender()
								.sendMessage(Core.PREFIX_WARNING + player.getName()
										+ " has tried to change the value of a BagOfGold Item. Value set to 0!(1)");
						rewardInSlot.setMoney(0);
						is = Reward.setDisplayNameAndHiddenLores(is, rewardInSlot);
					}
				}
			}
		}
		if (!found) {
			while (Tools.round(moneyLeftToGive) > 0 && canPickupMoney(player)) {
				double nextBag = 0;
				if (moneyLeftToGive > Core.getConfigManager().limitPerBag) {
					nextBag = Core.getConfigManager().limitPerBag;
					moneyLeftToGive = moneyLeftToGive - nextBag;
				} else {
					nextBag = moneyLeftToGive;
					moneyLeftToGive = 0;
				}
				int firstEmptySlot = player.getInventory().firstEmpty();
				if (firstEmptySlot == -1)
					dropBagOfGoldMoneyOnGround(player, null, player.getLocation(), Tools.round(nextBag));
				else {
					addedMoney = addedMoney + nextBag;
					ItemStack is;
					if (Core.getConfigManager().rewardItemtype.equalsIgnoreCase("SKULL"))
						is = new CoreCustomItems(plugin).getCustomtexture(
								new Reward(Core.getConfigManager().bagOfGoldName, Tools.round(nextBag),
										RewardType.BAGOFGOLD, UUID.fromString(RewardType.BAGOFGOLD.getUUID())),
								Core.getConfigManager().skullTextureValue,
								Core.getConfigManager().skullTextureSignature);
					else {
						is = new ItemStack(Material.valueOf(Core.getConfigManager().rewardItem), 1);
						is = Reward.setDisplayNameAndHiddenLores(is, new Reward(Core.getConfigManager().bagOfGoldName,
								Tools.round(nextBag), RewardType.ITEM, null));
					}

					player.getInventory().setItem(firstEmptySlot, is);
				}
			}
		}
		if (moneyLeftToGive > 0)
			dropBagOfGoldMoneyOnGround(player, null, player.getLocation(), moneyLeftToGive);
		return addedMoney;
	}

	public double removeBagOfGoldFromPlayer(Player player, double amount) {
		double taken = 0;
		double toBeTaken = Tools.round(amount);
		for (int slot = player.getInventory().getSize()-1; slot >= 0; slot--) {
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward reward = Reward.getReward(is);
				if (reward.checkHash()) {
					if (reward.isMoney()) {
						double saldo = Tools.round(reward.getMoney());
						if (saldo > toBeTaken) {
							reward.setMoney(Tools.round(saldo - toBeTaken));
							is = Reward.setDisplayNameAndHiddenLores(is, reward);
							player.getInventory().setItem(slot, is);
							taken = taken + toBeTaken;
							toBeTaken = 0;
							return Tools.round(taken);
						} else {
							player.getInventory().clear(slot);
							taken = taken + saldo;
							toBeTaken = toBeTaken - saldo;
						}
						if (reward.getMoney() == 0)
							player.getInventory().clear(slot);
					} 
				} else {
					// Hash is wrong
					Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING + player.getName()
							+ " has tried to change the value of a BagOfGold Item. Value set to 0!");
					reward.setMoney(0);
					is = Reward.setDisplayNameAndHiddenLores(is, reward);
				}
			}

		}
		return taken;
	}

	/**
	 * Dropes an Reward Item at the specified location
	 * 
	 * @param location - where the Item is dropped.
	 * @param reward   - the reward to be dropped
	 */
	public void dropRewardOnGround(Location location, Reward reward) {
		if (reward.isBagOfGoldReward()) {
			dropBagOfGoldMoneyOnGround(null, null, location, reward.getMoney());
		} else if (reward.isItemReward()) {
			ItemStack is = new ItemStack(Material.valueOf(Core.getConfigManager().rewardItem), 1);
			Item item = location.getWorld().dropItem(location, is);
			getDroppedMoney().put(item.getEntityId(), reward.getMoney());
		} else if (reward.isKilledHeadReward()) {
			MobType mob = MobType.getMobType(reward.getSkinUUID());
			if (mob != null) {
				ItemStack is = new CoreCustomItems(plugin).getCustomHead(mob, reward.getDisplayName(), 1,
						reward.getMoney(), reward.getSkinUUID());
				Item item = location.getWorld().dropItem(location, is);
				item.setMetadata(Reward.MH_REWARD_DATA_NEW, new FixedMetadataValue(plugin, new Reward(reward)));
				getDroppedMoney().put(item.getEntityId(), reward.getMoney());
			}
		} else if (reward.isKillerHeadReward()) {
			ItemStack is = new CoreCustomItems(plugin).getPlayerHead(reward.getSkinUUID(), reward.getDisplayName(), 1,
					reward.getMoney());
			Item item = location.getWorld().dropItem(location, is);
			item.setMetadata(Reward.MH_REWARD_DATA_NEW, new FixedMetadataValue(plugin, new Reward(reward)));
			getDroppedMoney().put(item.getEntityId(), reward.getMoney());
		} else {
			Bukkit.getConsoleSender()
					.sendMessage(Core.PREFIX + ChatColor.RED + " Unhandled reward type in CoreRewardManager.");
		}
	}

	public void dropBagOfGoldMoneyOnGround(Player player, Entity killedEntity, Location location, double money) {
		Item item = null;
		double moneyLeftToDrop = Tools.ceil(money);
		ItemStack is;
		UUID skinuuid = null;
		RewardType rewardType;
		double nextBag = 0;
		while (moneyLeftToDrop > 0) {
			if (moneyLeftToDrop > Core.getConfigManager().limitPerBag) {
				nextBag = Core.getConfigManager().limitPerBag;
				moneyLeftToDrop = Tools.round(moneyLeftToDrop - nextBag);
			} else {
				nextBag = Tools.round(moneyLeftToDrop);
				moneyLeftToDrop = 0;
			}

			if (Core.getConfigManager().rewardItemtype.equalsIgnoreCase("KILLED")) {
				MobType mob = MobType.getMobType(killedEntity);
				rewardType = RewardType.KILLED;
				skinuuid = mob.getSkinUUID();
				is = new CoreCustomItems(plugin).getCustomHead(mob, mob.getFriendlyName(), 1, money, skinuuid);

			} else if (Core.getConfigManager().rewardItemtype.equalsIgnoreCase("SKULL")) {
				rewardType = RewardType.BAGOFGOLD;
				skinuuid = UUID.fromString(RewardType.BAGOFGOLD.getUUID());
				is = new CoreCustomItems(plugin).getCustomtexture(
						new Reward(Core.getConfigManager().bagOfGoldName.trim(), money, rewardType, skinuuid),
						Core.getConfigManager().skullTextureValue, Core.getConfigManager().skullTextureSignature);
			} else if (Core.getConfigManager().rewardItemtype.equalsIgnoreCase("KILLER")) {
				rewardType = RewardType.KILLER;
				skinuuid = player.getUniqueId();
				is = new CoreCustomItems(plugin).getPlayerHead(player.getUniqueId(), player.getName(), 1, money);
			} else { // ITEM
				rewardType = RewardType.ITEM;
				skinuuid = null;
				is = new ItemStack(Material.valueOf(Core.getConfigManager().rewardItem), 1);
			}

			Reward reward = new Reward(
					ChatColor.valueOf(Core.getConfigManager().rewardTextColor) + Core.getConfigManager().bagOfGoldName,
					nextBag, rewardType, skinuuid);
			is = Reward.setDisplayNameAndHiddenLores(is, reward);

			item = location.getWorld().dropItem(location, is);

			if (item != null) {
				getDroppedMoney().put(item.getEntityId(), nextBag);
				item.setMetadata(Reward.MH_REWARD_DATA_NEW, new FixedMetadataValue(plugin, new Reward(reward)));
				item.setCustomName(is.getItemMeta().getDisplayName());
				item.setCustomNameVisible(Core.getConfigManager().showCustomDisplayname);
				if (player != null)
					Core.getMessages().debug("%s dropped %s on the ground as item %s (# of rewards=%s)(3)",
							player.getName(), Tools.format(nextBag), Core.getConfigManager().rewardItemtype,
							getDroppedMoney().size());
				else
					Core.getMessages().debug("a %s(%s) was dropped on the ground as item %s (# of rewards=%s)(3)",
							Core.getConfigManager().rewardItemtype, Tools.format(nextBag),
							Core.getConfigManager().rewardItemtype, getDroppedMoney().size());

			}
		}
	}

}

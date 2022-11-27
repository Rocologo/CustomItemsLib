package one.lindegaard.CustomItemsLib.rewards;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.compatibility.ShopkeepersCompat;
import one.lindegaard.CustomItemsLib.Core;
import one.lindegaard.CustomItemsLib.Tools;
import one.lindegaard.CustomItemsLib.compatibility.BagOfGoldCompat;
import one.lindegaard.CustomItemsLib.server.Servers;

public class CoreRewardListeners implements Listener {

	private Plugin plugin;

	public CoreRewardListeners(Plugin plugin) {
		this.plugin = plugin;
	}

	// **********************************************************************************************************
	// Events
	// **********************************************************************************************************

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {
		if (event.isCancelled())
			return;

		List<Block> changedBlocks = event.getBlocks();
		if (!changedBlocks.isEmpty())
			for (Block b : changedBlocks) {
				if (Reward.isReward(b)) {
					Core.getMessages().debug("CoreRewardListeners: Is not possible to move a Reward with a Piston");
					event.setCancelled(true);
					return;
				}
			}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player.getOpenInventory() != null) {
			if (player.getOpenInventory().getCursor() == null)
				return;
			if (!Reward.isReward(player.getOpenInventory().getCursor()))
				return;
			player.getOpenInventory().setCursor(null);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
		// This happens when an item moves from one Inventory to another. Ex from one
		// Hopper
		// to another Hopper.
		if (event.isCancelled())
			return;

		ItemStack is = event.getItem();
		if (Reward.isReward(is)) {
			Reward reward = Reward.getReward(is);
			Core.getMessages().debug(
					"CoreRewardListeners: onInventoryMoveItemEvent: a %s was moved from %s to %s. The Initiator was a %s at %s",
					reward.getDisplayName(), event.getSource().getType(), event.getDestination().getType(),
					event.getInitiator().getType(), event.getInitiator().getLocation().toString());

			// TODO: The BagOfGold in the Destination inventory should be merged.

		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDespawnRewardEvent(ItemDespawnEvent event) {
		if (event.isCancelled())
			return;

		if (Reward.isReward(event.getEntity())
				&& Core.getCoreRewardManager().getDroppedMoney().containsKey(event.getEntity().getEntityId())) {

			Core.getCoreRewardManager().getDroppedMoney().remove(event.getEntity().getEntityId());
			if (event.getEntity().getLastDamageCause() != null)
				Core.getMessages().debug("CoreRewardListeners: The reward was destroyed by %s",
						event.getEntity().getLastDamageCause().getCause());
			else
				Core.getMessages().debug("CoreRewardListeners: The reward despawned (# of rewards left=%s)",
						Core.getCoreRewardManager().getDroppedMoney().size());

		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerDropReward(PlayerDropItemEvent event) {
		if (event.isCancelled())
			return;

		Item item = event.getItemDrop();
		Player player = event.getPlayer();

		if (Reward.isFakeReward(item)) {
			player.sendMessage(Core.PREFIX_WARNING + "This was a FAKE reward with no value.");
			return;
		}

		if (Reward.isReward(item)) {
			Reward reward = Reward.getReward(item);
			if (!reward.checkHash()) {
				Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING + player.getName()
						+ " has tried to change the value of a BagOfGold Item. Value set to 0!(5)");
				reward.setMoney(0);
				ItemStack is = Reward.setDisplayNameAndHiddenLores(item.getItemStack(), reward);
				item.setItemStack(is);
			}
			if (reward.isMoney()) {
				int amount = item.getItemStack().getAmount();
				double money = reward.getMoney() * amount;
				Core.getCoreRewardManager().getDroppedMoney().put(item.getEntityId(), money);
				if (money == 0) {
					Core.getMessages().debug("%s dropped a %s (# of rewards left=%s)(1)", player.getName(),
							reward.getDisplayName() != null ? reward.getDisplayName()
									: Core.getConfigManager().bagOfGoldName,
							Core.getCoreRewardManager().getDroppedMoney().size());
				} else {
					Core.getMessages().debug("%s dropped %s %s. (# of rewards left=%s)(2)", player.getName(),
							Tools.format(money), reward.getDisplayName(),
							Core.getCoreRewardManager().getDroppedMoney().size());
					if (!Core.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
						Core.getMessages().playerActionBarMessageQueue(player, Core.getMessages().getString(
								"core.moneydrop", "money", Tools.format(money), "rewardname",
								ChatColor.valueOf(Core.getConfigManager().rewardTextColor) + reward.getDisplayName()));
					if (Reward.isReward(player.getItemOnCursor())) {
						Core.getMessages().debug("%s dropped %s %s from the PlayerInventory", player.getName(), money,
								reward.getDisplayName());
					} else {
						// when dropping from the quickbar using Q key
						Core.getMessages().debug("%s dropped %s %s using Q key", player.getName(), money,
								reward.getDisplayName());
						if (BagOfGoldCompat.isSupported())
							BagOfGold.getInstance().getRewardManager().removeMoneyFromPlayerBalance(player, money);
					}
				}
				reward.setMoney(money);
				item.setMetadata(Reward.MH_REWARD_DATA_NEW, new FixedMetadataValue(plugin, reward));
				ItemStack is = Reward.setDisplayNameAndHiddenLores(item.getItemStack(), reward);
				is.setAmount(1);
				item.setItemStack(is);
				item.setCustomName(is.getItemMeta().getDisplayName());
				item.setCustomNameVisible(Core.getConfigManager().showCustomDisplayname);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRewardBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		ItemStack is = event.getItemInHand();
		Block block = event.getBlockPlaced();

		if (Reward.isFakeReward(is)) {
			player.sendMessage(Core.PREFIX_WARNING + "This was a FAKE reward with no value.");
			return;
		}

		if (Reward.isReward(is)) {
			Reward reward = Reward.getReward(is);
			if (reward.checkHash()) {
				if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
					// Duplication not allowed
					reward.setMoney(0);
				}
				Core.getMessages().debug("%s placed a reward block: %s", player.getName(),
						ChatColor.stripColor(reward.toString()));
				reward.setUniqueID(Core.getRewardBlockManager().getNextID());
				Core.getRewardBlockManager().addReward(block, reward);
				if (BagOfGoldCompat.isSupported() && reward.isMoney()) {
					BagOfGold.getInstance().getRewardManager().removeMoneyFromPlayerBalance(player, reward.getMoney());
				}
			} else {
				Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING + player.getName()
						+ " has tried to change the value of a BagOfGold Item. Value set to 0!(6)");
				reward.setMoney(0);
				is = Reward.setDisplayNameAndHiddenLores(is, reward);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		Inventory inventory = event.getInventory();
		if (inventory.getType() == InventoryType.CRAFTING) {
			ItemStack helmet = player.getEquipment().getHelmet();

			if (Reward.isFakeReward(helmet)) {
				event.getPlayer().getEquipment().setHelmet(new ItemStack(Material.AIR));
				return;
			}

			if (Reward.isReward(helmet)) {
				Reward reward = Reward.getReward(helmet);
				if (reward.checkHash()) {
					if (reward.isBagOfGoldReward()) {
						Core.getMessages().playerActionBarMessageQueue(player,
								Core.getMessages().getString("core.learn.rewards.no-helmet"));
						event.getPlayer().getEquipment().setHelmet(new ItemStack(Material.AIR));
						if (Tools.round(reward.getMoney()) != Tools.round(
								Core.getCoreRewardManager().addBagOfGoldMoneyToPlayer(player, reward.getMoney())))
							Core.getCoreRewardManager().dropBagOfGoldMoneyOnGround(player, null, player.getLocation(),
									reward.getMoney());
					} else {
						event.getPlayer().getEquipment().setHelmet(new ItemStack(Material.AIR));
						player.getWorld().dropItem(player.getLocation(), helmet);
					}
				} else {
					Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING + player.getName()
							+ " has tried to change the value of a BagOfGold Item. Value set to 0!(8)");
					reward.setMoney(0);
					helmet = Reward.setDisplayNameAndHiddenLores(helmet, reward);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectileHitRewardEvent(ProjectileHitEvent event) {
		if (event.isCancelled())
			return;

		Projectile projectile = event.getEntity();
		Entity targetEntity = null;
		Iterator<Entity> nearby = projectile.getNearbyEntities(1, 1, 1).iterator();
		while (nearby.hasNext()) {
			targetEntity = nearby.next();

			if (Reward.isReward(targetEntity)) {
				if (Core.getCoreRewardManager().getDroppedMoney().containsKey(targetEntity.getEntityId()))
					Core.getCoreRewardManager().getDroppedMoney().remove(targetEntity.getEntityId());
				targetEntity.remove();
				Core.getMessages().debug("The reward was hit by %s and removed. (# of rewards left=%s)",
						projectile.getType(), Core.getCoreRewardManager().getDroppedMoney().size());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryPickupRewardEvent(InventoryPickupItemEvent event) {
		if (event.isCancelled())
			return;

		Item item = event.getItem();
		if (!Reward.isReward(item))
			return;

		if (Core.getConfigManager().denyHoppersToPickUpRewards
				&& event.getInventory().getType() == InventoryType.HOPPER) {
			event.setCancelled(true);
		} else {
			Core.getMessages().debug("The reward was picked up by %s", event.getInventory().getType());
			if (Core.getCoreRewardManager().getDroppedMoney().containsKey(item.getEntityId()))
				Core.getCoreRewardManager().getDroppedMoney().remove(item.getEntityId());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMoveOverRewardEvent(PlayerMoveEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();

		if (player.getInventory().firstEmpty() != -1)
			return;

		Iterator<Entity> entityList = ((Entity) player).getNearbyEntities(1, 1, 1).iterator();
		while (entityList.hasNext()) {
			Entity entity = entityList.next();
			if (!(entity instanceof Item))
				continue;

			Item item = (Item) entity;

			if (Reward.isFakeReward(item)) {
				player.sendMessage(Core.PREFIX_WARNING + "This was a FAKE reward and it was removed.");
				item.remove();
				return;
			}

			if (Reward.isReward(item) && Core.getCoreRewardManager().canPickupMoney(player)) {
				if (Core.getCoreRewardManager().getDroppedMoney().containsKey(entity.getEntityId())) {
					Core.getCoreRewardManager().getDroppedMoney().remove(entity.getEntityId());
					Reward reward = Reward.getReward(item);
					if (reward.checkHash()) {
						if (BagOfGoldCompat.isSupported() && reward.isMoney()) {
							double addedMoney = Core.getCoreRewardManager().addBagOfGoldMoneyToPlayer(player,
									reward.getMoney());
							if (addedMoney > 0) {
								PlayerBalance ps = BagOfGold.getInstance().getPlayerBalanceManager()
										.getPlayerBalance(player);
								ps.setBalance(Tools.round(ps.getBalance() + addedMoney));
								BagOfGold.getInstance().getPlayerBalanceManager().setPlayerBalance(player, ps);
							}

						}
					} else {
						Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING + player.getName()
								+ " has tried to change the value of a BagOfGold Item. Value set to 0!(7)");
						reward.setMoney(0);
						ItemStack is = Reward.setDisplayNameAndHiddenLores(item.getItemStack(), reward);
						item.setItemStack(is);
					}
					item.remove();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRewardBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		if (Reward.isReward(block)) {
			Reward reward = Reward.getReward(block);
			Core.getRewardBlockManager().removeReward(block);
			Core.getCoreRewardManager().dropRewardOnGround(block.getLocation(), reward);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockFromToEvent(BlockFromToEvent event) {
		if (event.isCancelled())
			return;

		Block toBlock = event.getToBlock();
		if (Reward.isReward(toBlock)) {
			Reward reward = Reward.getReward(toBlock);
			Block fromBlock = event.getBlock();
			event.setCancelled(true);
			if (!Core.getConfigManager().denyWaterToBreakRewards) {
				Core.getMessages().debug("BlockFromToEvent: %s broke %s", fromBlock.getType(), reward.getDisplayName());
				Core.getRewardBlockManager().removeReward(toBlock);
				Core.getCoreRewardManager().dropRewardOnGround(toBlock.getLocation(), reward);
			}
		}
	}

	// OBS BlockBurnEvent can be used to deny LAVA to burn Rewards.

	private boolean isInventoryAllowed(Inventory inv) {
		List<InventoryType> allowedInventories;
		if (Servers.isMC114OrNewer())
			allowedInventories = Arrays.asList(InventoryType.PLAYER, InventoryType.BARREL, InventoryType.ANVIL,
					InventoryType.CHEST, InventoryType.DISPENSER, InventoryType.DROPPER, InventoryType.ENDER_CHEST,
					InventoryType.HOPPER, InventoryType.SHULKER_BOX, InventoryType.CRAFTING, InventoryType.MERCHANT);
		else if (Servers.isMC19OrNewer())
			allowedInventories = Arrays.asList(InventoryType.PLAYER, InventoryType.ANVIL, InventoryType.CHEST,
					InventoryType.DISPENSER, InventoryType.DROPPER, InventoryType.ENDER_CHEST, InventoryType.HOPPER,
					InventoryType.SHULKER_BOX, InventoryType.CRAFTING);
		else // MC 1.8
			allowedInventories = Arrays.asList(InventoryType.PLAYER, InventoryType.ANVIL, InventoryType.CHEST,
					InventoryType.DISPENSER, InventoryType.DROPPER, InventoryType.ENDER_CHEST, InventoryType.HOPPER,
					InventoryType.CRAFTING);
		if (inv != null)
			return allowedInventories.contains(inv.getType());
		else
			return true;
	}

	// **********************************************************************************************************
	// InventoryInteractEvent
	//
	// Direct Known Subclasses: InventoryClickEvent, InventoryDragEvent,
	// TradeSelectEvent
	// **********************************************************************************************************

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClickReward(InventoryClickEvent event) {

		if (event.isCancelled() || event.getInventory() == null)
			return;

		InventoryAction action = event.getAction();
		if (action == InventoryAction.NOTHING)
			return;

		ClickType clickType = event.getClick();

		Player player = (Player) event.getWhoClicked();

		ItemStack isCurrentSlot = event.getCurrentItem() != null ? event.getCurrentItem().clone() : null;
		ItemStack isCursor = event.getCursor() != null ? event.getCursor().clone() : null;
		ItemStack isNumberKey = clickType == ClickType.NUMBER_KEY
				? event.getWhoClicked().getInventory().getItem(event.getHotbarButton())
				: event.getCurrentItem();
		ItemStack isSwapOffhand = clickType == ClickType.SWAP_OFFHAND
				? event.getWhoClicked().getInventory().getItem(EquipmentSlot.OFF_HAND)
				: event.getCurrentItem();

		if (Reward.isFakeReward(isCurrentSlot)) {
			isCurrentSlot.setType(Material.AIR);
			isCurrentSlot.setAmount(0);
			player.getInventory().clear(event.getSlot());
			return;
		}
		if (Reward.isFakeReward(isCursor)) {
			isCursor.setType(Material.AIR);
			isCursor.setAmount(0);
			return;
		}
		if (Reward.isFakeReward(isNumberKey)) {
			isNumberKey.setType(Material.AIR);
			isNumberKey.setAmount(0);
			return;
		}
		if (Reward.isFakeReward(isSwapOffhand)) {
			isSwapOffhand.setType(Material.AIR);
			isSwapOffhand.setAmount(0);
			return;
		}

		SlotType slotType = event.getSlotType();
		Inventory inventory = event.getInventory();
		Inventory clickedInventory = Servers.isMC113OrNewer() ? event.getClickedInventory() : inventory;

		if (!Reward.isReward(isCurrentSlot) && !Reward.isReward(isCursor) && !Reward.isReward(isNumberKey)
				&& !Reward.isReward(isSwapOffhand))
			return;

		if (Reward.isReward(isCurrentSlot)) {
			Reward reward = Reward.getReward(isCurrentSlot);
			if (!reward.checkHash()) {
				Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING + player.getName()
						+ " has tried to change the value of a BagOfGold Item. Value set to 0!(9)");
				reward.setMoney(0);
				isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCurrentSlot, reward);
			} else if (reward.isMoney() && isCurrentSlot.getAmount() > 1) {
				Core.getMessages().debug("Merge currentslot stack");
				reward.setMoney(reward.getMoney() * isCurrentSlot.getAmount());
				isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCurrentSlot, reward);
				isCurrentSlot.setAmount(1);
				event.setCurrentItem(isCurrentSlot);
			}
		}
		if (Reward.isReward(isCursor)) {
			Reward reward = Reward.getReward(isCursor);
			if (!reward.checkHash()) {
				Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING + player.getName()
						+ " has tried to change the value of a BagOfGold Item. Value set to 0!(10)");
				reward.setMoney(0);
				isCursor = Reward.setDisplayNameAndHiddenLores(isCursor, reward);
			} else if (reward.isMoney() && isCursor.getAmount() > 1) {
				Core.getMessages().debug("Merge cursor stack");
				reward.setMoney(reward.getMoney() * isCursor.getAmount());
				isCursor = Reward.setDisplayNameAndHiddenLores(isCursor, reward);
				isCursor.setAmount(1);
				event.setCursor(isCursor);
			}
		}
		if (Reward.isReward(isNumberKey)) {
			Reward reward = Reward.getReward(isNumberKey);
			if (!reward.checkHash()) {
				Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING + player.getName()
						+ " has tried to change the value of a BagOfGold Item. Value set to 0!(11a)");
				reward.setMoney(0);
				isNumberKey = Reward.setDisplayNameAndHiddenLores(isNumberKey, reward);
			}
		}
		if (Reward.isReward(isSwapOffhand)) {
			Reward reward = Reward.getReward(isSwapOffhand);
			if (!reward.checkHash()) {
				Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING + player.getName()
						+ " has tried to change the value of a BagOfGold Item. Value set to 0!(11b)");
				reward.setMoney(0);
				isSwapOffhand = Reward.setDisplayNameAndHiddenLores(isSwapOffhand, reward);
			}
		}

//		Core.getMessages().debug(
//				"action=%s, InvType=%s, clickedInvType=%s, slottype=%s, slotno=%s, current=%s, cursor=%s, view=%s, keyboardClick=%s, numberKey=%s, swap_hand=%s",
//				action, inventory.getType(), clickedInventory == null ? "null" : clickedInventory.getType(), slotType,
//				event.getSlot(), isCurrentSlot == null ? "null" : isCurrentSlot.getType(),
//				isCursor == null ? "null" : isCursor.getType(), event.getView().getType(),
//				event.getClick().isKeyboardClick(), isNumberKey == null ? "null" : isNumberKey.getType(),
//				isSwapOffhand == null ? "null" : isSwapOffhand.getType());

		if (slotType == SlotType.ARMOR && 
				((Reward.isReward(isCursor) && Reward.getReward(isCursor).isMoney())
				 || (Reward.isReward(isNumberKey) && Reward.getReward(isNumberKey).isMoney()))) {
			if ((action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE
					|| action == InventoryAction.PLACE_SOME || action == InventoryAction.COLLECT_TO_CURSOR)) {
				Core.getMessages().playerActionBarMessageQueue(player,
						Core.getMessages().getString("core.learn.rewards.no-helmet"));
				Core.getMessages().debug("No-Helmet");
				Core.getMessages().debug(
				"action=%s, InvType=%s, clickedInvType=%s, slottype=%s, slotno=%s, current=%s, cursor=%s, view=%s, keyboardClick=%s, numberKey=%s, swap_hand=%s",
				action, inventory.getType(), clickedInventory == null ? "null" : clickedInventory.getType(), slotType,
				event.getSlot(), isCurrentSlot == null ? "null" : isCurrentSlot.getType(),
				isCursor == null ? "null" : isCursor.getType(), event.getView().getType(),
				event.getClick().isKeyboardClick(), isNumberKey == null ? "null" : isNumberKey.getType(),
				isSwapOffhand == null ? "null" : isSwapOffhand.getType());

				event.setCancelled(true);
				return;
			}
		}

		List<SlotType> allowedSlots;
		if (BagOfGoldCompat.isSupported() && ShopkeepersCompat.isSupported())
			allowedSlots = Arrays.asList(SlotType.CONTAINER, SlotType.QUICKBAR, SlotType.OUTSIDE, SlotType.ARMOR,
					SlotType.CRAFTING, SlotType.RESULT);
		else
			allowedSlots = Arrays.asList(SlotType.CONTAINER, SlotType.QUICKBAR, SlotType.OUTSIDE, SlotType.ARMOR);

		if (allowedSlots.contains(slotType)) {
			if (isInventoryAllowed(clickedInventory)) {
				switch (action) {
				case NOTHING, UNKNOWN:
					break;
				case CLONE_STACK:
					if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor)) {
						Core.getMessages().debug("CLONE_STACK: %s its not allowed to clone BagOfGold items",
								player.getName());
						event.setCancelled(true);
					}
					break;
				case COLLECT_TO_CURSOR:
					if (Reward.isReward(isCursor)) {
						Reward cursor = Reward.getReward(isCursor);
						if (cursor.isMoney()) {
							event.setCancelled(true);
							double money_in_hand = cursor.getMoney() * isCursor.getAmount();
							double saldo = Tools.floor(money_in_hand);
							for (int slot = 0; slot < clickedInventory.getSize(); slot++) {
								ItemStack is = clickedInventory.getItem(slot);
								if (Reward.isReward(is)) {
									Reward reward = Reward.getReward(is);
									if ((reward.isMoney()) && reward.getMoney() > 0) {
										saldo = saldo + reward.getMoney() * is.getAmount();
										if (saldo <= Core.getConfigManager().limitPerBag)
											clickedInventory.clear(slot);
										else {
											reward.setMoney(Core.getConfigManager().limitPerBag);
											is = Reward.setDisplayNameAndHiddenLores(is.clone(), reward);
											is.setAmount(1);
											clickedInventory.clear(slot);
											clickedInventory.addItem(is);
											saldo = saldo - Core.getConfigManager().limitPerBag;
										}
									}
								}
							}
							cursor.setMoney(saldo);
							isCursor = Reward.setDisplayNameAndHiddenLores(isCursor.clone(), cursor);
							isCursor.setAmount(1);
							event.setCursor(isCursor);
							Core.getMessages().debug("COLLECT_TO_CURSOR: %s collected %s to the cursor",
									player.getName(), saldo);
							if (clickedInventory.getType() == InventoryType.PLAYER) {
								if (BagOfGoldCompat.isSupported() && cursor.isMoney())
									BagOfGold.getInstance().getRewardManager().removeMoneyFromPlayerBalance(player,
											saldo - money_in_hand);
							}
						} else if (cursor.isKilledHeadReward() || cursor.isKillerHeadReward()) {
							Core.getMessages().debug(
									"COLLECT_TO_CURSOR: Collect to cursor on MobHunting heads is still not implemented");
						}
					}
					break;
				case DROP_ALL_CURSOR, DROP_ONE_CURSOR:
					if (Reward.isReward(isCursor)) {
						Core.getMessages().debug("DROP_ALL_CURSOR, DROP_ONE_CURSOR: %s tried to do a drop BagOfGold.",
								player.getName());
						if (slotType == SlotType.OUTSIDE && Reward.isReward(isCursor)) {
							Reward reward = Reward.getReward(isCursor);
							Core.getMessages().debug(
									"DROP_ALL_CURSOR, DROP_ONE_CURSOR: %s dropped %s BagOfGold outside the inventory",
									player.getName(), reward.getMoney());
							if (BagOfGoldCompat.isSupported() && reward.isMoney())
								BagOfGold.getInstance().getRewardManager().addMoneyToPlayerBalance(player,
										reward.getMoney());
						}
					}
					break;
				case DROP_ALL_SLOT, DROP_ONE_SLOT:
					if (Reward.isReward(isCurrentSlot)) {
						Core.getMessages().debug("DROP_ALL_SLOT, DROP_ONE_SLOT: %s tried to do a drop BagOfGold.",
								player.getName());
						if (slotType == SlotType.OUTSIDE && Reward.isReward(isCurrentSlot)) {
							Reward reward = Reward.getReward(isCursor);
							Core.getMessages().debug("DROP_ALL_SLOT, DROP_ONE_SLOT: %s dropped %s BagOfGold from slot",
									player.getName(), reward.getMoney());
							if (BagOfGoldCompat.isSupported() && reward.isMoney())
								BagOfGold.getInstance().getRewardManager().addMoneyToPlayerBalance(player,
										reward.getMoney());
						}
					}
					break;
				case HOTBAR_SWAP, HOTBAR_MOVE_AND_READD:
					// can be made by using key F or the keyboard and number keys
					if (BagOfGoldCompat.isSupported() && (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor)
							|| Reward.isReward(isNumberKey) || Reward.isReward(isSwapOffhand))) {
						if (clickedInventory.getType() != InventoryType.PLAYER) {
							Reward keyReward = Reward.isReward(isNumberKey) ? Reward.getReward(isNumberKey)
									: new Reward();
							Reward swapHandReward = Reward.isReward(isSwapOffhand) ? Reward.getReward(isSwapOffhand)
									: new Reward();
							if ((clickType == ClickType.NUMBER_KEY && Reward.isReward(isNumberKey))
									|| (clickType == ClickType.SWAP_OFFHAND && Reward.isReward(isSwapOffhand))) {
								BagOfGold.getInstance().getRewardManager().removeMoneyFromPlayerBalance(player,
										keyReward.getMoney() - swapHandReward.getMoney());
							} else {
								BagOfGold.getInstance().getRewardManager().addMoneyToPlayerBalance(player,
										-keyReward.getMoney() + swapHandReward.getMoney());
							}
						}
					}
					break;
				case MOVE_TO_OTHER_INVENTORY:
					// Shift mouse click on the item to move item to another inventory
					if (BagOfGoldCompat.isSupported()
							&& (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor))) {
						Reward reward = Reward.isReward(isCurrentSlot) ? Reward.getReward(isCurrentSlot)
								: Reward.getReward(isCursor);
						if (reward.isMoney()) {
							if (clickedInventory.getType() == InventoryType.PLAYER) {
								if (inventory.getType() == InventoryType.WORKBENCH
										|| inventory.getType() == InventoryType.ENCHANTING
										|| inventory.getType() == InventoryType.ANVIL
										|| inventory.getType() == InventoryType.SMITHING
										|| inventory.getType() == InventoryType.GRINDSTONE
										|| inventory.getType() == InventoryType.CRAFTING) {
									Core.getMessages().debug("%s: this reward can't be moved into %s's crafting slot",
											player.getName(), inventory.getType());
									event.setCancelled(true);
									return;
								} else if (inventory.getType() == InventoryType.FURNACE
										|| inventory.getType() == InventoryType.BLAST_FURNACE
										|| inventory.getType() == InventoryType.BREWING
										|| inventory.getType() == InventoryType.MERCHANT
										|| inventory.getType() == InventoryType.STONECUTTER
										|| inventory.getType() == InventoryType.LOOM
										|| inventory.getType() == InventoryType.CARTOGRAPHY) {
									Core.getMessages().debug(
											"MOVE_TO_OTHER_INVENTORY: %s moved %s %s inside the Player Inventory",
											player.getName(), reward.getMoney(), reward.getDisplayName());
								} else {
									Core.getMessages().debug(
											"MOVE_TO_OTHER_INVENTORY: %s moved %s %s out of the Player Inventory",
											player.getName(), reward.getMoney(), reward.getDisplayName());
									BagOfGold.getInstance().getRewardManager().removeMoneyFromPlayerBalance(player,
											reward.getMoney());
								}
							} else { // CHEST, DISPENSER, DROPPER, ......
								Core.getMessages().debug(
										"MOVE_TO_OTHER_INVENTORY: %s moved %s %s into the Player Inventory",
										player.getName(), reward.getMoney(), reward.getDisplayName());
								BagOfGold.getInstance().getRewardManager().addMoneyToPlayerBalance(player,
										reward.getMoney());
							}
						}
					}
					break;
				case PICKUP_ALL, PICKUP_ONE, PICKUP_SOME:
					if (Reward.isReward(isCurrentSlot)) {
						Reward reward = Reward.getReward(isCurrentSlot);
						int amount = isCurrentSlot.getAmount();
						if (reward.isMoney() && amount > 1) {
							reward.setMoney(reward.getMoney() * amount);
							isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward);
							isCurrentSlot.setAmount(1);
							event.setCurrentItem(isCurrentSlot);
						}
						if (clickedInventory.getType() == InventoryType.PLAYER) {
							Core.getMessages().debug(
									"PICKUP_ALL, PICKUP_ONE, PICKUP_SOME: %s moved BagOfGold (%s) out of Inventory",
									player.getName(), reward.getMoney());
							if (BagOfGoldCompat.isSupported() && reward.isMoney() && slotType != SlotType.ARMOR) {
								Core.getMessages().debug(
										"PICKUP_ALL, PICKUP_ONE, PICKUP_SOME: Remove %s money from %s balance",
										reward.getMoney(), player.getName());
								BagOfGold.getInstance().getRewardManager().removeMoneyFromPlayerBalance(player,
										reward.getMoney());
							}
						}
					}
					break;
				case PICKUP_HALF:
					if (isCursor.getType() == Material.AIR && Reward.isReward(isCurrentSlot)) {
						Reward reward = Reward.getReward(isCurrentSlot);
						if (reward.isMoney()) {
							int amount_of_currentslot = isCurrentSlot.getAmount();
							double currentSlotMoney = Tools.round(reward.getMoney() * amount_of_currentslot / 2);
							double cursorMoney = Tools
									.round((reward.getMoney() * amount_of_currentslot - currentSlotMoney));
							if (cursorMoney >= Core.getConfigManager().minimumReward) {
								event.setCancelled(true);
								reward.setMoney(currentSlotMoney);
								isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward);
								isCurrentSlot.setAmount(1);
								event.setCurrentItem(isCurrentSlot);

								reward.setMoney(cursorMoney);
								isCursor = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward);
								isCursor.setAmount(1);
								event.setCursor(isCursor);

								Core.getMessages().debug("PICKUP_HALF: %s halfed a reward in two (%s,%s)",
										player.getName(), Tools.format(currentSlotMoney), Tools.format(cursorMoney));

								if (clickedInventory.getType() == InventoryType.PLAYER
										|| clickedInventory.getType() == InventoryType.CRAFTING) {
									if (BagOfGoldCompat.isSupported() && reward.isMoney()
											&& slotType != SlotType.ARMOR) {
										Core.getMessages().debug("PICKUP_HALF: Remove %s money from %s balance",
												reward.getMoney(), player.getName());
										BagOfGold.getInstance().getRewardManager().removeMoneyFromPlayerBalance(player,
												reward.getMoney());
									}
								}
							}
						}
					}
					break;
				case PLACE_ONE:
					int amount_of_cursor2 = isCursor != null ? isCursor.getAmount() : 0;
					// int amount_of_currentslot2 = isCurrentSlot != null ?
					// isCurrentSlot.getAmount() : 0;
					if (player.getGameMode() == GameMode.SURVIVAL) {
						if (Reward.isReward(isCursor)) {
							Reward reward = Reward.getReward(isCursor);
							if (reward.isMoney()) {
								event.setCancelled(true);
								double cursorMoney = reward.getMoney() * amount_of_cursor2;

								double bagSize = 0;
								if (cursorMoney > 1000)
									bagSize = 1000;
								else if (cursorMoney > 100)
									bagSize = 100;
								else if (cursorMoney > 10)
									bagSize = 10;
								else if (cursorMoney > 1)
									bagSize = 1;
								else
									bagSize = cursorMoney;
								Core.getMessages().debug("PLACE_ONE: bagSize=%s", bagSize);
								double slotMoney = Reward.isReward(isCurrentSlot)?Reward.getReward(isCurrentSlot).getMoney():0;
								reward.setMoney(bagSize+slotMoney);
								isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCursor.clone(), reward);
								isCurrentSlot.setAmount(1);
								event.setCurrentItem(isCurrentSlot);
								Core.getMessages().debug("PLACE_ONE: cursorMoney=%s", cursorMoney);
								if (bagSize == cursorMoney) {
									isCursor.setType(Material.AIR);
									isCursor.setAmount(0);
									event.setCursor(isCursor);
								} else {
									Reward cursorReward = new Reward(reward);
									cursorReward.setMoney(cursorMoney - bagSize);
									isCursor = Reward.setDisplayNameAndHiddenLores(isCursor.clone(), cursorReward);
									event.setCursor(isCursor);
								}

								if (BagOfGoldCompat.isSupported()
										&& clickedInventory.getType() == InventoryType.PLAYER) {
									BagOfGold.getInstance().getRewardManager().addMoneyToPlayerBalance(player, bagSize);
								}
								Core.getMessages().debug("PLACE_ONE: %s moved %s (%s) into Inventory:%s",
										player.getName(), reward.getDisplayName(), bagSize, clickedInventory.getType());
							}
						}

					} else { // GameMode!=Survival
						if (Reward.isReward(isCurrentSlot) && isCursor.getType() == Material.AIR) {
							Reward reward = Reward.getReward(isCurrentSlot);
							isCursor = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward);
							isCursor.setAmount(1);
							event.setCursor(isCursor);
							isCurrentSlot.setType(Material.AIR);
							isCurrentSlot.setAmount(0);
							event.setCurrentItem(isCurrentSlot);
							Core.getMessages().debug("PLACE_ONE: %s moved %s (%s) out of Inventory", player.getName(),
									reward.getDisplayName(), reward.getMoney());
						} else if (Reward.isReward(isCursor) && isCurrentSlot.getType() == Material.AIR) {
							Reward reward = Reward.getReward(isCursor);
							isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCursor.clone(), reward);
							isCurrentSlot.setAmount(1);
							event.setCurrentItem(isCurrentSlot);
							isCursor.setType(Material.AIR);
							isCursor.setAmount(0);
							event.setCursor(isCursor);
							Core.getMessages().debug("PLACE_ONE: %s moved %s (%s) into Inventory", player.getName(),
									reward.getDisplayName(), reward.getMoney());
						} else if (Reward.isReward(isCursor) && Reward.isReward(isCurrentSlot)) {
							Reward reward = Reward.getReward(isCurrentSlot);
							Reward reward2 = Reward.getReward(isCursor);
							double money = reward.getMoney() + reward2.getMoney();
							if (money > Core.getConfigManager().limitPerBag) {
								reward.setMoney(Core.getConfigManager().limitPerBag);
								reward2.setMoney(money - Core.getConfigManager().limitPerBag);
							} else {
								reward.setMoney(money);
								reward2.setMoney(0);
							}

							isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward);
							isCurrentSlot.setAmount(1);
							event.setCurrentItem(isCurrentSlot);

							if (reward2.getMoney() > 0) {
								isCursor = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward2);
								isCursor.setAmount(1);
							} else {
								isCursor.setType(Material.AIR);
								isCursor.setAmount(0);
							}
							event.setCursor(isCursor);
							Core.getMessages().debug("PLACE_ONE: currentSlot set to %s", reward.getMoney());
							Core.getMessages().debug("PLACE_ONE: cursor set to %s", reward2.getMoney());

						}
					}
					break;
				case PLACE_SOME, PLACE_ALL:
					int amount_of_cursor = isCursor != null ? isCursor.getAmount() : 0;
					int amount_of_currentslot = isCurrentSlot != null ? isCurrentSlot.getAmount() : 0;
					if (player.getGameMode() == GameMode.SURVIVAL) {
						// if (Reward.isReward(isCurrentSlot) && isCursor.getType() == Material.AIR) {
						// Is this ever used ?????
						// Reward reward = Reward.getReward(isCurrentSlot);
						// if (reward.isMoney()) {
						// reward.setMoney(reward.getMoney() * amount_of_currentslot);
						// isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(),
						// reward);
						// isCurrentSlot.setAmount(1);
						// event.setCurrentItem(isCurrentSlot);
						// }
						// Core.getMessages().debug("PLACE_SOME, PLACE_ALL: %s moved %s (%s) out of
						// Inventory",
						// player.getName(), reward.getDisplayName(), reward.getMoney());
						// } else
						if (Reward.isReward(isCursor)) {
							Reward reward = Reward.getReward(isCursor);
							if (reward.isMoney()) {
								event.setCancelled(true);
								double added_money = reward.getMoney() * amount_of_cursor;
								reward.setMoney(reward.getMoney() * (amount_of_cursor + amount_of_currentslot));
								isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCursor.clone(), reward);
								isCurrentSlot.setAmount(1);
								event.setCurrentItem(isCurrentSlot);
								isCursor.setType(Material.AIR);
								isCursor.setAmount(0);
								event.setCursor(isCursor);
								if (BagOfGoldCompat.isSupported()
										&& clickedInventory.getType() == InventoryType.PLAYER) {
									BagOfGold.getInstance().getRewardManager().addMoneyToPlayerBalance(player,
											added_money);
								}
								Core.getMessages().debug("PLACE_SOME, PLACE_ALL: %s moved %s (%s) into Inventory:%s",
										player.getName(), reward.getDisplayName(), added_money,
										clickedInventory.getType());
							}
						}

					} else { // GameMode!=Survival
						if (Reward.isReward(isCurrentSlot) && isCursor.getType() == Material.AIR) {
							Reward reward = Reward.getReward(isCurrentSlot);
							isCursor = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward);
							isCursor.setAmount(1);
							event.setCursor(isCursor);
							isCurrentSlot.setType(Material.AIR);
							isCurrentSlot.setAmount(0);
							event.setCurrentItem(isCurrentSlot);
							Core.getMessages().debug("PLACE_SOME, PLACE_ALL: %s moved %s (%s) out of Inventory (2)",
									player.getName(), reward.getDisplayName(), reward.getMoney());
						} else if (Reward.isReward(isCursor) && isCurrentSlot.getType() == Material.AIR) {
							Reward reward = Reward.getReward(isCursor);
							isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCursor.clone(), reward);
							isCurrentSlot.setAmount(1);
							event.setCurrentItem(isCurrentSlot);
							isCursor.setType(Material.AIR);
							isCursor.setAmount(0);
							event.setCursor(isCursor);
							Core.getMessages().debug("PLACE_SOME, PLACE_ALL: %s moved %s (%s) into Inventory (2)",
									player.getName(), reward.getDisplayName(), reward.getMoney());
						} else if (Reward.isReward(isCursor) && Reward.isReward(isCurrentSlot)) {
							Reward reward = Reward.getReward(isCurrentSlot);
							Reward reward2 = Reward.getReward(isCursor);
							double money = reward.getMoney() + reward2.getMoney();
							if (money > Core.getConfigManager().limitPerBag) {
								reward.setMoney(Core.getConfigManager().limitPerBag);
								reward2.setMoney(money - Core.getConfigManager().limitPerBag);
							} else {
								reward.setMoney(money);
								reward2.setMoney(0);
							}

							isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward);
							isCurrentSlot.setAmount(1);
							event.setCurrentItem(isCurrentSlot);

							if (reward2.getMoney() > 0) {
								isCursor = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward2);
								isCursor.setAmount(1);
							} else {
								isCursor.setType(Material.AIR);
								isCursor.setAmount(0);
							}
							event.setCursor(isCursor);
							Core.getMessages().debug("PLACE_SOME, PLACE_ALL: currentSlot set to %s", reward.getMoney());
							Core.getMessages().debug("PLACE_SOME, PLACE_ALL: cursor set to %s", reward2.getMoney());

						}
					}
					break;
				case SWAP_WITH_CURSOR:
					if (Reward.isReward(isCurrentSlot) && Reward.isReward(isCursor)) {
						ItemMeta imCurrent = isCurrentSlot.getItemMeta();
						ItemMeta imCursor = isCursor.getItemMeta();
						Reward reward1 = new Reward(imCurrent.getLore());
						Reward reward2 = new Reward(imCursor.getLore());
						int amount_reward1 = isCurrentSlot.getAmount();
						int amount_reward2 = isCursor.getAmount();
						if (reward2.isMoney() && slotType == SlotType.ARMOR) {
							// Used when swaping player head with a BagOfGold
							Core.getMessages().debug("SWAP_WITH_CURSOR: %s tried to moved money in to the helmetslot",
									player.getName());
							event.setCancelled(true);
						} else if ((reward1.isMoney()) && reward1.getRewardType().equals(reward2.getRewardType())) {
							event.setCancelled(true);
							if (reward1.getMoney() * amount_reward1
									+ reward2.getMoney() * amount_reward2 <= Core.getConfigManager().limitPerBag) {
								double added_money = reward2.getMoney();
								reward2.setMoney(
										reward1.getMoney() * amount_reward1 + reward2.getMoney() * amount_reward2);
								isCursor = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward2);
								isCursor.setAmount(1);
								isCurrentSlot.setAmount(0);
								isCurrentSlot.setType(Material.AIR);
								event.setCurrentItem(isCursor);
								event.setCursor(isCurrentSlot);
								Core.getMessages().debug("SWAP_WITH_CURSOR: %s merged two rewards(1)",
										player.getName());
								if (BagOfGoldCompat.isSupported()
										&& clickedInventory.getType() == InventoryType.PLAYER) {
									BagOfGold.getInstance().getRewardManager().addMoneyToPlayerBalance(player,
											added_money);
								}
							} else {
								double rest = reward1.getMoney() * amount_reward1 + reward2.getMoney() * amount_reward2
										- Core.getConfigManager().limitPerBag;
								double added_money = Core.getConfigManager().limitPerBag
										- reward1.getMoney() * amount_reward1;
								reward2.setMoney(Core.getConfigManager().limitPerBag);
								isCursor = Reward.setDisplayNameAndHiddenLores(isCursor.clone(), reward2);
								isCursor.setAmount(1);
								reward1.setMoney(rest);
								isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward1);
								isCurrentSlot.setAmount(1);
								event.setCurrentItem(isCursor);
								event.setCursor(isCurrentSlot);
								Core.getMessages().debug("SWAP_WITH_CURSOR: %s merged two rewards(2)",
										player.getName());
								if (BagOfGoldCompat.isSupported()
										&& clickedInventory.getType() == InventoryType.PLAYER) {
									BagOfGold.getInstance().getRewardManager().addMoneyToPlayerBalance(player,
											added_money);
								}
							}
						} else if ((reward1.isKilledHeadReward() || reward1.isKillerHeadReward())
								&& reward1.getRewardType().equals(reward2.getRewardType())
								&& reward1.getSkinUUID().equals(reward2.getSkinUUID())
								&& Tools.round(reward1.getMoney()) == Tools.round(reward2.getMoney())) {
							event.setCancelled(true);
							if (isCursor.getAmount() + isCurrentSlot.getAmount() <= 64) {
								isCurrentSlot.setAmount(isCursor.getAmount() + isCurrentSlot.getAmount());
								isCursor.setAmount(0);
								isCursor.setType(Material.AIR);
								Core.getMessages().debug("SWAP_WITH_CURSOR: %s merged two rewards(3)",
										player.getName());
							} else {
								isCursor.setAmount(isCursor.getAmount() + isCurrentSlot.getAmount() - 64);
								isCurrentSlot.setAmount(64);
								Core.getMessages().debug("SWAP_WITH_CURSOR: %s merged two rewards(4)",
										player.getName());
							}
						}
					}
					break;
				default:
					Core.getMessages().debug("CoreRewardListeners: Unhandled action=%s", action);
				}

			} else {
				Core.getMessages().debug("%s its not allowed to use BagOfGold in a %s inventory", player.getName(),
						inventory.getType());
				event.setCancelled(true);
			}
		} else {
			Core.getMessages().debug("%s its not allowed to use BagOfGold a %s slot", player.getName(), slotType);
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryDragEvent(InventoryDragEvent event) {
		ItemStack isCursor = event.getCursor();
		if (Reward.isReward(event.getOldCursor())) {
			Reward reward = Reward.getReward(event.getOldCursor());
			if (reward.isMoney()) {
				Core.getMessages().debug("InventoryDragEvent: You tried to drag money=%s", reward.getMoney());
				if (reward.isMoney()) {
					Core.getMessages().debug("InventoryDragEvent: you draged money over %s slots",
							event.getInventorySlots().size());
					Set<Integer> slots = event.getInventorySlots();
					for (Integer i : slots) {
						Core.getMessages().debug("InventoryDragEvent: slot=%s", i);
					}
				}
				event.setCancelled(true);
			}
		}
	}

}

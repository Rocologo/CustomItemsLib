package one.lindegaard.CustomItemsLib.materials;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.CustomItemsLib.server.Servers;

public class Materials {

	public static boolean isAxe(ItemStack item) {
		if (Servers.isMC113OrNewer())
			return item != null && (item.getType() == Material.DIAMOND_AXE || item.getType() == Material.GOLDEN_AXE
					|| item.getType() == Material.IRON_AXE || item.getType() == Material.STONE_AXE
					|| item.getType() == Material.WOODEN_AXE);
		else
			return item != null && (item.getType() == Material.DIAMOND_AXE
					|| item.getType() == Material.matchMaterial("GOLD_AXE") || item.getType() == Material.IRON_AXE
					|| item.getType() == Material.STONE_AXE || item.getType() == Material.matchMaterial("WOOD_AXE"));

	}

	public static boolean isSword(ItemStack item) {
		if (Servers.isMC113OrNewer())
			return item != null && (item.getType() == Material.DIAMOND_SWORD || item.getType() == Material.GOLDEN_SWORD
					|| item.getType() == Material.IRON_SWORD || item.getType() == Material.STONE_SWORD
					|| item.getType() == Material.WOODEN_SWORD);
		else
			return item != null && (item.getType() == Material.DIAMOND_SWORD
					|| item.getType() == Material.matchMaterial("GOLD_SWORD") || item.getType() == Material.IRON_SWORD
					|| item.getType() == Material.STONE_SWORD
					|| item.getType() == Material.matchMaterial("WOOD_SWORD"));
	}

	public static boolean isPick(ItemStack item) {
		if (Servers.isMC113OrNewer())
			return item != null && (item.getType() == Material.DIAMOND_PICKAXE
					|| item.getType() == Material.GOLDEN_PICKAXE || item.getType() == Material.IRON_PICKAXE
					|| item.getType() == Material.STONE_PICKAXE || item.getType() == Material.WOODEN_PICKAXE);
		else
			return item != null && (item.getType() == Material.DIAMOND_PICKAXE
					|| item.getType() == Material.matchMaterial("GOLD_PICKAXE")
					|| item.getType() == Material.IRON_PICKAXE || item.getType() == Material.STONE_PICKAXE
					|| item.getType() == Material.matchMaterial("WOOD_PICKAXE"));
	}

	public static boolean isBow(ItemStack item) {
		return item != null && (item.getType() == Material.BOW);
	}

	public static boolean isUnarmed(ItemStack item) {
		return (item == null || item.getType() == Material.AIR);
	}

	public static boolean isTrident(ItemStack item) {
		if (Servers.isMC113OrNewer())
			return item != null && (item.getType() == Material.TRIDENT);
		else
			return false;
	}

	public static boolean isSign(Block block) {
		if (Servers.isMC114OrNewer())
			return block.getType() == Material.ACACIA_SIGN || block.getType() == Material.ACACIA_WALL_SIGN
					|| block.getType() == Material.BIRCH_SIGN || block.getType() == Material.BIRCH_WALL_SIGN
					|| block.getType() == Material.DARK_OAK_SIGN || block.getType() == Material.DARK_OAK_WALL_SIGN
					|| block.getType() == Material.JUNGLE_SIGN || block.getType() == Material.JUNGLE_WALL_SIGN
					|| block.getType() == Material.LEGACY_SIGN || block.getType() == Material.LEGACY_SIGN_POST
					|| block.getType() == Material.LEGACY_WALL_SIGN || block.getType() == Material.OAK_SIGN
					|| block.getType() == Material.OAK_WALL_SIGN || block.getType() == Material.SPRUCE_SIGN
					|| block.getType() == Material.SPRUCE_WALL_SIGN;
		else if (Servers.isMC113OrNewer())
			return block.getType() == Material.matchMaterial("WALL_SIGN")
					|| block.getType() == Material.matchMaterial("SIGN_POST")
					|| block.getType() == Material.matchMaterial("SIGN") || block.getType() == Material.LEGACY_SIGN
					|| block.getType() == Material.LEGACY_SIGN_POST || block.getType() == Material.LEGACY_WALL_SIGN;
		else
			return block.getType() == Material.matchMaterial("WALL_SIGN")
					|| block.getType() == Material.matchMaterial("SIGN_POST")
					|| block.getType() == Material.matchMaterial("SIGN");
	}

	public static boolean isWallSign(Block block) {
		if (Servers.isMC114OrNewer())
			return block.getType() == Material.ACACIA_WALL_SIGN || block.getType() == Material.BIRCH_WALL_SIGN
					|| block.getType() == Material.DARK_OAK_WALL_SIGN || block.getType() == Material.JUNGLE_WALL_SIGN
					|| block.getType() == Material.LEGACY_WALL_SIGN || block.getType() == Material.OAK_WALL_SIGN
					|| block.getType() == Material.SPRUCE_WALL_SIGN;
		else if (Servers.isMC113OrNewer())
			return block.getType() == Material.matchMaterial("WALL_SIGN");
		else
			return block.getType() == Material.matchMaterial("WALL_SIGN");
	}

	public static boolean isSign(Material material) {
		if (Servers.isMC114OrNewer())
			return material == Material.ACACIA_SIGN || material == Material.ACACIA_WALL_SIGN
					|| material == Material.BIRCH_SIGN || material == Material.BIRCH_WALL_SIGN
					|| material == Material.DARK_OAK_SIGN || material == Material.DARK_OAK_WALL_SIGN
					|| material == Material.JUNGLE_SIGN || material == Material.JUNGLE_WALL_SIGN
					|| material == Material.LEGACY_SIGN || material == Material.LEGACY_SIGN_POST
					|| material == Material.LEGACY_WALL_SIGN || material == Material.OAK_SIGN
					|| material == Material.OAK_WALL_SIGN || material == Material.SPRUCE_SIGN
					|| material == Material.SPRUCE_WALL_SIGN;
		else if (Servers.isMC113OrNewer())
			return material == Material.matchMaterial("WALL_SIGN") || material == Material.matchMaterial("SIGN_POST")
					|| material == Material.matchMaterial("SIGN") || material == Material.LEGACY_SIGN
					|| material == Material.LEGACY_SIGN_POST || material == Material.LEGACY_WALL_SIGN;
		else
			return material == Material.matchMaterial("WALL_SIGN")
					// || material == Material.matchMaterial("SIGN_POST")
					|| material == Material.matchMaterial("SIGN");
	}

	public static boolean isSkull(Material material) {
		if (Servers.isMC113OrNewer())
			return material == Material.PLAYER_HEAD || material == Material.PLAYER_WALL_HEAD
					|| material == Material.SKELETON_SKULL || material == Material.SKELETON_WALL_SKULL
					|| material == Material.WITHER_SKELETON_SKULL || material == Material.WITHER_SKELETON_WALL_SKULL
					|| material == Material.CREEPER_HEAD || material == Material.CREEPER_WALL_HEAD
					|| material == Material.DRAGON_HEAD || material == Material.DRAGON_WALL_HEAD;
		else
			return material == Material.matchMaterial("SKULL_ITEM") || material == Material.matchMaterial("SKULL");
	}
	
	public static boolean isPlayerHead(Material material) {
		if (Servers.isMC113OrNewer())
			return material == Material.PLAYER_HEAD || material == Material.PLAYER_WALL_HEAD;
		else
			return material == Material.matchMaterial("SKULL_ITEM") || material == Material.matchMaterial("SKULL");
	}

}

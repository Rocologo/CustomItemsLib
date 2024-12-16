package metadev.digital.metacustomitemslib.rewards;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import metadev.digital.metacustomitemslib.Core;
import metadev.digital.metacustomitemslib.Strings;
import metadev.digital.metacustomitemslib.mobs.MobType;
import metadev.digital.metacustomitemslib.server.Servers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

public class CoreCustomItems {

	/**
	 * Return an ItemStack with a custom texture. Updated to 1.21.1+ standards for PlayerProfile over GameProfile
	 *
	 * @param reward - Existing Reward Item
	 * @param textureURL - Mojang Texture URL
	 * @return ItemStack with custom texture.
	 */
	public static ItemStack getCustomTexture(Reward reward, String textureURL) {
		ItemStack skull = CoreCustomItems.getDefaultPlayerHead(1);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		if (textureURL.isEmpty()){
			return skull;
		}

		try {
			PlayerProfile playerProfile = Bukkit.createPlayerProfile(reward.getSkinUUID(), reward.getDisplayName().replaceAll("\\s+", "_"));
			PlayerTextures textures = playerProfile.getTextures();

			textures.setSkin(new URL(textureURL));

			skullMeta.setOwnerProfile(playerProfile);
			skull.setItemMeta(skullMeta);
			skull = Reward.setDisplayNameAndHiddenLores(skull, reward);

		} catch (MalformedURLException e) {
			Core.getMessages().debug("There was an issue validating the texture URL and getting the data from it");
			e.printStackTrace();
        } catch (NullPointerException e) {
			Core.getMessages().debug("setOwnerProfile on skullMeta created an NPE");
			e.printStackTrace();
		}

		return skull;
    }

	/**
	 * Return an ItemStack with a custom texture. If Mojang changes the way they
	 * calculate Signatures this method will stop working.
	 *
	 *
	 * @param reward
	 * @param mTextureValue
	 * @param mTextureSignature
	 * @return ItemStack with custom texture.
	 * @deprecated - 1.21.1+ requires PlayerProfile and TextureURLs instead of GameProfile and Texture Value/Signatures
	 */
	public static ItemStack getCustomtexture(Reward reward, String mTextureValue, String mTextureSignature) {
		ItemStack skull = CoreCustomItems.getDefaultPlayerHead(1);
		if (mTextureSignature.isEmpty() || mTextureValue.isEmpty())
			return skull;

		// add custom texture to skull
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		GameProfile profile = new GameProfile(reward.getSkinUUID(), reward.getDisplayName().replaceAll("\\s+", "_"));
		if (mTextureSignature.isEmpty())
			profile.getProperties().put("textures", new Property("textures", mTextureValue));
		else
			profile.getProperties().put("textures", new Property("textures", mTextureValue, mTextureSignature));
		Field profileField = null;

		try {
			profileField = skullMeta.getClass().getDeclaredField("profile");
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return skull;
		}
		profileField.setAccessible(true);
		try {
			profileField.set(skullMeta, profile);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		skull.setItemMeta(skullMeta);

		// add displayname and lores to skull
		skull = Reward.setDisplayNameAndHiddenLores(skull, reward);
		return skull;
	}

	/**
	 * Return an ItemStack with the Players head texture.
	 *
	 * @param name
	 * @param money
	 * @return
	 */
	public static ItemStack getPlayerHead(UUID uuid, String name, int amount, double money) {
		// TODO: SEE IF WE NEED NAME OR MONEY AT ALL
		ItemStack skull = CoreCustomItems.getDefaultPlayerHead(amount);
		skull.setAmount(amount);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

		try {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
			PlayerProfile playerProfile = offlinePlayer.getPlayerProfile();

			PlayerTextures playerTextures = playerProfile.getTextures();
			playerTextures.setSkin((playerProfile.getTextures().getSkin()));

			skullMeta.setOwnerProfile(playerProfile);
			skull.setItemMeta(skullMeta);
		} catch (NullPointerException e) {
			Core.getMessages().debug("setOwnerProfile on skullMeta created an NPE");
			e.printStackTrace();
		}
		return skull;
	}

	/**
	 *
	 * @param uuid
	 * @return
	 * @deprecated - Player head drop system changed in 1.21.1 API
	 */
	private static String[] getSkinFromUUID(UUID uuid) {
		try {
			URL url_1 = new URL(
					"https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
			InputStreamReader reader_1;
			reader_1 = new InputStreamReader(url_1.openStream());

			JsonElement json = new JsonParser().parse(reader_1);
			if (json.isJsonObject()) {
				JsonObject textureProperty = json.getAsJsonObject().get("properties").getAsJsonArray().get(0)
						.getAsJsonObject();
				String texture = textureProperty.get("value").getAsString();
				String signature = textureProperty.get("signature").getAsString();

				return new String[] { texture, signature };
			} else {
				Core.getMessages().debug("(1) Could not get skin data from session servers!");
				return null;
			}

		} catch (IOException e) {
			Core.getMessages().debug("(2)Could not get skin data from session servers!");
			return null;
		}
	}

	/**
	 *
	 * @param uuid
	 * @param name
	 * @param amount
	 * @param money
	 * @return
	 * @deprecated - Player head drop system changed in 1.21.1 API
	 */
	private static ItemStack getPlayerHeadOwningPlayer(UUID uuid, String name, int amount, double money) {
		ItemStack skull = CoreCustomItems.getDefaultPlayerHead(amount);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

		skull.setItemMeta(skullMeta);
		skull = Reward.setDisplayNameAndHiddenLores(skull, name, money, new ArrayList<String>(Arrays.asList(
				"Hidden(0):" + name, "Hidden(1):" + String.format(Locale.ENGLISH, "%.5f", money),
				"Hidden(2):" + RewardType.KILLED.getType(), "Hidden(4):" + uuid,
				"Hidden(5):"
						+ Strings.encode(String.format(Locale.ENGLISH, "%.5f", money) + RewardType.KILLED.getType()),
				Core.getMessages().getString("core.reward.lore"))));
		Core.getMessages().debug("CustomItems: set the skin using OwningPlayer/Owner (%s)", name);
		return skull;
	}

	public static ItemStack getCustomHead(MobType minecraftMob, String name, int amount, double money, UUID skinUUID) {
		ItemStack skull;
		switch (minecraftMob) {
		case Skeleton:
			skull = CoreCustomItems.getDefaultSkeletonHead(amount);
			skull = Reward.setDisplayNameAndHiddenLores(skull,
					new Reward(minecraftMob.getEntityName(), money, RewardType.KILLED, skinUUID));
			break;

		case WitherSkeleton:
			skull = CoreCustomItems.getDefaultWitherSkeletonHead(amount);
			skull = Reward.setDisplayNameAndHiddenLores(skull,
					new Reward(minecraftMob.getEntityName(), money, RewardType.KILLED, skinUUID));
			break;

		case Zombie:
			skull = CoreCustomItems.getDefaultZombieHead(amount);
			skull = Reward.setDisplayNameAndHiddenLores(skull,
					new Reward(minecraftMob.getEntityName(), money, RewardType.KILLED, skinUUID));
			break;

		case PvpPlayer:
			skull = getPlayerHead(skinUUID, name, amount, money);
			break;

		case Creeper:
			skull = CoreCustomItems.getDefaultCreeperHead(amount);
			skull = Reward.setDisplayNameAndHiddenLores(skull,
					new Reward(minecraftMob.getEntityName(), money, RewardType.KILLED, skinUUID));
			break;

		case EnderDragon:
			skull = CoreCustomItems.getDefaultEnderDragonHead(amount);
			skull = Reward.setDisplayNameAndHiddenLores(skull,
					new Reward(minecraftMob.getEntityName(), money, RewardType.KILLED, skinUUID));
			break;

		default:
			ItemStack is = new ItemStack(
					getCustomTexture(new Reward(minecraftMob.getEntityName(), money, RewardType.KILLED, skinUUID),
							minecraftMob.getTextureURL()));
			is.setAmount(amount);
			return is;
		}
		return skull;
	}

	private static ItemStack getDefaultSkeletonHead(int amount) {
		if (Servers.isMC113OrNewer())
			return new ItemStack(Material.SKELETON_SKULL, amount);
		else
			return new ItemStack(Material.matchMaterial("SKULL_ITEM"), amount, (short) 0);
	}

	private static ItemStack getDefaultWitherSkeletonHead(int amount) {
		if (Servers.isMC113OrNewer())
			return new ItemStack(Material.WITHER_SKELETON_SKULL, amount);
		else
			return new ItemStack(Material.matchMaterial("SKULL_ITEM"), amount, (short) 1);
	}

	private static ItemStack getDefaultZombieHead(int amount) {
		if (Servers.isMC113OrNewer())
			return new ItemStack(Material.ZOMBIE_HEAD, amount);
		else
			return new ItemStack(Material.matchMaterial("SKULL_ITEM"), amount, (short) 2);
	}

	private static ItemStack getDefaultPlayerHead(int amount) {
		if (Servers.isMC113OrNewer())
			return new ItemStack(Material.PLAYER_HEAD, amount);
		else
			return new ItemStack(Material.matchMaterial("SKULL_ITEM"), amount, (short) 3);
	}

	private static ItemStack getDefaultCreeperHead(int amount) {
		if (Servers.isMC113OrNewer())
			return new ItemStack(Material.CREEPER_HEAD, amount);
		else
			return new ItemStack(Material.matchMaterial("SKULL_ITEM"), amount, (short) 4);
	}

	private static ItemStack getDefaultEnderDragonHead(int amount) {
		if (Servers.isMC113OrNewer())
			return new ItemStack(Material.DRAGON_HEAD, amount);
		else
			return new ItemStack(Material.matchMaterial("SKULL_ITEM"), amount, (short) 5);
	}

}

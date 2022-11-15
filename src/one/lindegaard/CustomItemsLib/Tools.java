package one.lindegaard.CustomItemsLib;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class Tools {

	public static String format(double money) {
		Locale locale = new Locale("en", "UK");
		String pattern = "0.#####";
		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
		decimalFormat.applyPattern(pattern);
		return decimalFormat.format(money);
	}

	public static boolean isUUID(String string) {
		try {
			UUID.fromString(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static final Block getTargetBlock(Player player, int range) {
		BlockIterator iter = new BlockIterator(player, range);
		Block lastBlock = iter.next();
		while (iter.hasNext()) {
			lastBlock = iter.next();
			if (lastBlock.getType() != Material.AIR) 
				break;
			
		}
		return lastBlock;
	}

	/**
	 * Gets the online player (backwards compatibility)
	 *
	 * @return number of players online
	 */
	public static int getOnlinePlayersAmount() {
		try {
			Method method = Server.class.getMethod("getOnlinePlayers");
			if (method.getReturnType().equals(Collection.class)) {
				return ((Collection<?>) method.invoke(Bukkit.getServer())).size();
			} else {
				return ((Player[]) method.invoke(Bukkit.getServer())).length;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	/**
	 * Gets the online player (for backwards compatibility)
	 *
	 * @return all online players as a Java Collection, if return type of
	 *         Bukkit.getOnlinePlayers() is Player[] it will be converted to a
	 *         Collection.
	 */
	@SuppressWarnings({ "unchecked" })
	public static Collection<Player> getOnlinePlayers() {
		Method method;
		try {
			method = Bukkit.class.getDeclaredMethod("getOnlinePlayers");
			Object players = method.invoke(null);
			Collection<Player> newPlayers;
			if (players instanceof Player[])
				newPlayers = Arrays.asList((Player[]) players);
			else
				newPlayers = (Collection<Player>) players;
			return newPlayers;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return Collections.emptyList();
	}

	public static Player getOnlinePlayer(OfflinePlayer offlinePlayer) {
		for (Player player : getOnlinePlayers()) {
			if (player.getName().equals(offlinePlayer.getName()))
				return player;
		}
		return null;
	}

	public static boolean isPlayerOnline(String name) {
		return Bukkit.getPlayer(name)!=null;
	}

	public static Map<String, Object> toMap(Location loc) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("X", loc.getX());
		map.put("Y", loc.getY());
		map.put("Z", loc.getZ());

		map.put("Yaw", (double) loc.getYaw());
		map.put("Pitch", (double) loc.getPitch());

		if (loc.getWorld() != null)
			map.put("W", loc.getWorld().getUID().toString());

		return map;
	}

	public static Location fromMap(Map<String, Object> map) {
		double x, y, z;
		float yaw, pitch;
		UUID world;

		x = (Double) map.get("X");
		y = (Double) map.get("Y");
		z = (Double) map.get("Z");

		yaw = (float) (double) (Double) map.get("Yaw");
		pitch = (float) (double) (Double) map.get("Pitch");

		if (map.containsKey("W")) {
			world = UUID.fromString((String) map.get("W"));
			return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
		} else
			return new Location(null, x, y, z, yaw, pitch);
	}

	public static String trimSignText(String string) {
		return string.length() > 15 ? string.substring(0, 14).trim() : string;
	}

	public static double round(double d) {
		return Math.round(d / Core.getConfigManager().rewardRounding) * Core.getConfigManager().rewardRounding;
	}

	public static double ceil(double d) {
		return Math.ceil(d / Core.getConfigManager().rewardRounding) * Core.getConfigManager().rewardRounding;
	}

	public static double floor(double d) {
		return Math.floor(d / Core.getConfigManager().rewardRounding) * Core.getConfigManager().rewardRounding;
	}

}

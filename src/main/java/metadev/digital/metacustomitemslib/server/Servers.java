package metadev.digital.metacustomitemslib.server;

import org.bukkit.Bukkit;

public class Servers {

	// *******************************************************************
	// Version detection
	// *******************************************************************

	public static boolean isMC120() {
		return Bukkit.getBukkitVersion().contains("1.20");
	}
	
	public static boolean isMC119() {
		return Bukkit.getBukkitVersion().contains("1.19");
	}
	
	public static boolean isMC118() {
		return Bukkit.getBukkitVersion().contains("1.18");
	}
	
	public static boolean isMC117() {
		return Bukkit.getBukkitVersion().contains("1.17");
	}
	
	public static boolean isMC1162() {
		return Bukkit.getBukkitVersion().contains("1.16.2");
	}
	
	public static boolean isMC116() {
		return Bukkit.getBukkitVersion().contains("1.16");
	}
	
	public static boolean isMC115() {
		return Bukkit.getBukkitVersion().contains("1.15");
	}

	public static boolean isMC114() {
		return Bukkit.getBukkitVersion().contains("1.14");
	}

	public static boolean isMC113() {
		return Bukkit.getBukkitVersion().contains("1.13");
	}

	public static boolean isMC112() {
		return Bukkit.getBukkitVersion().contains("1.12");
	}

	public static boolean isMC111() {
		return Bukkit.getBukkitVersion().contains("1.11");
	}

	public static boolean isMC110() {
		return Bukkit.getBukkitVersion().contains("1.10");
	}

	public static boolean isMC19() {
		return Bukkit.getBukkitVersion().contains("1.9");
	}

	public static boolean isMC18() {
		return Bukkit.getBukkitVersion().contains("1.8");
	}

	public static boolean isMC120OrNewer() {
		if (isMC120())
			return true;
		else if (isMC119() || isMC118() || isMC117() || isMC1162() || isMC116() || isMC115() || isMC114() || isMC113() || isMC112() || isMC111() || isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}

	public static boolean isMC119OrNewer() {
		if (isMC119())
			return true;
		else if (isMC118() || isMC117() || isMC1162() || isMC116() || isMC115() || isMC114() || isMC113() || isMC112() || isMC111() || isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}
	
	public static boolean isMC118OrNewer() {
		if (isMC118())
			return true;
		else if (isMC117() || isMC1162() || isMC116() || isMC115() || isMC114() || isMC113() || isMC112() || isMC111() || isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}
	
	public static boolean isMC117OrNewer() {
		if (isMC117())
			return true;
		else if (isMC1162() || isMC116() || isMC115() || isMC114() || isMC113() || isMC112() || isMC111() || isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}
	
	public static boolean isMC1162OrNewer() {
		if (isMC1162())
			return true;
		else if (isMC116() || isMC115() || isMC114() || isMC113() || isMC112() || isMC111() || isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}
	
	public static boolean isMC116OrNewer() {
		if (isMC116())
			return true;
		else if (isMC115() || isMC114() || isMC113() || isMC112() || isMC111() || isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}

	public static boolean isMC115OrNewer() {
		if (isMC115())
			return true;
		else if (isMC114() || isMC113() || isMC112() || isMC111() || isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}

	public static boolean isMC114OrNewer() {
		if (isMC114())
			return true;
		else if (isMC113() || isMC112() || isMC111() || isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}

	public static boolean isMC113OrNewer() {
		if (isMC113())
			return true;
		else if (isMC112() || isMC111() || isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}

	public static boolean isMC112OrNewer() {
		if (isMC112())
			return true;
		else if (isMC111() || isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}

	public static boolean isMC111OrNewer() {
		if (isMC111())
			return true;
		else if (isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}

	public static boolean isMC110OrNewer() {
		if (isMC110())
			return true;
		else if (isMC19() || isMC18())
			return false;
		return true;
	}

	public static boolean isMC19OrNewer() {
		if (isMC19())
			return true;
		else if (isMC18())
			return false;
		return true;
	}

	// *******************************************************************
	// Version detection
	// *******************************************************************
	public static boolean isGlowstoneServer() {
		return Bukkit.getServer().getName().equalsIgnoreCase("Glowstone");
	}

    public static boolean isPaperServer() {
        return Bukkit.getServer().getName().equalsIgnoreCase("Paper")
                && Bukkit.getServer().getVersion().toLowerCase().contains("paper");
    }

    public static boolean isPurpurServer() {
        return Bukkit.getServer().getName().equalsIgnoreCase("Purpur")
                && Bukkit.getServer().getVersion().toLowerCase().contains("purpur");
    }

	public static boolean isSpigotServer() {
		return Bukkit.getServer().getName().equalsIgnoreCase("CraftBukkit")
				&& Bukkit.getServer().getVersion().toLowerCase().contains("spigot");
	}

	public static boolean isCraftBukkitServer() {
		return Bukkit.getServer().getName().equalsIgnoreCase("CraftBukkit")
				&& Bukkit.getServer().getVersion().toLowerCase().contains("bukkit");
	}

}

package metadev.digital.metacustomitemslib;

import net.milkbowl.vault.economy.Economy;
import net.tnemc.core.Reserve;
import net.tnemc.core.economy.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;

/**
 * Economy handler to interface with Vault or Reserve directly.
 * 
 * @author Rocologo
 * 
 */
public class EconomyManager {

	private Core plugin = null;
	private Economy vaultEconomy = null;
	private EconomyAPI reserveEconomy = null;
	private EcoType Type = EcoType.NONE;
	private String version = "";

	public enum EcoType {
		NONE, VAULT, RESERVE
	}

	public EconomyManager(Core plugin) {
		this.plugin = plugin;
		setupEconomy();
	}

	/**
	 * @return the economy type we have detected.
	 */
	public EcoType getType() {

		return Type;
	}

	/**
	 * Are we using any economy system?
	 * 
	 * @return true if we found one.
	 */
	public boolean isActive() {
		return (Type != EcoType.NONE);
	}

	/**
	 * @return The current economy providers version string
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Internal function to set the version string.
	 * 
	 * @param version
	 */
	private void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Find and configure a suitable economy provider
	 * 
	 * @return true if successful.
	 */
	public Boolean setupEconomy() {
		Plugin economyProvider = null;

		/*
		 * Attempt to find Vault for Economy handling
		 */
		try {
			RegisteredServiceProvider<Economy> vaultEcoProvider = plugin.getServer().getServicesManager()
					.getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (vaultEcoProvider != null) {
				/*
				 * Flag as using Vault hooks
				 */
				vaultEconomy = vaultEcoProvider.getProvider();
				setVersion(String.format("%s %s", vaultEcoProvider.getProvider().getName(), "via Vault"));
				Bukkit.getConsoleSender().sendMessage(
						Core.PREFIX + "CustomItemsLib is using " + getVersion() + " as Economy Provider");
				Type = EcoType.VAULT;
				return true;
			}
		} catch (NoClassDefFoundError ex) {
		}

		/*
		 * Attempt to find Reserve for Economy handling
		 */
		economyProvider = plugin.getServer().getPluginManager().getPlugin("Reserve");
		if (economyProvider != null && ((Reserve) economyProvider).economyProvided()) {
			/*
			 * Flat as using Reserve Hooks.
			 */
			reserveEconomy = ((Reserve) economyProvider).economy();
			setVersion(String.format("%s %s", reserveEconomy.name(), "via Reserve"));
			Bukkit.getConsoleSender()
					.sendMessage(Core.PREFIX + "CustomItemsLib is using " + getVersion() + " as Economy Provider");
			Type = EcoType.RESERVE;
			return true;
		}

		return false;
	}

	/**
	 * Get the name of the economy plugin
	 * 
	 * @return name
	 */
	public String getName() {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.name();
		case VAULT:
			return vaultEconomy.getName();
		default:
			return "Vault or Reserve was not found";
		}
	}

	public String format(Double amount) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.format(new BigDecimal(amount));
		case VAULT:
			return vaultEconomy.format(amount);
		default:
			return "Vault or Reserve was not found";
		}
	}

	/**
	 * Check if account exists
	 * 
	 * @param offlinePlayer
	 * @return
	 */
	public boolean hasEconomyAccount(OfflinePlayer offlinePlayer) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.hasAccount(offlinePlayer.getUniqueId());
		case VAULT:
			return vaultEconomy.hasAccount(offlinePlayer);
		default:
			break;
		}
		return false;
	}

	/**
	 * Attempt to delete the economy account.
	 */
	public void removeAccount(OfflinePlayer offlinePlayer) {
		try {
			switch (Type) {
			case RESERVE:
				reserveEconomy.deleteAccount(offlinePlayer.getUniqueId());
				break;
			case VAULT: // Attempt to zero the account as Vault provides no delete method.
				if (!vaultEconomy.hasAccount(offlinePlayer))
					vaultEconomy.createPlayerAccount(offlinePlayer);
				vaultEconomy.withdrawPlayer(offlinePlayer, (vaultEconomy.getBalance(offlinePlayer)));
				return;
			default:
				break;
			}
		} catch (NoClassDefFoundError e) {
		}
		return;
	}

	/**
	 * Check if offlinePlayer has the amount of money on his account
	 * 
	 * @param offlinePlayer
	 * @param amount
	 * 
	 * @return true if the player has enough money on his account
	 */
	public boolean hasMoney(OfflinePlayer offlinePlayer, double amount) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.hasHoldings(offlinePlayer.getUniqueId(), new BigDecimal(amount));
		case VAULT:
			return vaultEconomy.has(offlinePlayer, amount);
		default:
			break;
		}
		return false;
	}

	/**
	 * Returns the accounts current balance
	 * 
	 * @param offlinePlayer
	 * @return double containing the total in the account
	 */
	public double getBalance(OfflinePlayer offlinePlayer) {
		switch (Type) {
		case RESERVE:
			if (!reserveEconomy.hasAccount(offlinePlayer.getUniqueId()))
				reserveEconomy.createAccount(offlinePlayer.getUniqueId());
			return reserveEconomy.getHoldings(offlinePlayer.getUniqueId()).doubleValue();
		case VAULT:
			if (!vaultEconomy.hasAccount(offlinePlayer))
				vaultEconomy.createPlayerAccount(offlinePlayer);
			return vaultEconomy.getBalance(offlinePlayer);
		default:
			break;
		}
		return 0.0;
	}

	/**
	 * Returns true if the account has enough money
	 * 
	 * @param offlinePlayer
	 * @param amount
	 * @return true if there is enough in the account
	 */
	public boolean hasEnough(OfflinePlayer offlinePlayer, Double amount) {
		if (getBalance(offlinePlayer) >= amount)
			return true;
		return false;
	}

	/**
	 * Attempts to remove an amount from an account
	 * 
	 * @param offlinePlayer
	 * @param amount
	 * @return true if successful
	 */
	public boolean withdrawPlayer(OfflinePlayer offlinePlayer, Double amount) {
		switch (Type) {
		case RESERVE:
			if (!reserveEconomy.hasAccount(offlinePlayer.getUniqueId()))
				reserveEconomy.createAccount(offlinePlayer.getUniqueId());
			return reserveEconomy.removeHoldings(offlinePlayer.getUniqueId(), new BigDecimal(amount));
		case VAULT:
			if (!vaultEconomy.hasAccount(offlinePlayer))
				vaultEconomy.createPlayerAccount(offlinePlayer);
			return vaultEconomy.withdrawPlayer(offlinePlayer, amount).transactionSuccess();
		default:
			break;
		}
		return false;
	}

	/**
	 * Add funds to an account.
	 * 
	 * @param offlinePlayer
	 * @param amount
	 * @param world
	 * @return true if successful
	 */
	public boolean depositPlayer(OfflinePlayer offlinePlayer, Double amount) {
		switch (Type) {
		case RESERVE:
			if (!reserveEconomy.hasAccount(offlinePlayer.getUniqueId()))
				reserveEconomy.createAccount(offlinePlayer.getUniqueId());
			return reserveEconomy.addHoldings(offlinePlayer.getUniqueId(), new BigDecimal(amount));
		case VAULT:
			if (!vaultEconomy.hasAccount(offlinePlayer))
				vaultEconomy.createPlayerAccount(offlinePlayer);
			return vaultEconomy.depositPlayer(offlinePlayer, amount).transactionSuccess();
		default:
			break;
		}
		return false;
	}

	/**
	 * Set the OfflinePlayers balance on his account to amount
	 * 
	 * @param offlinePlayer
	 * @param amount
	 * @param world
	 * 
	 * @return true if the balance was set
	 */
	public boolean setBalance(OfflinePlayer offlinePlayer, Double amount, World world) {
		switch (Type) {
		case RESERVE:
			if (!reserveEconomy.hasAccount(offlinePlayer.getUniqueId()))
				reserveEconomy.createAccount(offlinePlayer.getUniqueId());
			return reserveEconomy.setHoldings(offlinePlayer.getUniqueId(), new BigDecimal(amount), world.getName());
		case VAULT:
			if (!vaultEconomy.hasAccount(offlinePlayer))
				vaultEconomy.createPlayerAccount(offlinePlayer);
			return vaultEconomy.depositPlayer(offlinePlayer, (amount - vaultEconomy.getBalance(offlinePlayer)))
					.transactionSuccess();
		default:
			break;
		}
		return false;
	}

	/**
	 * Format this balance according to the current economy systems settings.
	 * 
	 * @param balance
	 * @return string containing the formatted balance
	 */
	public String getFormattedBalance(double balance) {
		try {
			switch (Type) {
			case RESERVE:
				return reserveEconomy.format(new BigDecimal(balance));
			case VAULT:
				return vaultEconomy.format(balance);
			default:
				break;
			}
		} catch (Exception InvalidAPIFunction) {
		}
		return String.format("%.2f", balance);
	}

	/**
	 * Get the maximum amount of money the player can carry in his inventory
	 * 
	 * @param offlinePlayer
	 * @return
	 */
	public double getSpaceForMoney(OfflinePlayer offlinePlayer) {
		switch (Type) {
		case RESERVE:
			if (!reserveEconomy.hasAccount(offlinePlayer.getUniqueId()))
				reserveEconomy.createAccount(offlinePlayer.getUniqueId());
			return reserveEconomy.getHoldings(offlinePlayer.getUniqueId()).doubleValue();
		case VAULT:
			if (!vaultEconomy.hasAccount(offlinePlayer))
				vaultEconomy.createPlayerAccount(offlinePlayer);
			return vaultEconomy.getBalance(offlinePlayer);
		default:
			break;
		}
		return 0;
	}

	/**
	 * Check if the if the accounts owner is offlineplayer
	 * 
	 * @param account
	 * @param offlinePlayer
	 * 
	 * @return true if the accounts owner is the offlineplayer
	 */
	public boolean isBankOwner(String account, OfflinePlayer offlinePlayer) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.isAccessor(account, offlinePlayer.getUniqueId());
		case VAULT:
			return vaultEconomy.isBankOwner(account, offlinePlayer).transactionSuccess();
		default:
			break;
		}
		return false;
	}

	/**
	 * Get Bank Balance for OfflinePlayer
	 * 
	 * @param offlinePlayer
	 * @return
	 */
	public double getBankBalance(OfflinePlayer offlinePlayer) {
		switch (Type) {
		case RESERVE:
			if (!reserveEconomy.hasAccount(offlinePlayer.getUniqueId()))
				reserveEconomy.createAccount(offlinePlayer.getUniqueId());
			return reserveEconomy.getHoldings(offlinePlayer.getUniqueId()).doubleValue();
		case VAULT:
			if (!vaultEconomy.hasAccount(offlinePlayer))
				vaultEconomy.createPlayerAccount(offlinePlayer);
			return vaultEconomy.bankBalance(offlinePlayer.getUniqueId().toString()).balance;
		default:
			break;
		}
		return 0.0;
	}

}

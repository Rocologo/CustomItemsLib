package metadev.digital.metacustomitemslib.messages;

//TODO: PlaceHolderAPICompat is throwing errors when being called here. Make CustomItemsLib version?
import metadev.digital.metabagofgold.compatibility.PlaceholderAPICompat;

import metadev.digital.metacustomitemslib.Core;
import metadev.digital.metacustomitemslib.Strings;
import metadev.digital.metacustomitemslib.compatibility.*;
import metadev.digital.metacustomitemslib.server.Servers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Messages {

	private Plugin plugin;

	private File dataFolder;
	private String datapath = "";

	public Messages(Plugin plugin) {
		this.plugin = plugin;
		datapath = plugin.getDataFolder().getPath();
		dataFolder = new File(datapath);
		exportDefaultLanguages(plugin);
	}

	private static Map<String, String> mTranslationTable;
	private static String[] mValidEncodings = new String[] { "UTF-16", "UTF-16BE", "UTF-16LE", "UTF-8", "ISO646-US" };
	private static String[] sources = new String[] { "en_US.lang", "fr_FR.lang", "hu_HU.lang", "pl_PL.lang",
			"pt_BR.lang", "ru_RU.lang", "zh_CN.lang" };

	public void exportDefaultLanguages(Plugin plugin) {
		File folder = new File(dataFolder, "lang");
		if (!folder.exists())
			folder.mkdirs();

		for (String source : sources) {
			File dest = new File(folder, source);
			if (!dest.exists()) {
				Bukkit.getConsoleSender().sendMessage(Core.PREFIX + "Creating language file " + source + " from JAR.");
				InputStream is = plugin.getResource("lang/" + source);
				String outputFile = datapath + "/lang/" + source;
				try {
					Files.copy(is, Paths.get(outputFile));
					File file = new File(outputFile);
					sortFileOnDisk(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				if (!injectChanges(plugin.getResource("lang/" + source), new File(dataFolder, "lang/" + source))) {
					InputStream is = plugin.getResource("lang/" + source);
					String outputFile = datapath + "/lang/" + source;
					try {
						if(Files.deleteIfExists(Path.of(outputFile))){
							Bukkit.getConsoleSender().sendMessage(Core.PREFIX + "Newer version of language file " + source + " available from JAR, overwriting file.");
						}
						Files.copy(is, Paths.get(outputFile));
						File file = new File(outputFile);
						sortFileOnDisk(file);
					} catch (IOException e) {
						Bukkit.getConsoleSender().sendMessage(Core.PREFIX_ERROR + "Failed to write to or generate new lang file.");
						e.printStackTrace();
					}
                }
			}
		}
	}

	private static boolean injectChanges(InputStream inJar, File onDisk) {
		try {
			Map<String, String> source = loadLang(inJar, "UTF-8");
			Map<String, String> dest = loadLang(onDisk);

			if (dest == null)
				return false;

			if(dest.get("archived-lang-version") == null || compareVersion(source.get("archived-lang-version"), dest.get("archived-lang-version"))){
				return false;
			}

			HashMap<String, String> newEntries = new HashMap<String, String>();
			for (String key : source.keySet()) {
				if (!dest.containsKey(key)) {
					newEntries.put(key, source.get(key));
				}
			}

			if (!newEntries.isEmpty()) {
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(onDisk, true), StandardCharsets.UTF_8));
				for (Entry<String, String> entry : newEntries.entrySet())
					writer.append("\n" + entry.getKey() + "=" + entry.getValue());
				writer.close();
				sortFileOnDisk(onDisk);
				Bukkit.getConsoleSender()
						.sendMessage(Core.PREFIX + "Updated " + onDisk.getName() + " language file with missing keys");
			}

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean sortFileOnDisk(File onDisk) {
		try {
			Map<String, String> source = loadLang(onDisk);
			source = sortByKeys(source);
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(onDisk, false), StandardCharsets.UTF_8));
			for (Entry<String, String> entry : source.entrySet()) {
				writer.append("\n" + entry.getKey() + "=" + entry.getValue());
			}
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static Map<String, String> loadLang(InputStream stream, String encoding) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encoding));

			while (reader.ready()) {
				String line = reader.readLine();
				if (line == null)
					continue;
				int index = line.indexOf('=');
				if (index == -1)
					continue;

				String key = line.substring(0, index).trim();
				String value = line.substring(index + 1).trim();

				map.put(key, value);
			}
			reader.close();
		} catch (Exception e) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage(Core.PREFIX_ERROR + "Error reading the language file. Please check the format.");
		}

		return map;
	}

	/**
	 * Compare two semantic versions to see which is newer.
	 * @param sourceVersion - Version of the lang file in jar
	 * @param diskVersion - Version of the lang file on disk
	 * @return - If source is newer than disk
	 */
	private static boolean compareVersion(String sourceVersion, String diskVersion){
		List<Integer> version1Components = Arrays.stream(sourceVersion.split("\\."))
				.map(Integer::parseInt)
				.collect(Collectors.toList());
		List<Integer> version2Components = Arrays.stream(diskVersion.split("\\."))
				.map(Integer::parseInt)
				.collect(Collectors.toList());

		int maxLength = Math.max(version1Components.size(), version2Components.size());

		for (int i = 0; i < maxLength; i++) {
			int v1Component = i < version1Components.size() ? version1Components.get(i) : 0;
			int v2Component = i < version2Components.size() ? version2Components.get(i) : 0;

			if (v1Component > v2Component) {
				return true;
			} else if (v1Component < v2Component) {
				return false;
			}
		}

		return false;
	}

	private static Pattern mDetectEncodingPattern = Pattern.compile("^[a-zA-Z\\.\\-0-9_]+=.+$");

	private static String detectEncoding(File file) throws IOException {
		for (String charset : mValidEncodings) {
			FileInputStream input = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, charset));
			String line = null;
			boolean ok = true;
			while (reader.ready()) {
				line = reader.readLine();
				if (line == null || line.trim().isEmpty())
					continue;
				if (!mDetectEncodingPattern.matcher(line.trim()).matches())
					ok = false;
			}
			reader.close();
			if (ok)
				return charset;
		}
		return "UTF-8";
	}

	private static Map<String, String> loadLang(File file) {
		Map<String, String> map;

		try {
			String encoding = detectEncoding(file);
			if (encoding == null) {
				FileInputStream input = new FileInputStream(file);
				Bukkit.getConsoleSender()
						.sendMessage(Core.PREFIX_ERROR + "Could not detect encoding of lang file. Defaulting to UTF-8");
				map = loadLang(input, "UTF-8");
				input.close();
			}

			FileInputStream input = new FileInputStream(file);
			map = loadLang(input, encoding);
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return map;
	}

	public void setLanguage(String lang) {
		File file = new File(dataFolder, "lang/" + lang);
		if (!file.exists()) {
			Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING
					+ "Language file does not exist. Creating a new file based on en_US. You need to translate the file yourself.");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (file.exists()) {
			InputStream resource = plugin.getResource("lang/en_US.lang");
			injectChanges(resource, file);
			mTranslationTable = loadLang(file);
		} else {
			Bukkit.getConsoleSender()
					.sendMessage(Core.PREFIX_ERROR + "Could not read the language file:" + file.getName());
		}

		if (mTranslationTable == null) {
			mTranslationTable = new HashMap<String, String>();
			Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING + "Creating empty translation table.");
		}
	}

	private static String getStringInternal(String key) {
		String value = mTranslationTable.get(key);

		if (value == null) {
			Bukkit.getConsoleSender().sendMessage(Core.PREFIX + "mTranslationTable has not key: " + key.toString());
			throw new MissingResourceException("", "", key);
		}

		return value.trim();
	}

	private static Pattern mPattern;

	/**
	 * Gets the message and replaces specified values
	 * 
	 * @param key    The message key to find
	 * @param values these are key-value pairs, they should be like: {key1, value1,
	 *               key2, value2,..., keyN,valueN}. keys must be strings
	 */
	public String getString(String key, Object... values) {
		try {
			if (mPattern == null)
				mPattern = Pattern.compile("\\$\\{([\\w\\.\\-]+)\\}");

			HashMap<String, Object> map = new HashMap<String, Object>();

			String name = null;
			for (Object value : values) {
				if (name == null)
					name = (String) value; // This must be a string
				else {
					map.put(name, value);
					name = null;
				}
			}

			String str = getStringInternal(key);
			Matcher m = mPattern.matcher(str);

			String output = str;

			while (m.find()) {
				name = m.group(1);
				Object replace = map.get(name);
				if (replace != null)
					output = output.replaceAll("\\$\\{" + name + "\\}", Matcher.quoteReplacement(replace.toString()));
			}

			return Strings.convertColors(ChatColor.translateAlternateColorCodes('&', output));
		} catch (MissingResourceException e) {
			Bukkit.getConsoleSender().sendMessage(Core.PREFIX_WARNING + " Could not find key: " + key.toString());
			return key;
		}
	}

	public String getString(String key) {
		try {
			return Strings.convertColors(ChatColor.translateAlternateColorCodes('&', getStringInternal(key)));
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Show debug information in the Server console log
	 * 
	 * @param message
	 * @param args
	 */
	public void debug(String message, Object... args) {
		if (Core.getConfigManager().debug) {
			Bukkit.getServer().getConsoleSender().sendMessage(Core.PREFIX_DEBUG + String.format(message, args));
		}
	}

	/**
	 * Show console message
	 *
	 * @param message
	 * @param args
	 */
	public void notice(String message, Object... args) {
		Bukkit.getServer().getConsoleSender().sendMessage(Core.PREFIX + String.format(message, args));
	}

	/**
	 * Show console warning
	 *
	 * @param message
	 * @param args
	 */
	public void warning(String message, Object... args) {
		Bukkit.getServer().getConsoleSender().sendMessage(Core.PREFIX_WARNING + String.format(message, args));
	}

	/**
	 * Show console error
	 *
	 * @param message
	 * @param args
	 */
	public void error(String message, Object... args) {
		Bukkit.getServer().getConsoleSender().sendMessage(Core.PREFIX_ERROR + String.format(message, args));
	}


	private static Map<String, String> sortByKeys(Map<String, String> map) {
		SortedSet<String> keys = new TreeSet<String>(map.keySet());
		Map<String, String> sortedHashMap = new LinkedHashMap<String, String>();
		for (String it : keys) {
			sortedHashMap.put(it, map.get(it));
		}
		return sortedHashMap;
	}

	private static boolean isEmpty(String message) {
		message = ChatColor.stripColor(message);
		return message.isEmpty();
	}

	HashMap<Player, Long> lastMessage = new HashMap<Player, Long>();

	// TODO: This seems borked
	public void playerActionBarMessageQueue(Player player, String message) {
		if (isEmpty(message)) {
			return;
		}

		final String final_message = (BagOfGoldCompat.isSupported()) ? PlaceholderAPICompat.setPlaceholders(player, message) : "";

		Core.getMessages().debug(final_message);

		/*|| ActionAnnouncerCompat.isSupported()*/
		if (  TitleManagerCompat.isSupported() || ActionbarCompat.isSupported()	|| ActionBarAPICompat.isSupported() || CMICompat.isSupported()) {
			long last = 0L;
			long time_between_messages = 80L;
			long delay = 1L, now = System.currentTimeMillis();
			if (lastMessage.containsKey(player)) {
				last = lastMessage.get(player);
				if (now > last + time_between_messages) {
					delay = 1L;
				} else if (now > last)
					delay = time_between_messages - (now - last);
				else
					delay = (last - now) + time_between_messages;
			}

			lastMessage.put(player, now + delay);

			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					playerActionBarMessageNow(player, final_message);
				}
			}, delay);
		} else {
			if( Servers.isMC119OrNewer()){
				player.sendTitle("", message,10,100,10);
			}
			else if (Servers.isMC115OrNewer())
				player.sendTitle("", message);
			else
				player.sendMessage(message);

		}
	}

	/**
	 * Show message to the player using the ActionBar
	 * 
	 * @param player
	 * @param message
	 */
	public void playerActionBarMessageNow(Player player, String message) {
		if (isEmpty(message))
			return;

		message = Strings.convertColors(PlaceholderAPICompat.setPlaceholders(player, message));
			
		if (ActionbarCompat.isSupported()) {
			ActionbarCompat.setMessage(player, message);
		} else if (ActionBarAPICompat.isSupported()) {
			ActionBarAPICompat.setMessage(player, message);
		} else if (CMICompat.isSupported()) {
			CMICompat.sendActionBarMessage(player, message);
		} else {
			if (!Core.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
				if (Servers.isMC115OrNewer())
					player.sendTitle("", message);
				else
					player.sendMessage(message);
		}
	}

	/**
	 * Show learning messages to the player
	 * 
	 * @param player
	 * @param text
	 * @param args
	 */
	public void learn(Player player, String text, Object... args) {
		if (player != null && !CitizensCompat.isNPC(player)
				&& Core.getPlayerSettingsManager().getPlayerSettings(player).isLearningMode() && !isEmpty(text))
			playerBossbarMessage(player, text, args);
	}

	/**
	 * Show message to the player using the BossBar. If no BossBar plugin is
	 * available the player chat will be used.
	 * 
	 * @param player
	 * @param message
	 * @param args
	 */
	public void playerBossbarMessage(Player player, String message, Object... args) {
		if (isEmpty(message))
			return;

		message = Strings.convertColors(PlaceholderAPICompat.setPlaceholders(player, message));

		if (BossBarAPICompat.isSupported()) {
			BossBarAPICompat.addBar(player, String.format(message, args));
		} else if (BarAPICompat.isSupported()) {
			BarAPICompat.setMessageTime(player, String.format(message, args), 5);
		} else if (CMICompat.isSupported()) {
			CMICompat.sendBossBarMessage(player, String.format(message, args));
		} else {
			player.sendMessage(ChatColor.AQUA + getString("core.learn.prefix") + " " + String.format(message, args));
		}
	}

	public void senderSendMessage(final CommandSender sender, String message) {
		if (isEmpty(message))
			return;
		if (sender instanceof Player) {
			Player player = ((Player) sender);
			if (!Core.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
				player.sendMessage(message);
		} else
			sender.sendMessage(message);
	}

}

package one.lindegaard.CustomItemsLib.config;

import java.io.File;
import java.util.LinkedHashMap;

import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager extends AutoConfig {

	public ConfigManager(File file) {

		super(file);

		setCategoryComment("Introduction", "########################################################################"
				+ "\n########################################################################");

		setCategoryComment("general", "This file contains settings which is shared between BagOfGold and MobHunting, "
				+ "\nthis means if you change settings in this file it will affect both plugins."
				+ "\n\n\n########################################################################"
				+ "\ngeneral Settings" + "\n########################################################################");

		setCategoryComment("economy", "########################################################################"
				+ "\nShared Settings" + "\n########################################################################");

		setCategoryComment("reward", "########################################################################"
				+ "\nReward Settings" + "\n########################################################################");

		setCategoryComment("reward.name", "########################################################################"
				+ "\nName Settings" + "\n########################################################################");

		setCategoryComment("reward.itemtype",
				"########################################################################" + "\nReward Type Settings"
						+ "\n########################################################################");

		setCategoryComment("reward.itemtype.skull",
				"########################################################################" + "\nSKULL Settings"
						+ "\n########################################################################" + "\nExamples:"
						+ "\n\nBag of gold: (https://mineskin.org/6875)"
						+ "\n\nThis is the default values for the 'Bag of gold' item."
						+ "\nskull_texture_value: 'eyJ0aW1lc3RhbXAiOjE0ODU5MTIwNjk3OTgsInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM5NmNlMTNmZjYxNTVmZGYzMjM1ZDhkMjIxNzRjNWRlNGJmNTUxMmYxYWRlZGExYWZhM2ZjMjgxODBmM2Y3In19fQ=='"
						+ "\nskull_texture_signature: 'm8u2ChI43ySVica7pcY0CsCuMCGgAdN7c9f/ZOxDZsPzJY8eiDrwxLIh6oPY1rvE1ja/rmftPSmdnbeHYrzLQ18QBzehFp8ZVegPsd9iNHc4FuD7nr1is2FD8M8AWAZOViiwlUKnfd8avb3SKfvFmhmVhQtE+atJYQrXhJwiqR4S+KTccA6pjIESM3AWlbCOmykg31ey7MQWB4YgtRp8NyFD3HNTLZ8alcEXBuG3t58wYBEME1UaOFah45tHuV1FW+iGBHHFWLu1UsAbg0Uw87Pp+KSTUGrhdwSc/55czILulI8IUnUfxmkaThRjd7g6VpH/w+9jLvm+7tOwfMQZlXp9104t9XMVnTAchzQr6mB3U6drCsGnuZycQzEgretQsUh3hweN7Jzz5knl6qc1n3Sn8t1yOvaIQLWG1f3l6irPdl28bwEd4Z7VDrGqYgXsd2GsOK/gCQ7rChNqbJ2p+jCja3F3ZohfmTYOU8W7DJ8Ne+xaofSuPnWODnZN9x+Y+3RE3nzH9tzP+NBMsV3YQXpvUD7Pepg7ScO+k9Fj3/F+KfBje0k6xfl+75s7kR3pNWQI5EVrO6iuky6dMuFPUBfNfq33fZV6Tqr/7o24aKpfA4WwJf91G9mC18z8NCgFR6iK4cPGmkTMvNtxUQ3MoB0LCOkRcbP0i7qxHupt8xE='"
						+ "\n\nBag of gold (alternative): (https://mineskin.org/3384)"
						+ "\nskull_texture_value: 'eyJ0aW1lc3RhbXAiOjE0NzQzMzI0MzY1MDYsInByb2ZpbGVJZCI6IjNlMjZiMDk3MWFjZDRjNmQ5MzVjNmFkYjE1YjYyMDNhIiwicHJvZmlsZU5hbWUiOiJOYWhlbGUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg2NzczZDc0Y2Y1MDhmZDc3Yzc4MmZmZDI5ZGYyZmU0N2ZiNzE0YjViMGQ3ZGU2N2Q1Mjg2OTMxZTJmMWRmMiJ9fX0='"
						+ "\nskull_texture_signature: 'JdvJksowuxYQ0eqf56J+Dmczg7zvlw2DbIc58Q33kRt65uMUNn2iRCQsbNpztC1cAAgyYMOyFDiOUZQeIK03CSRoPLDtWp2u501YoGKqhjgrE0V0UDh3JetWKz4Ob0KmATtY+4R2vSoMjHFEFppM0Oq+8ZER12FAiVEMAzeseFN3Z9fWAMc/V10LoquGBpq6ExTfSCEEMDEGZopF1T8ZBKL0vf4DVendfz4v3yl7bRBzISZEAnF+ECTa9z36r8HRqS8+s0eO/AWYQcRaKIu9H+wSK5F/1v+rgifeSlMAnt1Na8m1b5tMfNuq6pXxWCq4nUGgYVTOLUinqs9ZcFz3Z6Mtx5YtymKk2M0mzxmTm9+AeOL4s3K/UrJYQlcmLBJSv4hd6EigJXoashzWNCHKmFDYCdEhh4FArq4G9vRZtoudcTeMsvi0VmXIgER8U5iSfoTtzXcGbf/GT0ECtgfeA40f5oCqyE4nXreudMmvlDCBr/KHbILQWeeH/jhtYqQ6OwJb3Ji2Bs9F5fQmICSqk7X4yKzexf8rdDhOG1z+/TCot7K8unPVuQx46sXPeP7t2hCiHOXMAnOMt8vuL3gQUURIEM6fMryjmlKsgvk8Jo0gawavRCIZQtA6vT0JRRnSAchzEOA7QP1iiVV3LnwX9Yqw7oMJ/+REV1hWesuzDOc='"
						+ "\n\nChest: (https://mineskin.org/3136)"
						+ "\n\nUse these values if you want the item to be a small 'Treasure chest'."
						+ "\nskull_texture_value: 'eyJ0aW1lc3RhbXAiOjE0NzI4Mzk3Nzk2ODMsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJJbnZlbnRpdmVHYW1lcyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY5NDcxMjQ1YmNhN2M0ZmUwNjQ0MGQ5YjRiOWY3NDIxN2VkNzM0M2FhZDU5YTc5MThiMWExZDYxZDhiYTZkYSJ9fX0='"
						+ "\nskull_texture_signature: 'lVA2QIbvybpzhcXof5yWz/7nkHdhG/3MGO+1DyD1txdRCALV6BRwsDUBwIUg06MkLUpBkjmiOvFcCRgal/jDE/xkkJPyk2tb/w4NtQ5PiPiAe0oInVnuiSIVFIE4tnsCdvX0joll3uKwVu6XY3t1KEsqJATcPhA5hslVn1iOp/IfMziIfuCzzob04rScpwcw0mLNtbtbMVAl6LYR9gXVuOkAfXujuYq4lbI/iW0yuLxSAzr8i9QWBP2ftup4qQHwocQRTdUE6/G5G9LwJWXhhnqKWjgjfvL0y2FRFJkgN1cvuq7DvUDBVsePnRIHwU5YvBPMjcZe/KE8VPTSodsN84/+++5p95Puxe1DXMX822xR71IQsxM7eax7Ffrr/Tzxw2rSDh9ivGGlRAB85OHwp/ouUgWNSrT8inNMYImque9EuZku9p3OFet8iZsFhkMXANeNtTVL7LKV7/L/0YWwoeyBnw5QQqvGyWKw3dac5eDkRNCyCtdDIntM5vsd8FxnIFj36zxLWgmrJmOM9hg5PBM4gcDxxryBcug8jSe+W9XDU39OOJotXajj8dgSL8yUn+d7l4Qvat/vJbAE8lonMl7P0P9QBPzmcIUvlRMuHSpRZQYkoCbwc2Filahd/5INtm7I4Y28XYzzupdwLk3cavKfOloL5YrWNqaZr/+9Tbk='"
						+ "\n\nBirthday present: (https://mineskin.org/4743)"
						+ "\n\nUse these values if you want the reward to be a 'Birthday present'."
						+ "\nskull_texture_value: 'eyJ0aW1lc3RhbXAiOjE0Nzk5MzEzNDMxMjgsInByb2ZpbGVJZCI6IjNlMjZiMDk3MWFjZDRjNmQ5MzVjNmFkYjE1YjYyMDNhIiwicHJvZmlsZU5hbWUiOiJOYWhlbGUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2NmNDRkZjIzMjBiNzYzMTI0N2FhZGY1OWMwZWNlOTdhNGJiNTdkZjI4YzFjZWU3OTM0ZjZhZTI4YWY4OTg5In19fQ=='"
						+ "\nskull_texture_signature: 'k1xQ6E1NuxG1ZN7nlQqRJltYrJn44XHVhNA9pSEu2Pt2mkuixMxhIDj2Tg6o+JWlTyGfXtPVWLxygeGymmeSGaVcmDTaCALg7PL11ZfSzSWSxaIufNbj1EcSi264jg5FrAa/2/DnFsgu16wjlWiIGtjCzgx2QabY8YofoPKw6Y6Y5FHZJVXpT8Rsxs8ok6ZHtfm/ZyyTgvRSzh2mKmVyQIYJ1ZKxuqWhDQfbtBpu3dlEzMAEJo85Dvb7uIFYa7WFitjFJue/c9qpqAnazWFLrx33nYpjjeYhcfAvsaNQW3JVFEkyxzEgzOHbdsbiZcqTCwO+49whu175xOqT7XhouEubDT7A3H1jiSvQvkUZJv/GzUF4qFYHSfxhr6OWoBrRGwWmPdcrYx7fUWKo43CAqa5inaiTV4gU70BWrx5i3LhIJxpnspAyTXs8tZBxeoh8IizWD7uXkYYqh3j9cwuHoxfwZuMpOx9CPTC6R/YwJ1YK5OgJBY1+QhNw+NOilWT3jTok82elFvOLm3a5yLyVs+/UPmLD7rZsFm7/DD3VnRcpgjKRiyy2j9vYsYLyNE2BVLVJxBVk2yyy9u7L4VR6PO+8v2dh9DQl7vM2ORCxKPl2lt6woHWM2+eT1PXr16LtMtAOGYT8mlKFhp8Ou2+9fu4AqWkX7n3swU6XLiK5cJs='");

		setCategoryComment("reward.itemtype.item",
				"########################################################################" + "\nITEM Settings"
						+ "\n########################################################################");

		setCategoryComment("reward.itemtype.killed",
				"########################################################################" + "\nKILLED Settings"
						+ "\n########################################################################");

		setCategoryComment("reward.itemtype.killer",
				"########################################################################" + "\nKILLED Settings"
						+ "\n########################################################################");

		setCategoryComment("reward.itemtype.gringotts",
				"########################################################################"
						+ "\nGRINGOTTS_STYLE Settings"
						+ "\n########################################################################"
						+ "\nOBS GRINGOTTS_STYLE is still being tested! The original Gringotts seems to die with Minecraft 1.14, but I have made a Gringotts style"
						+ "\nfor Gringotts lovers (https://www.spigotmc.org/resources/gringotts.42071/)");

		setCategoryComment("reward.command", "########################################################################"
				+ "\nCommand Settings" + "\n########################################################################");

		setCategoryComment("reward.other",
				"########################################################################" + "\nOther reward settings"
						+ "\n########################################################################");
		
		setCategoryComment("plugins",
				"########################################################################"
						+ "\nIntegration to other plugins."
						+ "\n########################################################################");

		setCategoryComment("plugins.bossbarapi",
				"########################################################################" + "\nBossBarAPI"
						+ "\n########################################################################");

		setCategoryComment("plugins.barapi", "########################################################################"
				+ "\nBarApi" + "\n########################################################################");

		setCategoryComment("plugins.titlemanager",
				"########################################################################" + "\nTitleManager"
						+ "\n########################################################################");

		setCategoryComment("plugins.titleapi",
				"########################################################################" + "\nTitleApi"
						+ "\n########################################################################");

		setCategoryComment("plugins.actionbar",
				"########################################################################" + "\nActionbar"
						+ "\n########################################################################");

		setCategoryComment("plugins.actionbarapi",
				"########################################################################" + "\nActionbarAPI"
						+ "\n########################################################################");

		setCategoryComment("plugins.actionannouncer",
				"########################################################################" + "\nActionAnnouncer"
						+ "\n########################################################################");

		setCategoryComment("plugins.cmi", "########################################################################"
				+ "\nCMI" + "\n########################################################################");

		setCategoryComment("database",
				"########################################################################" + "\nDatabase Settings."
						+ "\n########################################################################");

	}

	public static int getConfigVersion(File file) {
		if (!file.exists())
			return -1;

		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		return config.getInt("shared.config_version", config.contains("shared.debug") == true ? 0 : -1);
	}

	// #####################################################################################
	// General settings
	// #####################################################################################

	@ConfigField(name = "language", category = "general", comment = "The language (file) to use. You can put the name of the language file as the language code "
			+ "\n(eg. en_US, fr_FR, hu_HU, pt_BR, zh_CN, ru_RU ect.) or you can specify the name of a custom file without the .lang\nPlease check the lang/ folder for a list of all available translations.")
	public String language = "en_US";

	@ConfigField(name = "debug", category = "general", comment = "Enable/disable debug information")
	public boolean debug = true;

	@ConfigField(name = "newplayer_learning_mode", category = "general", comment = "When a new playerjoins the server he will by default start"
			+ "\nin 'LEARNING MODE' and get extra information about when he get rewards and not,"
			+ "\nwhen killing Mobs. The player can disable this InGame by using the command '/mobhunt learn'")
	public boolean learningMode = false;

	@ConfigField(name = "config_version", category = "general", comment = "Config version. Do NOT change this!")
	public int configVersion = 0;

	// #####################################################################################
	// eConomy settings
	// #####################################################################################

	@ConfigField(name = "number_format", category = "economy", comment = "Here you can change the way the numbers is formatted when you use BagOfGold as an EconomyPlugin.")
	public String numberFormat = "#.#####";

	@ConfigField(name = "reward_rounding", category = "economy", comment = "Rounding of rewards when you uses a range or %. (ex creeperPrize=10:30) the reward."
			+ "\nAll numbers except 0 can be used. "
			+ "\nSet rounding_reward=1 if you want integers. IE. 10,11,12,13,14..."
			+ "\nSet rounding_reward=0.01 if you want 2 decimals 10.00, 10.01, 10.02... integers."
			+ "\nSet rounding_reward=5 if you want multipla of 5 IE. 10,15,20,25..."
			+ "\nSet rounding_reward=2 if you want multipla of 2 IE. 10,12,14,16...")
	public double rewardRounding = 1;

	@ConfigField(name = "limit_per_bag", category = "economy", comment = "If you only want the bags to be able to contain a "
			+ "\ncertain amount of gold you can set the limit here. Default 10000. "
			+ "\nSet limit_per_bag: 9999999999 to disable the feature.")
	public double limitPerBag = 10000;

	@ConfigField(name = "minimum_reward", category = "economy", comment = "This is the minimum reward which will which will be paid to the player 0.01 will be fine"
			+ "\nin most installation, but Gringott users who want very low rewards (like 0.001  for killing"
			+ "\na mob) will have to lower the minimum reward. Remember that some multipliers are less than 1"
			+ "\n and grinding detection and penalties. The minimum_reward should therefor be less than 10%"
			+ "\n of smallest reward. In the Gringotts example minimum_reward should be 0.0001 or 0.00005.")
	public double minimumReward = 0.01;

	@ConfigField(name = "limit_per_bag", category = "economy", comment = "If you only want the bags to be able to contain a "
			+ "\ncertain amount of gold you can set the limit here. Set limit_per_bag: 9999999999 to disable the limit.")
	public double limitPerBagOld = 10000;

	// #####################################################################################
	// Reward Settings
	// #####################################################################################

	@ConfigField(name = "reward_name", category = "reward.name", comment = "This is the name of the currency which will be used in the displayname. This can't be empty.")
	public String bagOfGoldName = "Bag Of Gold";

	@ConfigField(name = "reward_name_plural", category = "reward.name", comment = "This is the name of the reward in plural. This can't be empty.")
	public String bagOfGoldNamePlural = "Bag of gold";

	@ConfigField(name = "text_color", category = "reward.name", comment = "Here you can set of the color of the number above the dropped item. \nUse color names like WHITE, RED, BLUE, GOLD")
	public String rewardTextColor = "GOLD";

	@ConfigField(name = "show_displayname", category = "reward.name", comment = "Here you can hide the displayname when the bag of gold is an floating item in a world"
			+ "\nlike for normal Minecraft items."
			+ "\nYou will still be able to see the displayname and value in the player Inventory.")
	public boolean showCustomDisplayname = true;

	@ConfigField(name = "command_alias", category = "reward.command", comment = "Here you can chance the command /mh money ... and /bag money... to /mh <alias> ... and /bag <alias>..."
			+ "\nExample: gold,bag,silver,coin,???? ")
	public String commandAlias = "money";

	@ConfigField(name = "reward_itemtype", category = "reward.itemtype", comment = "Here you can set the type of item to be dropped."
			+ "\nYou can choose between \"ITEM\",\"KILLED\",\"SKULL\",\"KILLER\", \"GRINGOTTS_STYLE\". The default is SKULL."
			+ "\nThe value will be showed above the item." + "\nITEM: The reward is dropped as a normal Minecraft item."
			+ "\nKILLED: The reward is dropped as the head of the mob/player you killed."
			+ "\nSKULL: The reward is dropped as a SKULL with a custom texture. You can generate custom texture value"
			+ "\nand custom texture signature at http://mineskin.org"
			+ "\nKILLER: The reward is dropped as the killers head."
			+ "\nGRINGOTTS_STYLE: The reward is dropped as Gringotts Items."
			+ "\n\nOBS: If the Gringotts plugin is installed and support not disabled, the droped item will be the Gringotts chosen item.")
	public String rewardItemtype = "SKULL";

	// #####################################################################################
	// ITEM style
	// #####################################################################################
	@ConfigField(name = "itemtype", category = "reward.itemtype.item", comment = "Here you can set which item should be used when you have "
			+ "\nchosen reward_itemtype: ITEM. " + "\nUse Minecraft Item names like: "
			+ "\nGOLD_NUGGET, DIAMOND, GOLD_INGOT, EMERALD, GOLDEN_APPLE ")
	public String rewardItem = "GOLD_INGOT";

	@ConfigField(name = "displayname_format", category = "reward.itemtype.item", comment = "This is the displayname format of BagOfGold (ITEM) items.")
	public String itemDisplayNameFormat = "&6{value}&r";

	@ConfigField(name = "displayname_format_no_value", category = "reward.itemtype.item", comment = "This is the displayname format of BagOfGold (ITEM) items when the value is 0.")
	public String itemDisplayNameFormatNoValue = "";

	// #####################################################################################
	// SKULL style
	// #####################################################################################
	@ConfigField(name = "displayname_format", category = "reward.itemtype.skull", comment = "This is the displayname format of BagOfGold (SKULL) items.")
	public String bagOfGoldDisplayNameFormat = "&6{name} &6({value})&r";

	@ConfigField(name = "skull_texture_value", category = "reward.itemtype.skull", comment = "This is the Custom Texture Value generated at http://mineskin.org")
	public String skullTextureValue = "eyJ0aW1lc3RhbXAiOjE0ODU5MTIwNjk3OTgsInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM5NmNlMTNmZjYxNTVmZGYzMjM1ZDhkMjIxNzRjNWRlNGJmNTUxMmYxYWRlZGExYWZhM2ZjMjgxODBmM2Y3In19fQ==";

	@ConfigField(name = "skull_texture_signature", category = "reward.itemtype.skull", comment = "This is the Custom Texture Signature generated at http://mineskin.org")
	public String skullTextureSignature = "m8u2ChI43ySVica7pcY0CsCuMCGgAdN7c9f/ZOxDZsPzJY8eiDrwxLIh6oPY1rvE1ja/rmftPSmdnbeHYrzLQ18QBzehFp8ZVegPsd9iNHc4FuD7nr1is2FD8M8AWAZOViiwlUKnfd8avb3SKfvFmhmVhQtE+atJYQrXhJwiqR4S+KTccA6pjIESM3AWlbCOmykg31ey7MQWB4YgtRp8NyFD3HNTLZ8alcEXBuG3t58wYBEME1UaOFah45tHuV1FW+iGBHHFWLu1UsAbg0Uw87Pp+KSTUGrhdwSc/55czILulI8IUnUfxmkaThRjd7g6VpH/w+9jLvm+7tOwfMQZlXp9104t9XMVnTAchzQr6mB3U6drCsGnuZycQzEgretQsUh3hweN7Jzz5knl6qc1n3Sn8t1yOvaIQLWG1f3l6irPdl28bwEd4Z7VDrGqYgXsd2GsOK/gCQ7rChNqbJ2p+jCja3F3ZohfmTYOU8W7DJ8Ne+xaofSuPnWODnZN9x+Y+3RE3nzH9tzP+NBMsV3YQXpvUD7Pepg7ScO+k9Fj3/F+KfBje0k6xfl+75s7kR3pNWQI5EVrO6iuky6dMuFPUBfNfq33fZV6Tqr/7o24aKpfA4WwJf91G9mC18z8NCgFR6iK4cPGmkTMvNtxUQ3MoB0LCOkRcbP0i7qxHupt8xE=";

	// #####################################################################################
	// KILLED style
	// #####################################################################################

	@ConfigField(name = "displayname_format", category = "reward.itemtype.killed", comment = "This is the displayname format of BagOfGold (KILLED) items.")
	public String killedHeadDisplayNameFormat = "&6{name} &6({value})&r";
	@ConfigField(name = "displayname_format_no_value", category = "reward.itemtype.killed", comment = "This is the displayname format of BagOfGold (KILLED) items when the value is 0.")
	public String killedHeadDisplayNameFormatNoValue = "&6{name}&r";

	// #####################################################################################
	// KILLER style
	// #####################################################################################

	@ConfigField(name = "displayname_format", category = "reward.itemtype.killer", comment = "This is the displayname format of BagOfGold (KILLER) items.")
	public String killerHeadDisplayNameFormat = "&6{name} &6({value})&r";
	@ConfigField(name = "displayname_format_no_value", category = "reward.itemtype.killer", comment = "This is the displayname format of BagOfGold (KILLER) items when the value is 0.")
	public String killerHeadDisplayNameFormatNoValue = "&6{name}&r";

	// #####################################################################################
	// Gringotts style
	// #####################################################################################
	@ConfigField(name = "denomination", category = "reward.itemtype.gringotts", comment = "If you want to have an Gringotts style economy you can set "
			+ "\n'reward_itemtype: GRINGOTTS_STYLE' and then set the value of the"
			+ "\nvalue of the items here. You can add as many items as you want, but be"
			+ "\ncareful when you choose the value of the item so you dont ruin the"
			+ "\nserver economy. The Gringoots defaul values is EMERALD=1 and EMERALD_BLOCK=9"
			+ "\nAnother good combination would be GOLD_NUGGET=1, GOLD_INGOT=9, GOLD_BLOCK=81"
			+ "\nChoose from this list: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html"
			+ "\nThe value of the denomination must be desending.")
	public LinkedHashMap<String, String> gringottsDenomination = new LinkedHashMap<String, String>();
	{
		gringottsDenomination.put("EMERALD_BLOCK", "9");
		gringottsDenomination.put("EMERALD", "1");
	}

	@ConfigField(name = "deny_hoppers_to_pickup_rewards", category = "reward.other", comment = "Dark room mobspawners usually collect items in a HOPPER. This is allowed by default."
			+ "\nIf you want to deny HOPPERS to collect MobHunting Money rewards "
			+ "\nset \"deny_hoppers_to_pickup_rewards\"=false")
	public boolean denyHoppersToPickUpRewards = true;
	
	// #####################################################################################
	// Plugin integration
	// #####################################################################################

	@ConfigField(name = "enable_integration_titleapi", category = "plugins.titleapi", comment = "Enable/Disable integration with TitleAPI")
	public boolean enableIntegrationTitleAPI = true;

	@ConfigField(name = "enable_integration_titlemanager", category = "plugins.titlemanager", comment = "Enable/Disable integration with TitleManger. If you want messages in player chat you can set this to true."
			+ "\nhttps://www.spigotmc.org/resources/titlemanager.1049/")
	public boolean enableIntegrationTitleManager = true;

	@ConfigField(name = "enable_integration_actionbar", category = "plugins.actionbar", comment = "Enable/Disable integration with Actionbar. If you want messages in player chat you can set this to true.")
	public boolean enableIntegrationActionbar = true;

	@ConfigField(name = "enable_integration_actionbarapi", category = "plugins.actionbarapi", comment = "Enable/Disable integration with ActionBarAPI. If you want messages in player chat you can set this to true."
			+ "\nhttps://www.spigotmc.org/resources/actionbarapi_1_8_1_9_1_10.1315/")
	public boolean enableIntegrationActionBarAPI = true;

	@ConfigField(name = "enable_integration_actionannouncer", category = "plugins.actionannouncer", comment = "Enable/Disable integration with ActionAnnouncer. If you want messages in player chat you can set this to true."
			+ "\nhttps://www.spigotmc.org/resources/actionannouncer.1320/")
	public boolean enableIntegrationActionAnnouncer = true;

	@ConfigField(name = "enable_integration_bossbarapi", category = "plugins.bossbarapi", comment = "Enable/Disable integration with BossBarAPI. If you want messages in player chat you can set this to true.")
	public boolean enableIntegrationBossBarAPI = true;

	@ConfigField(name = "enable_integration_barapi", category = "plugins.barapi", comment = "Enable/Disable integration with BarAPI. If you want messages in player chat you can set this to true."
			+ "\nhttps://dev.bukkit.org/projects/bar_api")
	public boolean enableIntegrationBarAPI = true;

	@ConfigField(name = "enable_integration_cmi", category = "plugins.cmi", comment = "Enable/Disable integration with CMI."
			+ "\nhttps://www.spigotmc.org/resources/cmi-270-commands-insane-kits-portals-essentials-economy-mysql-sqlite-much-more.3742/")
	public boolean enableIntegrationCMI = true;

	@ConfigField(name = "enable_integration_protocollib", category = "plugins.protocollib", comment = "Enable/Disable integration with ProtocolLib."
			+ "\nhttps://www.spigotmc.org/resources/protocollib.1997/")
	public boolean enableIntegrationProtocolLib = true;

	// #####################################################################################
	// Database
	// #####################################################################################
	@ConfigField(name = "type", category = "database", comment = "Type of database to use. Valid values are: sqlite, mysql")
	public String databaseType = "sqlite";

	@ConfigField(name = "database_name", category = "database")
	public String databaseName = "customitemslib";

	@ConfigField(name = "username", category = "database.mysql")
	public String databaseUsername = "user";

	@ConfigField(name = "password", category = "database.mysql")
	public String databasePassword = "password";

	@ConfigField(name = "host", category = "database.mysql")
	public String databaseHost = "localhost:3306";

	@ConfigField(name = "useSSL", category = "database.mysql")
	public String databaseUseSSL = "false";

	@ConfigField(name = "save_period", category = "database", comment = "Time between saving data to disk in ticks (20 ticks ~ 1 sec) This number must be higher that 1200 ticks = 2 minutes,"
			+ "\nbut I recommend to save every 5th minute = 6000 ticks")
	public int savePeriod = 6000;

	@ConfigField(name = "database_version", category = "database", comment = "This is the database layout version. Mostly for internal use and you should not need"
			+ "\nto change this value. In case you decide to delete your database and let it recreate"
			+ "\nor if you change database type sqlite/mysql you should set this value to 0 again.")
	public int databaseVersion = 0;

}

package metadev.digital.metacustomitemslib;

import org.bukkit.plugin.java.JavaPlugin;

public class MetaCustomItemsLib extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Meta Custom Items Lib enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Meta Custom Items Lib disabled");
    }
}

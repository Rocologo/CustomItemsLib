package metadev.digital.metacustomitemslib.update;

import metadev.digital.metacustomitemslib.Core;
import java.util.concurrent.ExecutionException;

public class UpdateManager {
    private Core plugin;
    private UpdateChecker pluginUpdateChecker;
    private UpdateChecker.UpdateResult lastResult;

    public UpdateManager(Core plugin) {
        this.plugin = plugin;
        this.lastResult = null;

        this.pluginUpdateChecker = UpdateChecker.init(this.plugin, 117869);
        isInitialized();
    }

    public void isInitialized() {
        if(UpdateChecker.isInitialized()){
            Core.getMessages().debug("Update checker has been properly initialized");
        }
        else{
            Core.getMessages().error("Update checker failed to properly initialize");
        }
    }

    public void handleUpdateCheck() {
        if (UpdateChecker.isInitialized()){
            Core.getMessages().notice("Checking SpigotMc.org for available updates...");
            lastResult = pluginUpdateChecker.getLastResult();

            // If it hasn't been run, or it gave an unsatisfactory answer last time it was called, ping the API again
            // Catch and rerun in all cases where the status may have changed if a user has not restarted in some time
            if(lastResult == null || lastResult.getReason() == UpdateChecker.UpdateReason.UNKNOWN_ERROR
                    || lastResult.getReason() == UpdateChecker.UpdateReason.COULD_NOT_CONNECT
                    || lastResult.getReason() == UpdateChecker.UpdateReason.UNAUTHORIZED_QUERY
                    || lastResult.getReason() == UpdateChecker.UpdateReason.INVALID_JSON
                    || lastResult.getReason() == UpdateChecker.UpdateReason.UNRELEASED_VERSION
                    || lastResult.getReason() == UpdateChecker.UpdateReason.UP_TO_DATE
            ) {
                try {
                    lastResult = pluginUpdateChecker.requestUpdateCheck().get();
                }
                catch (ExecutionException | InterruptedException e) {
                    Core.getMessages().debug("UpdateManager threw Exception or was Interrupted when pinging Spigot API");
                }
            }

            processResult(lastResult);
        }
    }

    private void processResult(UpdateChecker.UpdateResult result) {

        switch (result.getReason()){
            case null:
                Core.getMessages().warning("[Updater] ===============================================");
                Core.getMessages().warning("[Updater] ==== Meta Custom Items Lib Updater Checker ====");
                Core.getMessages().warning("[Updater] ===============================================");
                Core.getMessages().warning("[Updater] Failed to connect to SpigotMC to check for updates or SpigotMC took too long to respond.");
                break;
            case NEW_UPDATE:
                Core.getMessages().warning("[Updater] ===============================================");
                Core.getMessages().warning("[Updater] ====      Update Available on SpigotMc     ====");
                Core.getMessages().warning("[Updater] ===============================================");
                Core.getMessages().warning("[Updater] Download the latest version " + result.getNewestVersion() + "at https://www.spigotmc.org/resources/meta-customitemslib.117869/");
                break;
            case COULD_NOT_CONNECT:
                Core.getMessages().warning("[Updater] ===============================================");
                Core.getMessages().warning("[Updater] ==== Meta Custom Items Lib Updater Checker ====");
                Core.getMessages().warning("[Updater] ===============================================");
                Core.getMessages().warning("[Updater] Failed to connect to SpigotMC to check for updates or SpigotMC took too long to respond. Does the server have internet?");
            case INVALID_JSON:
                Core.getMessages().error("[Updater] ===============================================");
                Core.getMessages().error("[Updater] ==== Meta Custom Items Lib Updater Checker ====");
                Core.getMessages().error("[Updater] ===============================================");
                Core.getMessages().error("[Updater] Spigot API returned unexpected or invalid JSON object on version check.");
                Core.getMessages().error("[Updater] Please report this at https://github.com/Metadev-Digital/CustomItemsLib/issues/");
            case UNAUTHORIZED_QUERY:
                Core.getMessages().error("[Updater] ===============================================");
                Core.getMessages().error("[Updater] ==== Meta Custom Items Lib Updater Checker ====");
                Core.getMessages().error("[Updater] ===============================================");
                Core.getMessages().error("[Updater] Spigot API blocked this request. Please report this at https://github.com/Metadev-Digital/CustomItemsLib/issues/");
                break;
            case UNRELEASED_VERSION:
                Core.getMessages().notice("[Updater] ===============================================");
                Core.getMessages().notice("[Updater] ====      Development Build Detected       ====");
                Core.getMessages().notice("[Updater] ===============================================");
                Core.getMessages().notice("[Updater] Your installed version" + result.getNewestVersion() + " is currently ahead of the public release. Please ensure you back up regularly while running a development build.");
                break;
            case UNKNOWN_ERROR, UNSUPPORTED_VERSION_SCHEME:
                Core.getMessages().error("[Updater] ===============================================");
                Core.getMessages().error("[Updater] ==== Meta Custom Items Lib Updater Checker ====");
                Core.getMessages().error("[Updater] ===============================================");
                Core.getMessages().error("[Updater] An unknown error occurred while checking for the latest version. Please report this at https://github.com/Metadev-Digital/CustomItemsLib/issues/");
                break;
            case UP_TO_DATE:
                Core.getMessages().notice("[Updater] Your build of Meta Custom Items Lib is up to date!");
                break;
        }
    }
}

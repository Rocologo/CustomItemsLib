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

    private UpdateChecker.UpdateResult handleUpdateCheck() {
        if (UpdateChecker.isInitialized()){
            Core.getMessages().notice("Checking SpigotMc.org for available updates...");
            UpdateChecker.UpdateResult lastRanResult = pluginUpdateChecker.getLastResult();

            // If it hasn't been run, or it gave an unsatisfactory answer last time it was called, ping the API again
            // Catch and rerun in all cases where the status may have changed if a user has not restarted in some time
            if(lastRanResult == null || lastRanResult.getReason() == UpdateChecker.UpdateReason.UNKNOWN_ERROR
                    || lastRanResult.getReason() == UpdateChecker.UpdateReason.COULD_NOT_CONNECT
                    || lastRanResult.getReason() == UpdateChecker.UpdateReason.UNAUTHORIZED_QUERY
                    || lastRanResult.getReason() == UpdateChecker.UpdateReason.INVALID_JSON
                    || lastRanResult.getReason() == UpdateChecker.UpdateReason.UNRELEASED_VERSION
                    || lastRanResult.getReason() == UpdateChecker.UpdateReason.UP_TO_DATE
            ) {
                try {
                    return pluginUpdateChecker.requestUpdateCheck().get();
                }
                catch (ExecutionException | InterruptedException e) {
                    Core.getMessages().debug("UpdateManager threw Exception or was Interrupted when pinging Spigot API");
                }
            }
        }
        return null;
    }

    public void processCheckResultInConsole() {
        this.lastResult = handleUpdateCheck();

        if(lastResult != null){
            switch (lastResult.getReason()){
                case NEW_UPDATE:
                    Core.getMessages().warning("===============================================");
                    Core.getMessages().warning("====      Update Available on SpigotMc     ====");
                    Core.getMessages().warning("===============================================");
                    Core.getMessages().warning("Download the latest version " + lastResult.getNewestVersion() + "at https://www.spigotmc.org/resources/meta-customitemslib.117869/");
                    break;
                case COULD_NOT_CONNECT:
                    Core.getMessages().warning("===============================================");
                    Core.getMessages().warning("==== Meta Custom Items Lib Updater Checker ====");
                    Core.getMessages().warning("===============================================");
                    Core.getMessages().warning("Failed to connect to SpigotMC to check for updates or SpigotMC took too long to respond. Does the server have internet?");
                case INVALID_JSON:
                    Core.getMessages().error("===============================================");
                    Core.getMessages().error("==== Meta Custom Items Lib Updater Checker ====");
                    Core.getMessages().error("===============================================");
                    Core.getMessages().error("Spigot API returned unexpected or invalid JSON object on version check.");
                    Core.getMessages().error("Please report this at https://github.com/Metadev-Digital/CustomItemsLib/issues/");
                case UNAUTHORIZED_QUERY:
                    Core.getMessages().error("===============================================");
                    Core.getMessages().error("==== Meta Custom Items Lib Updater Checker ====");
                    Core.getMessages().error("===============================================");
                    Core.getMessages().error("Spigot API blocked this request. Please report this at https://github.com/Metadev-Digital/CustomItemsLib/issues/");
                    break;
                case UNRELEASED_VERSION:
                    Core.getMessages().notice("===============================================");
                    Core.getMessages().notice("====      Development Build Detected       ====");
                    Core.getMessages().notice("===============================================");
                    Core.getMessages().notice("Your installed version" + lastResult.getNewestVersion() + " is currently ahead of the public release. Please ensure you back up regularly while running a development build.");
                    break;
                case UNKNOWN_ERROR, UNSUPPORTED_VERSION_SCHEME:
                    Core.getMessages().error("===============================================");
                    Core.getMessages().error("==== Meta Custom Items Lib Updater Checker ====");
                    Core.getMessages().error("===============================================");
                    Core.getMessages().error("An unknown error occurred while checking for the latest version. Please report this at https://github.com/Metadev-Digital/CustomItemsLib/issues/");
                    break;
                case UP_TO_DATE:
                    Core.getMessages().notice("Your build of Meta Custom Items Lib is up to date!");
                    break;
            }
        }
    }

    public String processCheckResultInChat() {
        this.lastResult = handleUpdateCheck();
        if(lastResult != null){
            switch (lastResult.getReason()) {
                case UP_TO_DATE:
                    return "Your build of Meta Custom Items Lib is up to date!";
                case NEW_UPDATE:
                    return "Download the latest version " + lastResult.getNewestVersion() + "at https://www.spigotmc.org/resources/meta-customitemslib.117869/";
                case COULD_NOT_CONNECT:
                    return "Failed to connect to SpigotMC to check for updates or SpigotMC took too long to respond. Does the server have internet";
                case INVALID_JSON:
                    return "Spigot API returned unexpected or invalid JSON object on version check.";
                case UNAUTHORIZED_QUERY:
                    return "Spigot API blocked this request.";
                case UNRELEASED_VERSION:
                    return "Your installed version" + lastResult.getNewestVersion() + " is currently ahead of the public release.";
                case UNKNOWN_ERROR, UNSUPPORTED_VERSION_SCHEME:
                    return "An unknown error occurred while checking for the latest version.";
            }
        }
            return "Update checker failed to properly initialize.";
    }
}

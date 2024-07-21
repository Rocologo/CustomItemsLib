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
            Core.getMessages().error(Core.getMessages().getString("core.commands.update.fail"));
        }
    }

    private UpdateChecker.UpdateResult handleUpdateCheck() {
        if (UpdateChecker.isInitialized()){
            Core.getMessages().notice(Core.getMessages().getString("core.commands.update.check"));
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
                    Core.getMessages().warning("====      " + Core.getMessages().getString("core.commands.update.header_update_available") + "    ====");
                    Core.getMessages().warning("===============================================");
                    Core.getMessages().warning(Core.getMessages().getString("core.commands.update.new_update", "newversion", lastResult.getNewestVersion()));
                    break;
                case COULD_NOT_CONNECT:
                    Core.getMessages().warning("===============================================");
                    Core.getMessages().warning("====      " + Core.getMessages().getString("core.commands.update.header") + "    ====");
                    Core.getMessages().warning("===============================================");
                    Core.getMessages().warning(Core.getMessages().getString("core.commands.update.could-not-connect"));
                case INVALID_JSON:
                    Core.getMessages().error("===============================================");
                    Core.getMessages().error("====      " + Core.getMessages().getString("core.commands.update.header") + "    ====");
                    Core.getMessages().error("===============================================");
                    Core.getMessages().error(Core.getMessages().getString("core.commands.update.invalid"));
                case UNAUTHORIZED_QUERY:
                    Core.getMessages().error("===============================================");
                    Core.getMessages().error("====      " + Core.getMessages().getString("core.commands.update.header") + "    ====");
                    Core.getMessages().error("===============================================");
                    Core.getMessages().error(Core.getMessages().getString("core.commands.update.unauthorized"));
                    break;
                case UNRELEASED_VERSION:
                    Core.getMessages().notice("===============================================");
                    Core.getMessages().notice("====      " + Core.getMessages().getString("core.commands.update.header_development") + "    ====");
                    Core.getMessages().notice("===============================================");
                    Core.getMessages().notice(Core.getMessages().getString("core.commands.update.development", "currentversion", lastResult.getNewestVersion()));
                    break;
                case UNKNOWN_ERROR, UNSUPPORTED_VERSION_SCHEME:
                    Core.getMessages().error("===============================================");
                    Core.getMessages().error("====      " + Core.getMessages().getString("core.commands.update.header") + "    ====");
                    Core.getMessages().error("===============================================");
                    Core.getMessages().error(Core.getMessages().getString("core.commands.update.unknown"));
                    break;
                case UP_TO_DATE:
                    Core.getMessages().notice(Core.getMessages().getString("core.commands.update.no_update"));
                    break;
            }
        }
    }

    public String processCheckResultInChat() {
        this.lastResult = handleUpdateCheck();
        if(lastResult != null){
            switch (lastResult.getReason()) {
                case UP_TO_DATE:
                    return Core.getMessages().getString("core.commands.update.no_update");
                case NEW_UPDATE:
                    return Core.getMessages().getString("core.commands.update.new_update", "newversion", lastResult.getNewestVersion());
                case COULD_NOT_CONNECT:
                    return Core.getMessages().getString("core.commands.update.could-not-connect");
                case INVALID_JSON:
                    return Core.getMessages().getString("core.commands.update.invalid");
                case UNAUTHORIZED_QUERY:
                    return Core.getMessages().getString("core.commands.update.unauthorized");
                case UNRELEASED_VERSION:
                    return Core.getMessages().getString("core.commands.update.development", "currentversion", lastResult.getNewestVersion());
                case UNKNOWN_ERROR, UNSUPPORTED_VERSION_SCHEME:
                    return Core.getMessages().getString("core.commands.update.unknown");
            }
        }
            return Core.getMessages().getString("core.commands.update.fail");
    }
}

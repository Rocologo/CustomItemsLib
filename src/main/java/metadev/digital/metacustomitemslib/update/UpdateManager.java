package metadev.digital.metacustomitemslib.update;

import metadev.digital.metacustomitemslib.Core;
import metadev.digital.metacustomitemslib.update.UpdateChecker;

public class UpdateManager {
    private Core plugin;

    public UpdateManager(Core plugin) {
        this.plugin = plugin;

        UpdateChecker.init(this.plugin, 117869);

    }

    public void isInitialized() {
        if(UpdateChecker.isInitialized()){
            Core.getMessages().debug("Update checker is properly initialized");
            Core.getMessages().debug("Update checker is properly initialized");
            Core.getMessages().debug("Update checker is properly initialized");
            Core.getMessages().debug("Update checker is properly initialized");
            Core.getMessages().debug("Update checker is properly initialized");
            Core.getMessages().debug("Update checker is properly initialized");

        }
        else{
            Core.getMessages().debug("Update checker is NOT properly initialized");
            Core.getMessages().debug("Update checker is NOT properly initialized");
            Core.getMessages().debug("Update checker is NOT properly initialized");
            Core.getMessages().debug("Update checker is NOT properly initialized");
            Core.getMessages().debug("Update checker is NOT properly initialized");
            Core.getMessages().debug("Update checker is NOT properly initialized");
            Core.getMessages().debug("Update checker is NOT properly initialized");
        }
    }
}

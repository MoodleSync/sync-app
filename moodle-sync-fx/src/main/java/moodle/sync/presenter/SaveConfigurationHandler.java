package moodle.sync.presenter;


import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.util.ShutdownHandler;

public class SaveConfigurationHandler extends ShutdownHandler {

    private final ApplicationContext context;


    public SaveConfigurationHandler(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public boolean execute() {
        try {
            context.saveConfiguration();
        }
        catch (Exception e) {
            logException(e, "Save configuration failed");
        }

        return true;
    }
}

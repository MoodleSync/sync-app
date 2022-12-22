package moodle.sync.presenter;

import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.util.ShutdownHandler;

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

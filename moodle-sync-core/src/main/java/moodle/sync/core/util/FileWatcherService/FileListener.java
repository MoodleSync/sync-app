package moodle.sync.core.util.FileWatcherService;

import java.util.EventListener;

/**
 * Interface declaring the possible changes regarding a local directory.
 */
public interface FileListener extends EventListener {

    default void onCreated(FileEvent event) {
        System.out.println("Created" + event.getFile().toString());
        event.getFile();
    }

    default void onModified(FileEvent event) {
        System.out.println("Changed");
    }

    default void onDeleted(FileEvent event) {
        System.out.println("Deleted");
    }
}

package moodle.sync.core.util.FileWatcherService;

import java.io.File;
import java.util.EventObject;

/**
 *
 * Class representing an event regarding a local directory.
 *
 */
public class FileEvent extends EventObject {

    public FileEvent(File file) {
        super(file);
    }

    public File getFile() {
        return (File) getSource();
    }

}

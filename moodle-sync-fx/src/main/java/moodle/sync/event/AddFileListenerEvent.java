package moodle.sync.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import moodle.sync.core.util.FileWatcherService.FileWatcher;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddFileListenerEvent {

    private FileWatcher element;

}

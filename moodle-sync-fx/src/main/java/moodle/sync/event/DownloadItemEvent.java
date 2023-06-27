package moodle.sync.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import moodle.sync.javafx.model.SyncTableElement;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DownloadItemEvent {

    private SyncTableElement element;

}

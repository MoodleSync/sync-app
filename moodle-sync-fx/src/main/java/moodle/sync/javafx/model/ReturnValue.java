package moodle.sync.javafx.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReturnValue {

    private List<Path> fileList;

    private SyncTableElement element;

}

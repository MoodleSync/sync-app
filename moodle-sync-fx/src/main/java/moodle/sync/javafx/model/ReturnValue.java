package moodle.sync.javafx.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import moodle.sync.javafx.model.syncTableElement;

import java.nio.file.Path;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReturnValue {

    private List<Path> fileList;

    private syncTableElement element;

}

package moodle.sync.javafx.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;

@Getter
@AllArgsConstructor

/*
 * Class used to be able to transfer a SyncTableElement and a dedicated fileList. Helps minimizing the setDataXXX()
 * classes.
 */
public class ReturnValue {

    private List<Path> fileList;

    private SyncTableElement element;

}

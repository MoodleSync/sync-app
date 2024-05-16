package moodle.sync.javafx.custom;

import javafx.scene.control.ListCell;
import moodle.sync.core.fileserver.FileServerType;


import static java.util.Objects.isNull;

public class FileserverListCell extends ListCell<String>{

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);

        if (isNull(item) || empty) {
            setText("");
        }
        else {
            setText(item);
        }
    }
}

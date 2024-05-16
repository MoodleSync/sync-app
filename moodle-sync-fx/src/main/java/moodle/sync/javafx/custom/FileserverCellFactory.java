package moodle.sync.javafx.custom;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import moodle.sync.core.fileserver.FileServerType;

public class FileserverCellFactory implements Callback<ListView<String>, ListCell<String>>{

    @Override
    public ListCell<String> call(ListView<String> param) {
        return new FileserverListCell();
    }
}




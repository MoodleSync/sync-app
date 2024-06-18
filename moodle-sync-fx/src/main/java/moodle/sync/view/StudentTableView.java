package moodle.sync.view;

import javafx.collections.ObservableList;
import moodle.sync.core.view.View;
import moodle.sync.javafx.model.SyncTableElement;

public interface StudentTableView extends View {

    void setStudentTableData(ObservableList<SyncTableElement> data);

}

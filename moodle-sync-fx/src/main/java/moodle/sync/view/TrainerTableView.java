package moodle.sync.view;

import javafx.collections.ObservableList;
import moodle.sync.core.beans.BooleanProperty;
import moodle.sync.core.view.View;
import moodle.sync.javafx.model.SyncTableElement;

public interface TrainerTableView extends View {

    void setTrainerTableData(ObservableList<SyncTableElement> data);

    void setSelectAll(BooleanProperty selectAll);

}

package moodle.sync.javafx.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import moodle.sync.javafx.core.view.FxView;
import moodle.sync.javafx.core.view.FxmlView;
import moodle.sync.javafx.model.SyncTableElement;
import moodle.sync.presenter.StudentTablePresenter;
import moodle.sync.view.StudentTableView;

@FxmlView(name = "main-studenttable", presenter = StudentTablePresenter.class)
public class FxStudentTableView extends StackPane implements StudentTableView, FxView {

    @FXML
    private TableView<SyncTableElement> studentTable;

    public FxStudentTableView() {
        super();
    }

    @Override
    public void setStudentTableData(ObservableList<SyncTableElement> data) {
        studentTable.setItems(data);
    }
}

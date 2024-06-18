package moodle.sync.javafx.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.beans.BooleanProperty;
import moodle.sync.javafx.core.beans.LectBooleanProperty;
import moodle.sync.javafx.core.util.FxUtils;
import moodle.sync.javafx.core.view.FxView;
import moodle.sync.javafx.core.view.FxmlView;
import moodle.sync.javafx.model.SyncTableElement;
import moodle.sync.presenter.TrainerTablePresenter;
import moodle.sync.view.TrainerTableView;

import javax.inject.Inject;

@FxmlView(name = "main-trainertable", presenter = TrainerTablePresenter.class)
public class FxTrainerTableView extends StackPane implements TrainerTableView, FxView {

    private final ApplicationContext context;

    @FXML
    private TableView<SyncTableElement> trainerTable;

    @FXML
    private CheckBox allSelected;

    @Inject
    public FxTrainerTableView(ApplicationContext context) {
        super();
        this.context = context;
    }

    @Override
    public void setTrainerTableData(ObservableList<SyncTableElement> data) {
        trainerTable.setItems(data);
    }

    /**
     * Method to select all possible "execute"-boxes.
     *
     * @param selectAll boolean param.
     */
    @Override
    public void setSelectAll(BooleanProperty selectAll) {
        FxUtils.invoke(() ->  allSelected.selectedProperty().bindBidirectional(new LectBooleanProperty(selectAll)));
    }
}

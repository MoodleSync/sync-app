package moodle.sync.javafx.custom;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.StringConverter;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.javafx.model.SyncTableElement;

/**
 * Class used to display the selctedProperty-value inside a CheckBoxTreeTableCell.
 *
 * @author Daniel Schröter
 */
public class AvailabilityCheckBoxCell<U, B> extends CheckBoxTableCell<SyncTableElement, Boolean> {

    private CheckBox checkBox;

    private boolean showLabel;

    private ObservableValue<Boolean> booleanProperty;


    @Override
    public void updateItem(Boolean item, boolean empty) {
        this.checkBox = new CheckBox();
        this.checkBox.setAlignment(Pos.CENTER);
        setAlignment(Pos.CENTER);
        setGraphic(checkBox);

        super.updateItem(item, empty);

        if (booleanProperty instanceof BooleanProperty) {
            checkBox.selectedProperty().unbindBidirectional((BooleanProperty) booleanProperty);
            booleanProperty = null;
        }
        if (empty) {
            checkBox.setAlignment(Pos.CENTER);
            setText(null);
            setGraphic(null);
        } else if (getTableRow() != null) {
            if (getTableRow().getItem() != null && (!getTableRow().getItem().isSelectable() ||
                    getTableRow().getItem().getAction() == MoodleAction.UploadSection || getTableRow().getItem().getAction() == MoodleAction.ExistingFile
                    || getTableRow().getItem().getAction() == MoodleAction.NotLocalFile || getTableRow().getItem().getAction() == MoodleAction.FolderSynchronize)) {
                checkBox.setAlignment(Pos.CENTER);
                setDisable(false);
                setGraphic(null);
            }
        } else {
            StringConverter<Boolean> c = getConverter();

            if (showLabel) {
                checkBox.setAlignment(Pos.CENTER);
                setText(c.toString(item));
            }
            setGraphic(checkBox);

            ObservableValue<?> obsValue = getSelectedProperty();
            if (obsValue instanceof BooleanProperty) {
                booleanProperty = (ObservableValue<Boolean>) obsValue;
                checkBox.selectedProperty().bindBidirectional((BooleanProperty) booleanProperty);
            }

            checkBox.disableProperty().bind(Bindings.not(getTableView().
                    editableProperty().and(getTableColumn().editableProperty()).and(editableProperty())));
        }

        checkBox.setAlignment(Pos.CENTER);
        setAlignment(Pos.CENTER);
    }


    private ObservableValue<?> getSelectedProperty() {
        return getSelectedStateCallback() != null ? getSelectedStateCallback().call(getIndex()) :
                getTableColumn().getCellObservableValue(getIndex());
    }
}

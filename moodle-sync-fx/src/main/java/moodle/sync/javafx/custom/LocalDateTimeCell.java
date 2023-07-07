package moodle.sync.javafx.custom;

import com.dlsc.gemsfx.TimePicker;

import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

import moodle.sync.core.util.MoodleAction;

import moodle.sync.javafx.model.TimeDateElement;
import moodle.sync.javafx.model.SyncTableElement;

/**
 * Class used to display a time/date setter inside a TableCell.
 */
public class LocalDateTimeCell<S, U> extends TableCell<SyncTableElement, TimeDateElement> {

    private DatePicker datePicker;
    private TimePicker timePicker;

    @Override
    public void updateItem(TimeDateElement item, boolean empty) {
        this.datePicker = new DatePicker();
        this.timePicker = new TimePicker();
        setAlignment(Pos.CENTER);
        datePicker.setMaxWidth(100);
        timePicker.setMaxWidth(100);
        HBox hbox = new HBox(datePicker, timePicker);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(10);
        setGraphic(hbox);

        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else if (getTableRow() != null && getTableRow().getItem() != null) {
            datePicker.valueProperty().unbindBidirectional(getTableRow().getItem().availabilityDateTimeProperty().get().LocalDateProperty());
            timePicker.timeProperty().unbindBidirectional(getTableRow().getItem().availabilityDateTimeProperty().get().LocalTimeProperty());
            if (getTableRow().getItem() != null && (!getTableRow().getItem().isSelectable() ||
                    getTableRow().getItem().getAction() == MoodleAction.UploadSection || getTableRow().getItem().getAction() == MoodleAction.ExistingFile ||
                    getTableRow().getItem().getAction() == MoodleAction.NotLocalFile || getTableRow().getItem().getAction() == MoodleAction.FolderSynchronize)) {
                setDisable(false);
                setGraphic(null);
            }
            else {
                if (getTableRow().getItem() != null && getTableRow().getItem().isSelectable()) {
                    datePicker.valueProperty().bindBidirectional(getTableRow().getItem().availabilityDateTimeProperty().get().LocalDateProperty());
                    timePicker.timeProperty().bindBidirectional(getTableRow().getItem().availabilityDateTimeProperty().get().LocalTimeProperty());
                }
            }
        }
    }
}

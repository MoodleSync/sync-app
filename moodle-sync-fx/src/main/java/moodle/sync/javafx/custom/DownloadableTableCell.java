package moodle.sync.javafx.custom;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DefaultStringConverter;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.javafx.model.SyncTableElement;
import org.controlsfx.control.PopOver;
import org.lecturestudio.javafx.control.SvgIcon;

public class DownloadableTableCell <U, B> extends TableCell<SyncTableElement, Boolean> {

    @Override
    public void updateItem(Boolean item, boolean empty) {

        super.updateItem(item, empty);

        setAlignment(Pos.CENTER);
        setGraphic(null);
        setVisible(true);

        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
            setText(null);
            setEditable(false);
        } else if(getTableRow().getItem().getDownloadable()){
            setEditable(false);
            SvgIcon icon = new SvgIcon();
            setStyle("-fx-font-weight: normal");
            icon.getStyleClass().add("download-icon");
            setGraphic(icon);
            setAlignment(Pos.CENTER);
        }
    }
}

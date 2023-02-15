package moodle.sync.javafx.custom;

import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import moodle.sync.javafx.model.SyncTableElement;

/**
 * Class implementing a text field with different styles as the content of a TableCell.
 */
public class HighlightSectionCellFactory implements Callback<TableColumn<SyncTableElement, String>,
        TextFieldTableCell<SyncTableElement, String>> {

    @Override
    public TextFieldTableCell<SyncTableElement, String> call(TableColumn<SyncTableElement, String> p) {
        UploadHighlightTableCell<SyncTableElement, String> cell = new UploadHighlightTableCell<SyncTableElement,
                String>();
        cell.setAlignment(Pos.CENTER);
        return cell;
    }
}

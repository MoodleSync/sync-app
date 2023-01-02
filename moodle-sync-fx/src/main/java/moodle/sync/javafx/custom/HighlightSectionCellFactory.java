package moodle.sync.javafx.custom;

import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import moodle.sync.javafx.model.syncTableElement;

/**
 * Class implementing a text field with different styles as the content of a TableCell.
 */
public class HighlightSectionCellFactory implements Callback<TableColumn<syncTableElement, String>,
        TextFieldTableCell<syncTableElement, String>> {

    @Override
    public TextFieldTableCell<syncTableElement, String> call(TableColumn<syncTableElement, String> p) {
        UploadHighlightTableCell<syncTableElement, String> cell = new UploadHighlightTableCell<syncTableElement,
                String>();
        cell.setAlignment(Pos.CENTER);
        return cell;
    }
}

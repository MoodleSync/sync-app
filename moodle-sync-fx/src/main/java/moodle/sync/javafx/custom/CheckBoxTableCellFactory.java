package moodle.sync.javafx.custom;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import moodle.sync.javafx.model.SyncTableElement;

/**
 * Class implementing a Checkbox as the content of a TableCell.
 *
 * @author Daniel Schröter
 */
public class CheckBoxTableCellFactory implements Callback<TableColumn<SyncTableElement, Boolean>, TableCell<SyncTableElement, Boolean>> {
    @Override
    public TableCell<SyncTableElement, Boolean> call(TableColumn<SyncTableElement, Boolean> p) {
        UploadCheckBoxCell<SyncTableElement, Boolean> cell = new UploadCheckBoxCell<SyncTableElement, Boolean>();
        cell.setAlignment(Pos.CENTER);
        cell.setStyle("-fx-alignment: CENTER;");

        return cell;
    }
}
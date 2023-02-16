package moodle.sync.javafx.custom;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import moodle.sync.javafx.model.SyncTableElement;

/**
 * Class used to change the background color of a TableCell.
 */
public class StatusCellFactory implements Callback<TableColumn<SyncTableElement, String>, TableCell<SyncTableElement, String>> {
    @Override
    public TableCell<SyncTableElement, String> call(TableColumn<SyncTableElement, String> p){
        StatusTableCell<SyncTableElement, String> cell = new StatusTableCell<>();
        cell.setAlignment(Pos.CENTER);
        return cell;
    }
}

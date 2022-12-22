package moodle.sync.javafx.custom;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import moodle.sync.javafx.model.syncTableElement;

/**
 * Class used to change the background color of a TableCell.
 */
public class StatusCellFactory implements Callback<TableColumn<syncTableElement, String>, TableCell<syncTableElement, String>> {
    @Override
    public TableCell<syncTableElement, String> call(TableColumn<syncTableElement, String> p){
        StatusTableCell<syncTableElement, String> cell = new StatusTableCell<>();
        cell.setAlignment(Pos.CENTER);
        return cell;
    }
}

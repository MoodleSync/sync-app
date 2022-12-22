package moodle.sync.javafx.custom;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import moodle.sync.javafx.model.syncTableElement;

/**
 * Class implementing a Checkbox as the content of a TableCell.
 *
 * @author Daniel Schröter
 */
public class AvailabilityCellFactory implements Callback<TableColumn<syncTableElement, Boolean>, TableCell<syncTableElement, Boolean>> {
    @Override
    public TableCell<syncTableElement, Boolean> call(TableColumn<syncTableElement, Boolean> p) {
        AvailabilityCheckBoxCell <syncTableElement, Boolean> cell = new AvailabilityCheckBoxCell<syncTableElement, Boolean>();
        cell.setAlignment(Pos.CENTER);
        cell.setStyle("-fx-alignment: CENTER;");

        return cell;
    }
}
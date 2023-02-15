package moodle.sync.javafx.custom;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import moodle.sync.javafx.model.TimeDateElement;
import moodle.sync.javafx.model.SyncTableElement;

/**
 * Class used for determining the date and time of a upload inside the "sync-page"-table.
 *
 * @author Daniel Schröter
 */
public class AvailableDateTimeTableCellFactory implements Callback<TableColumn<SyncTableElement, TimeDateElement>, TableCell<SyncTableElement, TimeDateElement>> {
    @Override
    public TableCell<SyncTableElement, TimeDateElement> call(TableColumn<SyncTableElement, TimeDateElement> p) {
        LocalDateTimeCell<SyncTableElement, TimeDateElement> cell = new LocalDateTimeCell<SyncTableElement, TimeDateElement>();
        cell.setAlignment(Pos.CENTER);
        cell.setStyle("-fx-alignment: CENTER;");

        return cell;
    }
}
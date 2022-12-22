package moodle.sync.javafx.custom;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import moodle.sync.javafx.model.TimeDateElement;
import moodle.sync.javafx.model.syncTableElement;

/**
 * Class used for determining the date and time of a upload inside the "sync-page"-table.
 *
 * @author Daniel Schröter
 */
public class AvailableDateTimeTableCellFactory implements Callback<TableColumn<syncTableElement, TimeDateElement>, TableCell<syncTableElement, TimeDateElement>> {
    @Override
    public TableCell<syncTableElement, TimeDateElement> call(TableColumn<syncTableElement, TimeDateElement> p) {
        LocalDateTimeCell<syncTableElement, TimeDateElement> cell = new LocalDateTimeCell<syncTableElement, TimeDateElement>();
        cell.setAlignment(Pos.CENTER);
        cell.setStyle("-fx-alignment: CENTER;");

        return cell;
    }
}
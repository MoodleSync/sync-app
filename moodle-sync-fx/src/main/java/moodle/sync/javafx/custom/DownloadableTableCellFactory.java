package moodle.sync.javafx.custom;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import moodle.sync.javafx.model.SyncTableElement;
import org.lecturestudio.core.app.ApplicationContext;

import javax.inject.Inject;

public class DownloadableTableCellFactory implements Callback<TableColumn<SyncTableElement, Boolean>, TableCell<SyncTableElement, Boolean>> {

    private final ApplicationContext context;

    @Inject
    public DownloadableTableCellFactory(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public TableCell<SyncTableElement, Boolean> call(TableColumn<SyncTableElement, Boolean> p) {
        DownloadableTableCell <SyncTableElement, Boolean> cell =
                new DownloadableTableCell<SyncTableElement, Boolean>(context);

        cell.setAlignment(Pos.CENTER);
        cell.setStyle("-fx-alignment: CENTER;");

        return cell;
    }
}

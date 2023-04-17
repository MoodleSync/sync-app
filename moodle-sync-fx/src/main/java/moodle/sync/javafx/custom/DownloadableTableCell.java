package moodle.sync.javafx.custom;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DefaultStringConverter;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.event.DownloadItemEvent;
import moodle.sync.javafx.model.SyncTableElement;
import org.controlsfx.control.PopOver;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.javafx.control.SvgIcon;

import javax.inject.Inject;

public class DownloadableTableCell <U, B> extends TableCell<SyncTableElement, Boolean> {

    private final ApplicationContext context;

    @Inject
    public DownloadableTableCell(ApplicationContext context) {
        this.context = context;
    }


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
            Button button = new Button();
            button.setOnAction(event -> {
                {

                    SyncTableElement sectionElement = getTableRow().getItem();
                    sectionElement.setDoDownload(true);
                    context.getEventBus().post(new DownloadItemEvent(sectionElement));

                    event.consume();
                }
            });
            SvgIcon icon = new SvgIcon();
            setStyle("-fx-font-weight: normal");
            icon.getStyleClass().add("download-icon");
            button.setGraphic(icon);
            setGraphic(button);
            setAlignment(Pos.CENTER);
        }
    }
}

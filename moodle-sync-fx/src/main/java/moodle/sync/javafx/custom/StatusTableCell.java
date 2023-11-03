package moodle.sync.javafx.custom;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;

import javafx.scene.layout.VBox;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.javafx.model.SyncTableElement;

import org.controlsfx.control.PopOver;
import moodle.sync.javafx.core.control.SvgIcon;

/**
 * Class used to change the background color of a TableCell.
 */
public class StatusTableCell <U, B> extends TableCell<SyncTableElement, String> {

    private PopOver popOver;

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);

        if(popOver != null){
            popOver = null;
            this.setOnMouseEntered(mouseEvent -> {
            });

            this.setOnMouseExited(mouseEvent -> {
            });
        }

        if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
            setText(null);
            setStyle("-fx-background-color: TRANSPARENT");
        } else {
            if(getTableRow().getItem().getAction() == MoodleAction.MoodleSynchronize){
                setText(item);
                setStyle("-fx-background-color: PALEGREEN");
            }
            else if(getTableRow().getItem().getAction() == MoodleAction.MoodleUpload){
                setText(item);
                setStyle("-fx-background-color: SKYBLUE");
            }
            else if(getTableRow().getItem().getAction() == MoodleAction.FTPUpload){
                SvgIcon icon = new SvgIcon();
                icon.getStyleClass().add("ftp-icon");
                setGraphic(icon);
                setText(item);
                setStyle("-fx-background-color: ORANGE");
            }
            else if(getTableRow().getItem().getAction() == MoodleAction.FolderUpload){
                //Pop-Up to display files
                Label textArea = new Label();
                String content = "";
                String newline = System.getProperty("line.separator");
                for(int i = 0; i < getTableRow().getItem().getContent().size(); i++) {
                    content = content + getTableRow().getItem().getContent().get(i).getFileName() + newline;
                }
                textArea.setText(content);
                textArea.setWrapText(true);
                textArea.setMaxWidth(200);
                textArea.setStyle("-fx-font-weight: normal");
                textArea.getStyleClass().add("popUpTextArea");
                VBox vBox = new VBox(textArea);
                vBox.setPadding(new Insets(5));
                popOver = new PopOver(vBox);
                popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);

                this.setOnMouseEntered(mouseEvent -> {
                    //Show PopOver when mouse enters label
                    popOver.show(this);
                });
                this.setOnMouseExited(mouseEvent -> {
                    //Hide PopOver when mouse exits label
                    popOver.hide();
                });
                SvgIcon icon = new SvgIcon();
                icon.getStyleClass().add("folder-icon");
                setGraphic(icon);
                setText(item);
                setStyle("-fx-background-color: SKYBLUE");
            }
            else if(getTableRow().getItem().getAction() == MoodleAction.FolderSynchronize){
                //Pop-Up to display files
                Label textArea = new Label();
                String content = "";
                String newline = System.getProperty("line.separator");
                for(int i = 0; i < getTableRow().getItem().getContent().size(); i++) {
                    content = content + getTableRow().getItem().getContent().get(i).getFileName() + newline;
                }
                textArea.setText(content);
                textArea.setWrapText(true);
                textArea.setMaxWidth(200);
                textArea.setStyle("-fx-font-weight: normal");
                textArea.getStyleClass().add("popUpTextArea");
                VBox vBox = new VBox(textArea);
                vBox.setPadding(new Insets(5));
                popOver = new PopOver(vBox);
                popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);

                this.setOnMouseEntered(mouseEvent -> {
                    //Show PopOver when mouse enters label
                    popOver.show(this);
                });
                this.setOnMouseExited(mouseEvent -> {
                    //Hide PopOver when mouse exits label
                    popOver.hide();
                });
                SvgIcon icon = new SvgIcon();
                icon.getStyleClass().add("folder-icon");
                setGraphic(icon);
                setText(item);
                setStyle("-fx-background-color: PALEGREEN");
            }
            else if(getTableRow().getItem().getAction() == MoodleAction.UploadSection){
                setText(item);
                setStyle("-fx-font-weight: bold");
            }
            else if(getTableRow().getItem().getAction() == MoodleAction.DatatypeNotKnown){
                setText(item);
                setStyle("-fx-text-fill: gray");
            }
            else if(getTableRow().getItem().getAction() == MoodleAction.NotLocalFile) {
                setText("");
                setStyle("-fx-background-color: TRANSPARENT");
            }
            else{
                setText(item);
                setStyle("-fx-background-color: TRANSPARENT");
            }
        }
    }
}


package moodle.sync.javafx.custom;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DefaultStringConverter;

import moodle.sync.core.model.json.Content;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.javafx.core.control.SvgIcon;
import moodle.sync.javafx.model.SyncTableElement;

import org.controlsfx.control.PopOver;
import java.util.Objects;
import static java.util.Objects.isNull;

/**
 * Class used to display the Name of a Section/ Module including different styles/background colors inside a cell.
 */
public class UploadHighlightTableCell <U, B> extends TextFieldTableCell<SyncTableElement, String> {

    private Listener listener = new Listener();
    private PopOver popOver;

    @Override
    public void updateItem(String item, boolean empty) {

        super.updateItem(item, empty);

        this.setConverter(new DefaultStringConverter());

        if(getTableRow().getItem() != null){
            getTableRow().getItem().selectedProperty().removeListener(listener);
        }

        if(popOver != null){
            popOver = null;
            this.setOnMouseEntered(mouseEvent -> {
            });

            this.setOnMouseExited(mouseEvent -> {
            });
        }

        setGraphic(null);
        setVisible(true);
        getTableRow().getStyleClass().remove("headerstyle");

        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
            setText(null);
            setEditable(false);
        } else if(getTableRow().getItem().getAction() == MoodleAction.ExistingSection ){
            setEditable(false);
            if(!(getTableRow().getItem().getModuleType().replaceAll("\\<.*?>", "")).isEmpty()){
                Label textArea = new Label();
                textArea.setText(getTableRow().getItem().getModuleType().replaceAll("\\<.*?>", ""));
                textArea.setWrapText(true);
                textArea.setMaxWidth(500);
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
            }
            setText(getTableRow().getItem().getModuleName());
            setStyle("-fx-font-weight: bold");

            getTableRow().getStyleClass().add("headerstyle");

        }
        else if(getTableRow().getItem().getAction() == MoodleAction.MoodleUpload ||
                getTableRow().getItem().getAction() == MoodleAction.FTPUpload ||
                getTableRow().getItem().getAction() == MoodleAction.UploadSection ||
                getTableRow().getItem().getAction() == MoodleAction.FolderUpload ||
                getTableRow().getItem().getAction() == MoodleAction.FolderSynchronize ||
                getTableRow().getItem().getAction() == MoodleAction.DatatypeNotKnown) {
            if((getTableRow().getItem().getAction() == MoodleAction.MoodleUpload && getTableRow().getItem() != null) ||
                    (getTableRow().getItem().getAction() == MoodleAction.FTPUpload && getTableRow().getItem() != null)) {
                if(!getTableRow().getItem().selectedProperty().get()){
                    setText(null);
                }
                getTableRow().getItem().selectedProperty().addListener(listener);
            }
            else {
                setText(null);
            }
        }
        else{
            setEditable(false);
            SvgIcon icon = new SvgIcon();
            setStyle("-fx-font-weight: normal");
            switch (getTableRow().getItem().getModuleType()) {
                case "section" -> setStyle("-fx-font-weight: bold");
                case "resource" -> icon.getStyleClass().add("file-icon");
                case "forum" -> icon.getStyleClass().add("forum-icon");
                case "folder" -> icon.getStyleClass().add("folder-icon");
                case "label" -> icon.getStyleClass().add("label-icon");
                case "quiz" -> icon.getStyleClass().add("quiz-icon");
                case "assign" -> icon.getStyleClass().add("assignment-icon");
                case "chat" -> icon.getStyleClass().add("chat-icon");
                case "feedback" -> icon.getStyleClass().add("feedback-icon");
                case "url" -> icon.getStyleClass().add("url-icon");
                case "survey" -> icon.getStyleClass().add("survey-icon");
                default -> icon.getStyleClass().add("other-icon");
            }
            if(!getTableRow().getItem().getUserVisible()){
                SvgIcon optIcon = new SvgIcon();
                optIcon.getStyleClass().add("lock-icon");
                HBox elem = new HBox(optIcon, icon);
                setGraphic(elem);
            }
            else {
                setGraphic(icon);
            }
            if(getTableRow().getItem().getAction() == MoodleAction.NotLocalFile && Objects.equals(getTableRow().getItem().getModuleType(), "label")) {
                setText(getTableRow().getItem().getExistingFileName());
                if(!(getTableRow().getItem().getModuleType().replaceAll("\\<.*?>", "")).isEmpty()){
                    Label textArea = new Label();
                    textArea.setText(getTableRow().getItem().getExistingFileName());
                    textArea.setWrapText(true);
                    textArea.setMaxWidth(500);
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
                }
            }
            else if ((getTableRow().getItem().getAction() == MoodleAction.NotLocalFile || getTableRow().getItem().getAction() == MoodleAction.ExistingFile)
                    && Objects.equals(getTableRow().getItem().getModuleType(), "folder")
                    && !isNull(getTableRow().getItem().getContentsOnline())) {
                Label textArea = new Label();
                String contents = "";
                String newline = System.getProperty("line.separator");
                for(Content content : getTableRow().getItem().getContentsOnline()) {
                    contents = contents + content.getFilename() + newline;
                }
                textArea.setText(contents);
                textArea.setWrapText(true);
                textArea.setMaxWidth(500);
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
            }
            else {
                setText(this.getText().replaceAll("\\u00a0\\n|&nbsp;\\r\\n", ""));
            }
        }
    }

    public class Listener implements ChangeListener {
        @Override
        public void changed(ObservableValue observableValue, Object o, Object t1) {
            setEditable(getTableRow().getItem().selectedProperty().get());
            if(getTableRow().getItem().selectedProperty().get()){
                setStyle("-fx-font-weight: normal");
                setText(getTableRow().getItem().getExistingFileName());
            }
            else{
                setText(null);
            }

        }
    }

}


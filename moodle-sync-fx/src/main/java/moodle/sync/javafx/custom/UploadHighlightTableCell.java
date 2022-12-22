package moodle.sync.javafx.custom;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.javafx.model.syncTableElement;
import org.controlsfx.control.PopOver;
import org.lecturestudio.javafx.control.SvgIcon;

/**
 * Class used to display the Name of a Section/ Module including different styles/background colors inside a cell.
 */
public class UploadHighlightTableCell <U, B> extends TableCell<syncTableElement, String> {

    private Listener listener = new Listener();
    private PopOver popOver;

    @Override
    public void updateItem(String item, boolean empty) {

        super.updateItem(item, empty);

        if(getTableRow().getItem() != null) getTableRow().getItem().selectedProperty().removeListener(listener);

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

        if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
            setText(null);
            setEditable(false);
        } else if(getTableRow().getItem().getAction() == MoodleAction.ExistingSection ){
            if(!(getTableRow().getItem().getModuleType().replaceAll("\\<.*?>", "")).isEmpty()){
                Label textArea = new Label();
                textArea.setText(getTableRow().getItem().getModuleType().replaceAll("\\<.*?>", ""));
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
            }
            setText(getTableRow().getItem().getModuleName());
            setStyle("-fx-font-weight: bold");


            getTableRow().getStyleClass().add("headerstyle");

        }
        else if(getTableRow().getItem().getAction() == MoodleAction.MoodleUpload ||
                getTableRow().getItem().getAction() == MoodleAction.FTPUpload ||
                getTableRow().getItem().getAction() == MoodleAction.UploadSection ||
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
            setGraphic(icon);
            setText(item.replaceAll("\\u00a0\\n|&nbsp;\\r\\n", ""));
        }
    }

    public class Listener implements ChangeListener {
        @Override
        public void changed(ObservableValue observableValue, Object o, Object t1) {
            setEditable(getTableRow().getItem().selectedProperty().get());
            if(getTableRow().getItem().selectedProperty().get()) setText(getTableRow().getItem().getExistingFileName());
            setVisible(getTableRow().getItem().selectedProperty().get());
        }
    }

}


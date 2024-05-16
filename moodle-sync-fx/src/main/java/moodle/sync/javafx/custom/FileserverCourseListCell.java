package moodle.sync.javafx.custom;

import javafx.scene.control.ListCell;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.PanoptoCourse;

import static java.util.Objects.isNull;

public class FileserverCourseListCell extends ListCell<PanoptoCourse>{
        @Override
        protected void updateItem(PanoptoCourse item, boolean empty) {
                super.updateItem(item, empty);

                setGraphic(null);

                if (isNull(item) || empty) {
                        setText("");
                } else {
                        setText(item.getName());
                }
        }

}






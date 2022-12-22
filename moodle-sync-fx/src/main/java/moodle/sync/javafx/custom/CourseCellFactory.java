package moodle.sync.javafx.custom;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import moodle.sync.core.model.json.Course;

/**
 * Class implementing Courses as the content of a ComboBox.
 *
 * @author Daniel Schröter
 */

public class CourseCellFactory implements Callback<ListView<Course>, ListCell<Course>> {

    @Override
    public ListCell<Course> call(ListView<Course> param) {
        return new CourseListCell();
    }

}

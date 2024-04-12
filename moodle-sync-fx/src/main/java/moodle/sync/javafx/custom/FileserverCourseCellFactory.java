package moodle.sync.javafx.custom;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.PanoptoCourse;

public class FileserverCourseCellFactory implements Callback<ListView<PanoptoCourse>, ListCell<PanoptoCourse>> {

    @Override
    public ListCell<PanoptoCourse> call(ListView<PanoptoCourse> param) {
        return new FileserverCourseListCell();
    }

}


package moodle.sync.view;

import javafx.collections.ObservableList;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.Section;
import moodle.sync.javafx.model.SyncTableElement;
import org.lecturestudio.core.beans.BooleanProperty;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.ConsumerAction;
import org.lecturestudio.core.view.View;

import java.util.List;

/**
 * Interface defining the functions of the "start-page".
 *
 * @author Daniel Schröter
 */
public interface LandingView extends View {

    void setOnUpdate(Action action);

    void setOnSettings(Action action);

    void setCourses(List<Course> courses);

    void setCourse(ObjectProperty<Course> course);

    void setOnCourseChanged(ConsumerAction<Course> action);

}

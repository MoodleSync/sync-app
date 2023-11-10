package moodle.sync.view;

import javafx.collections.ObservableList;
import moodle.sync.core.beans.BooleanProperty;
import moodle.sync.core.beans.ObjectProperty;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.Section;
import moodle.sync.core.view.Action;
import moodle.sync.core.view.ConsumerAction;
import moodle.sync.core.view.View;
import moodle.sync.javafx.model.SyncTableElement;


import java.util.List;

/**
 * Interface defining the functions of the "start-page".
 *
 * @author Daniel Schröter
 */
public interface TrainerStartView extends View {

    void setOnUpdate(Action action);

    void setOnSync(Action action);

    void setOnDownloadCourse(Action action);

    void setOnOpenWiki(Action action);

    void setOnSettings(Action action);

    void setOnFolder(Action action);

    void setSelectAll(BooleanProperty selectAll);

    void setProgress(double progress);

    void setCourseId(String string);

    void setSectionId(String string);

    void setCourses(List<Course> courses);

    void setCourse(ObjectProperty<Course> course);

    void setSections(List<Section> sections);

    void selectFirstSection();

    void setSection(ObjectProperty<Section> section);

    void setOnCourseChanged(ConsumerAction<Course> action);

    void setOnSectionChanged(ConsumerAction<Section> action);

    void setDataTrainer(ObservableList<SyncTableElement> data);

    void setDataGuest(ObservableList<SyncTableElement> data);

}

package moodle.sync.javafx.view;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import moodle.sync.core.model.json.Course;
import moodle.sync.javafx.model.SyncTableElement;
import moodle.sync.core.model.json.Section;
import org.lecturestudio.core.beans.BooleanProperty;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.ConsumerAction;
import org.lecturestudio.javafx.beans.LectBooleanProperty;
import org.lecturestudio.javafx.beans.LectObjectProperty;
import org.lecturestudio.javafx.util.FxUtils;
import org.lecturestudio.javafx.view.FxView;
import org.lecturestudio.javafx.view.FxmlView;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import moodle.sync.presenter.StartPresenter;
import moodle.sync.view.StartView;

import java.util.List;

/**
 * Class implementing the functions of the "start-page".
 *
 * @author Daniel Schröter
 */
@FxmlView(name = "main-start", presenter = StartPresenter.class)
public class FxStartView extends VBox implements StartView, FxView {

    @FXML
    private Button syncButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button folderButton;

    @FXML
    private Label sectionidlabel;

    @FXML
    private Label courseidlabel;

    @FXML
    private CheckBox allSelected;

    @FXML
    private ComboBox<Course> courseCombo;

    @FXML
    private ComboBox<Section> sectionCombo;

    @FXML
    private TableView<SyncTableElement> syncTable;


    public FxStartView() {
        super();
    }


    @Override
    public void setData(ObservableList<SyncTableElement> data) {
        FxUtils.invoke(() -> {
            syncTable.getItems().clear();
            syncTable.setItems(data);
        });
    }

    /**
     * Update the interface
     *
     * @param action User presses button.
     */
    @Override
    public void setOnUpdate(Action action) {
        FxUtils.bindAction(updateButton, action);
    }

    /**
     * Start the synchronisation process.
     *
     * @param action User presses button.
     */
    @Override
    public void setOnSync(Action action) {
        FxUtils.bindAction(syncButton, action);
    }

    /**
     * User opens the "settings-page".
     *
     * @param action User presses button.
     */
    @Override
    public void setOnSettings(Action action) {
        FxUtils.bindAction(settingsButton, action);
    }

    @Override
    public void setSelectAll(BooleanProperty selectAll) {
        allSelected.selectedProperty().bindBidirectional(new LectBooleanProperty(selectAll));
    }

    @Override
    public void setOnFolder(Action action) {
        FxUtils.bindAction(folderButton, action);
    }

    @Override
    public void setCourseId(String string){
        courseidlabel.setText(string);
    }

    @Override
    public void setSectionId(String string){
        sectionidlabel.setText(string);
    }

    /**
     * Method to set the elements of the Course-Combobox.
     *
     * @param courses Moodle-Courses to display.
     */
    @Override
    public void setCourses(List<Course> courses) {
        FxUtils.invoke(() -> courseCombo.getItems().setAll(courses));
    }

    /**
     * Choosen Moodle-course.
     *
     * @param course choosen Moodle-course.
     */
    @Override
    public void setCourse(ObjectProperty<Course> course) {
        courseCombo.valueProperty().bindBidirectional(new LectObjectProperty<>(course));
    }

    /**
     * Method to set the elements of the Section-Combobox.
     *
     * @param sections Course-Sections to display.
     */
    @Override
    public void setSections(List<Section> sections) {
        FxUtils.invoke(() -> sectionCombo.getItems().setAll(sections));
        sectionCombo.getSelectionModel().selectFirst();
    }

    /**
     * Choosen course-section.
     *
     * @param section choosen course-section.
     */
    @Override
    public void setSection(ObjectProperty<Section> section) {
        sectionCombo.valueProperty().bindBidirectional(new LectObjectProperty<>(section));
    }

    /**
     * Method to initiate the display of the sections of a choosen Course.
     *
     * @param action User chooses Course.
     */
    @Override
    public void setOnCourseChanged(ConsumerAction<Course> action) {
        courseCombo.valueProperty().addListener((observable, oldCourse, newCourse) -> {
            executeAction(action, newCourse);
        });
    }
}

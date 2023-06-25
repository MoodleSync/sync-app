package moodle.sync.javafx.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.Section;
import moodle.sync.javafx.model.SyncTableElement;
import moodle.sync.presenter.GuestPresenter;
import moodle.sync.view.GuestStartView;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.ConsumerAction;
import org.lecturestudio.javafx.beans.LectObjectProperty;
import org.lecturestudio.javafx.util.FxUtils;
import org.lecturestudio.javafx.view.FxView;
import org.lecturestudio.javafx.view.FxmlView;

import java.util.List;

/**
 * Class implementing the functions of the "start-page".
 *
 * @author Daniel Schröter
 */
@FxmlView(name = "main-gueststart", presenter = GuestPresenter.class)
public class FxGuestStartView extends VBox implements GuestStartView, FxView {


    @FXML
    private Button downloadButton;

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
    private ProgressBar progressbar;

    @FXML
    private ComboBox<Course> courseCombo;

    @FXML
    private ComboBox<Section> sectionCombo;

    @FXML
    private TableView<SyncTableElement> syncTable;


    public FxGuestStartView() {
        super();
    }


    @Override
    public Void setData(ObservableList<SyncTableElement> data) {
        FxUtils.invoke(() -> {
            syncTable.getItems().clear();
            syncTable.setItems(data);
        });
        return null;
    }

    /**
     * Update the interface
     *
     * @param action User presses button.
     */
    @Override
    public void setOnUpdate(Action action) {
        FxUtils.bindAction(updateButton, action);
        //syncTable.getVisibleLeafColumns().get(1).setVisible(false);
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
    public void setOnDownloadCourse(Action action) {
        FxUtils.bindAction(downloadButton, action);
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

    @Override
    public void setProgress(double progress) {
        FxUtils.invoke(() -> progressbar.setProgress(progress));
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

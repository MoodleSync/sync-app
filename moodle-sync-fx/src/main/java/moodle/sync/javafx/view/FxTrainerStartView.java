package moodle.sync.javafx.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.Section;
import moodle.sync.javafx.model.SyncTableElement;
import moodle.sync.presenter.TrainerPresenter;
import moodle.sync.view.TrainerStartView;
import org.controlsfx.control.NotificationPane;
import org.lecturestudio.core.beans.BooleanProperty;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.ConsumerAction;
import org.lecturestudio.javafx.beans.LectBooleanProperty;
import org.lecturestudio.javafx.beans.LectObjectProperty;
import org.lecturestudio.javafx.layout.ColumnSizeConstraints;
import org.lecturestudio.javafx.layout.DynamicResizePolicy;
import org.lecturestudio.javafx.util.FxUtils;
import org.lecturestudio.javafx.view.FxView;
import org.lecturestudio.javafx.view.FxmlView;

import java.util.List;

/**
 * Class implementing the functions of the "start-page".
 *
 * @author Daniel Schröter
 */
@FxmlView(name = "main-trainerstart", presenter = TrainerPresenter.class)
public class FxTrainerStartView extends VBox implements TrainerStartView, FxView {

    @FXML
    private Button syncButton;

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
    private CheckBox allSelected;

    @FXML
    private ProgressBar progressbar;

    @FXML
    private ComboBox<Course> courseCombo;

    @FXML
    private ComboBox<Section> sectionCombo;

    @FXML
    private TableView<SyncTableElement> syncTableTrainer;

    @FXML
    private TableView<SyncTableElement> syncTableGuest;



    public FxTrainerStartView() {
        super();
    }


    @Override
    public void setDataTrainer(ObservableList<SyncTableElement> data) {
        FxUtils.invoke(() -> {
            syncTableGuest.setManaged(false);
            syncTableGuest.setVisible(false);
            syncTableTrainer.setVisible(true);
            syncTableTrainer.setManaged(true);
            syncButton.setManaged(true);
            syncButton.setVisible(true);
            syncTableTrainer.getItems().clear();
            syncTableTrainer.setItems(data);
        });
    }

    @Override
    public void setDataGuest(ObservableList<SyncTableElement> data) {
        FxUtils.invoke(() -> {
            syncTableTrainer.setVisible(false);
            syncTableTrainer.setManaged(false);
            syncTableGuest.setVisible(true);
            syncTableGuest.setManaged(true);
            syncButton.setManaged(false);
            syncButton.setVisible(false);
            syncTableGuest.getItems().clear();
            syncTableGuest.setItems(data);
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
    public void setOnDownloadCourse(Action action) {
        FxUtils.bindAction(downloadButton, action);
    }

    @Override
    public void setSelectAll(BooleanProperty selectAll) {
        FxUtils.invoke(() -> allSelected.selectedProperty().bindBidirectional(new LectBooleanProperty(selectAll)));
    }

    @Override
    public void setProgress(double progress) {
        FxUtils.invoke(() -> progressbar.setProgress(progress));
    }

    @Override
    public void setOnFolder(Action action) {
        FxUtils.bindAction(folderButton, action);
        NotificationPane notificationPane = new NotificationPane();
        notificationPane.setContent(new Label("test"));
        notificationPane.setText("Test mit setText");
        notificationPane.show();
    }

    @Override
    public void setCourseId(String string){
        FxUtils.invoke(() -> courseidlabel.setText(string));
    }

    @Override
    public void setSectionId(String string){
        FxUtils.invoke(() -> sectionidlabel.setText(string));
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
        System.out.println("in fx changed");
        System.out.println(course.get());
        FxUtils.invoke(() -> courseCombo.valueProperty().bindBidirectional(new LectObjectProperty<>(course)));
    }

    /**
     * Method to set the elements of the Section-Combobox.
     *
     * @param sections Course-Sections to display.
     */
    @Override
    public void setSections(List<Section> sections) {
        FxUtils.invoke(() -> {
            sectionCombo.getItems().setAll(sections);
            sectionCombo.getSelectionModel().selectFirst();
        });
    }

    /**
     * Choosen course-section.
     *
     * @param section choosen course-section.
     */
    @Override
    public void setSection(ObjectProperty<Section> section) {
        FxUtils.invoke(() -> sectionCombo.valueProperty().bindBidirectional(new LectObjectProperty<>(section)));
    }

    /**
     * Method to initiate the display of the sections of a choosen Course.
     *
     * @param action User chooses Course.
     */
    @Override
    public void setOnCourseChanged(ConsumerAction<Course> action) {
        System.out.println("in fx changed");
        courseCombo.valueProperty().addListener((observable, oldCourse, newCourse) -> {
            executeAction(action, newCourse);
        });
    }

    @Override
    public void setOnSectionChanged(ConsumerAction<Section> action) {
        System.out.println("SectionChanged");
        sectionCombo.valueProperty().addListener((observable, oldSection, newSection) -> {
            executeAction(action, newSection);
        });
    }

    @Override
    public void setGuestMode() {
    }

    @Override
    public void setTrainerMode() {
    }
}

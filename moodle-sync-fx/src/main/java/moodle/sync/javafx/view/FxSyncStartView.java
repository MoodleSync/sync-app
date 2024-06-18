package moodle.sync.javafx.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import moodle.sync.core.beans.ObjectProperty;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.PanoptoCourse;
import moodle.sync.core.model.json.Section;
import moodle.sync.core.view.Action;
import moodle.sync.core.view.ConsumerAction;
import moodle.sync.javafx.core.beans.LectObjectProperty;
import moodle.sync.javafx.core.util.FxUtils;
import moodle.sync.javafx.core.view.FxmlView;
import moodle.sync.javafx.core.view.FxView;
import moodle.sync.javafx.model.SyncTableElement;
import moodle.sync.presenter.SyncPresenter;
import moodle.sync.view.StudentTableView;
import moodle.sync.view.SyncStartView;
import moodle.sync.view.TrainerTableView;


import java.util.List;

/**
 * Class implementing the functions of the "start-page".
 *
 * @author Daniel Schröter
 */
@FxmlView(name = "main-syncstart", presenter = SyncPresenter.class)
public class FxSyncStartView extends VBox implements SyncStartView, FxView {

    @FXML
    private Button syncButton;

    @FXML
    private Button downloadButton;

    @FXML
    private Button helpButton;

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
    private ComboBox<PanoptoCourse> fileserverCombo;

    @FXML
    private Label fileserverCourseLabel;

    @FXML
    private Pane tableContainer;




    public FxSyncStartView() {
        super();
    }

    /**
     * Configures the table for Moodle-Role "Trainer".
     *
     * @param data content of the table.
     */
    @Override
    public void setDataTrainer(ObservableList<SyncTableElement> data) {
        FxUtils.invoke(() -> {
            //syncTableGuest.setManaged(false);
            //syncTableGuest.setVisible(false);
            //syncTableTrainer.setVisible(true);
            //syncTableTrainer.setManaged(true);
            syncButton.setManaged(true);
            syncButton.setVisible(true);
            //if(!isNull(syncTableTrainer.getItems())) {
            //    syncTableTrainer.getItems().clear();
            //}
            //syncTableTrainer.setItems(data);
        });
    }

    /**
     * Configures the table for Moodle-Role "Student".
     *
     * @param data content of the table.
     */
    @Override
    public void setDataGuest(ObservableList<SyncTableElement> data) {
        FxUtils.invoke(() -> {
            //syncTableTrainer.setVisible(false);
            //syncTableTrainer.setManaged(false);
            //syncTableGuest.setVisible(true);
            //syncTableGuest.setManaged(true);
            syncButton.setManaged(false);
            syncButton.setVisible(false);
            //if(!isNull(syncTableGuest.getItems())) {
            //    syncTableGuest.getItems().clear();
            //}
            //syncTableGuest.setItems(data);
        });
    }

    @Override
    public void clearTable() {
        FxUtils.invoke(() -> tableContainer.getChildren().clear());
    }

    @Override
    public void setStudent(StudentTableView studentTableView) {
        if (Node.class.isAssignableFrom(studentTableView.getClass())) {
            FxUtils.invoke(() -> tableContainer.getChildren().add((Node) studentTableView));
        }
    }

    @Override
    public void setTrainer(TrainerTableView trainerTableView) {
        if (Node.class.isAssignableFrom(trainerTableView.getClass())) {
            FxUtils.invoke(() -> tableContainer.getChildren().add((Node) trainerTableView));
        }
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

    /**
     * Method to invoke the download of the course.
     *
     * @param action Download course.
     */
    @Override
    public void setOnDownloadCourse(Action action) {
        FxUtils.bindAction(downloadButton, action);
    }

    @Override
    public void setOnOpenWiki(Action action) {
        FxUtils.bindAction(helpButton, action);
    }

    /**
     * Method to set the progress in the progressbar.
     *
     * @param progress Progress from 0-1.
     */
    @Override
    public void setProgress(double progress) {
        FxUtils.invoke(() -> progressbar.setProgress(progress));
    }

    /**
     * Method used to open the courses-directory.
     *
     * @param action open the folder.
     */
    @Override
    public void setOnFolder(Action action) {
        FxUtils.bindAction(folderButton, action);
    }

    /**
     * Method used to display the courses' id.
     *
     * @param string id of the course.
     */
    @Override
    public void setCourseId(String string){
        FxUtils.invoke(() -> courseidlabel.setText(string));
    }

    /**
     * Method used to display the sections' id.
     *
     * @param string id of the section.
     */
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
        FxUtils.invoke(() -> courseCombo.valueProperty().bindBidirectional(new LectObjectProperty<>(course)));
    }

    /**
     * Method to set the elements of the Panoptocourse-Combobox.
     *
     * @param panoptoCourses Moodle-Courses to display.
     */
    @Override
    public void setPanoptoCourses(List<PanoptoCourse> panoptoCourses) {
        FxUtils.invoke(() -> fileserverCombo.getItems().setAll(panoptoCourses));
    }

    /**
     * Choosen Moodle-course.
     *
     * @param panoptoCourse choosen Panopto-course.
     */
    @Override
    public void setPanoptoCourse(ObjectProperty<PanoptoCourse> panoptoCourse) {
        FxUtils.invoke(() -> fileserverCombo.valueProperty().bindBidirectional(new LectObjectProperty<>(panoptoCourse)));
    }

    @Override
    public void setPanoptoFileserver() {
        FxUtils.invoke(() -> {
            fileserverCombo.setVisible(true);
            fileserverCombo.setManaged(true);
            fileserverCourseLabel.setVisible(true);
            fileserverCourseLabel.setManaged(true);
        });
    }

    @Override
    public void removePanoptoFileserver() {
        FxUtils.invoke(() -> {
            fileserverCombo.setVisible(false);
            fileserverCombo.setManaged(false);
            fileserverCourseLabel.setVisible(false);
            fileserverCourseLabel.setManaged(false);
        });
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
        });
    }

    /**
     * Method used to select the first item of the sectionCombo.
     */
    @Override
    public void selectFirstSection() {
        FxUtils.invoke(() -> {
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
     * Method to initiate the update of the data when a course is changed.
     *
     * @param action update table.
     */
    @Override
    public void setOnCourseChanged(ConsumerAction<Course> action) {
        courseCombo.valueProperty().addListener((observable, oldCourse, newCourse) -> {
            executeAction(action, newCourse);
        });
    }

    /**
     * Method to initiate the update of the data when a section is changed.
     *
     * @param action update table.
     */
    @Override
    public void setOnSectionChanged(ConsumerAction<Section> action) {
        sectionCombo.valueProperty().addListener((observable, oldSection, newSection) -> {
            executeAction(action, newSection);
        });
    }

    /**
     * Method to initiate the update of the data when a section is changed.
     *
     * @param action update table.
     */
    @Override
    public void setOnPanoptoChanged(ConsumerAction<PanoptoCourse> action) {
        fileserverCombo.valueProperty().addListener((observable, oldPanoptoCourse, newPanoptoCourse) -> {
            executeAction(action, newPanoptoCourse);
        });
    }

}

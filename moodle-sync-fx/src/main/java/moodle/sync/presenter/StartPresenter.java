package moodle.sync.presenter;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;
import moodle.sync.core.util.FileDownloadService;
import moodle.sync.event.DownloadItemEvent;
import org.apache.commons.io.FilenameUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import moodle.sync.core.config.DefaultConfiguration;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.model.json.*;
import moodle.sync.core.model.json.Module;
import moodle.sync.core.fileserver.FileServerClientFTP;
import moodle.sync.core.fileserver.FileServerFile;
import moodle.sync.javafx.model.ReturnValue;
import moodle.sync.javafx.model.SyncTableElement;
import moodle.sync.core.util.FileWatcherService.FileEvent;
import moodle.sync.core.util.FileWatcherService.FileListener;
import moodle.sync.core.util.FileWatcherService.FileWatcher;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.core.web.service.MoodleService;
import moodle.sync.util.VerifyDataService;
import moodle.sync.util.FileService;
import moodle.sync.view.StartView;
import moodle.sync.presenter.command.ShowSettingsCommand;

import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.beans.BooleanProperty;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.View;
import org.lecturestudio.core.view.ViewContextFactory;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Class defining the logic of the "start-page".
 *
 * @author Daniel Schröter
 */
public class StartPresenter<T extends StartView> extends Presenter<T> implements FileListener {

    private final ViewContextFactory viewFactory;

    //Used MoodleService for executing Web Service API-Calls.
    private final MoodleService moodleService;

    //Configuration providing the settings.
    private final MoodleSyncConfiguration config;

    //Providing the content of a course. Used for the section-combobox.
    private List<Section> courseContent;

    //List representing the actual courseData with the planned/possible changes.
    private ObservableList<SyncTableElement> courseData;

    //FileWatcher for the current course's directory.
    private FileWatcher watcher;

    //Saves if the fileserver is required.
    private boolean fileServerRequired;

    //Used fileServerClient implementation.
    private FileServerClientFTP fileClient;

    //Select all possible changes.
    private BooleanProperty selectAll;

    //User's moodle token.
    private String token;

    //The moodle plattforms url.
    private String url;

    //Selected moodle course.
    private Course course;

    //Selected moodle section.
    private Section section;


    @Inject
    StartPresenter(ApplicationContext context, T view, ViewContextFactory viewFactory,
                   MoodleService moodleService) {
        super(context, view);
        this.viewFactory = viewFactory;
        this.moodleService = moodleService;
        this.config = (MoodleSyncConfiguration) context.getConfiguration();
        this.selectAll = new BooleanProperty(false);

    }// @Override
    // public void initialize() {
    //     System.out.println("StartPresenter initialized");
    //     context.getEventBus().register(this);
    //
    //
    //     //Initialising all functions of the "start-page" with the help of the configuration.
    //     String syncPath = config.getSyncRootPath();
    //     //Check whether a default path should be used to prevent unwanted behavior.
    //     if (!VerifyDataService.validateString(syncPath)) {
    //         DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
    //         config.setSyncRootPath(defaultConfiguration.getSyncRootPath());
    //     }
    //
    //     //view.setOnSync(this::onSync);
    //
    //     /**
    //     view.setOnUpdate(this::updateCourses);
    //     view.setOnSettings(this::onSettings);
    //     view.setOnSync(this::onSync);
    //     view.setOnDownloadCourse(this::onDownloadCourse);
    //     view.setCourse(config.recentCourseProperty());
    //     view.setCourses(courses());
    //     view.setSection(config.recentSectionProperty());
    //     view.setSections(sections());
    //     view.setOnCourseChanged(this::onCourseChanged);
    //     view.setData(setData());
    //     view.setOnFolder(this::openCourseDirectory);
    //     view.setSelectAll(selectAll);
    //     updateBottomLine();
    //
    //     //Display the course-sections after Moodle-course is chosen.
    //     config.recentCourseProperty().addListener((observable, oldCourse, newCourse) -> {
    //         course = config.getRecentCourse();
    //         config.setRecentSection(null);
    //         updateBottomLine();
    //         view.setData(setData());
    //     });
    //
    //     //Refresh Table if another section is chosen.
    //     config.recentSectionProperty().addListener((observable, oldSection, newSection) -> {
    //         section = config.getRecentSection();
    //         updateBottomLine();
    //         view.setData(setData());
    //     });
    //
    //     //Refresh course-list if new URL is entered.
    //     config.moodleUrlProperty().addListener((observable, oldUrl, newUrl) -> {
    //         config.setRecentCourse(null);
    //         config.setRecentSection(null);
    //         course = null;
    //         section = null;
    //         //TODO: hier werden dann 4 mal getcontents ausgelöst
    //     });
    //
    //     //"Select-All"-Button clicked.
    //     selectAll.addListener((observable, oldUrl, newUrl) -> {
    //         if (newUrl) {
    //             for (SyncTableElement elem : courseData) {
    //                 if (elem.isSelectable()) {
    //                     elem.selectedProperty().setValue(true);
    //                 }
    //             }
    //         } else {
    //             for (SyncTableElement elem : courseData) {
    //                 if (elem.isSelectable()) {
    //                     elem.selectedProperty().setValue(false);
    //                 }
    //             }
    //         }
    //     });

// }

    //Show selected section if a module is clicked.
    @Subscribe
    protected void onElementClicked(SyncTableElement selectedSection) {
        view.setSectionId(selectedSection.getSection().toString());
    }

    @Subscribe
    protected void onDownloadItem(DownloadItemEvent event) {
        onDownloadFile(event.getElement());
    }


    protected void onDownloadFile(SyncTableElement file) {
        try {
            FileDownloadService.getFile(file.getFileUrl(), token,
                    config.getSyncRootPath() + "/" + course.getDisplayname() + "/" + file.getSection() + "_"+ file.getSectionName(),
                    file.getExistingFileName(), file.getExistingFile());
        } catch (Exception e) {
            logException(e, "Sync failed");
        }
    }

    protected void onDownloadCourse() {
        try{
            watcher.close();
        for(SyncTableElement courseData : courseData) {
            if (courseData.getDownloadable()) {
                FileDownloadService.getFile(courseData.getFileUrl(), token, config.getSyncRootPath() + "/" + course.getDisplayname() + "/" + courseData.getSection() + "_" + courseData.getSectionName(), courseData.getExistingFileName(), courseData.getExistingFile());
            }
        }
        } catch (Exception e) {
            logException(e, "Sync failed");
        }
        //view.setData(setData());
    }

    //Update view if course was changed.
    protected void onCourseChanged(Course course) {
        updateBottomLine();
        view.setSections(sections());
    }

    //Show course-id and section number at the bottom.
    protected void updateBottomLine() {
        if (isNull(course)) {
            view.setCourseId(this.context.getDictionary().get("start.labelcourse.empty"));
            view.setSectionId(this.context.getDictionary().get("start.labelsection.empty"));
        } else if (isNull(section) || section.getSection() == -1) {
            view.setCourseId(course.getId().toString());
            view.setSectionId(this.context.getDictionary().get("start.labelsection.empty"));
        } else {
            view.setCourseId(course.getId().toString());
            view.setSectionId(section.getSection().toString());
        }
    }

    /**
     * Execute an API-call to get users Moodle-courses.
     *
     * @return list containing users Moodle-courses.
     */
    protected List<Course> courses() {
        url = config.getMoodleUrl();
        token = config.getMoodleToken();
        //Security checks to prevent unwanted behaviour.
        if (!VerifyDataService.validateString(url) || !VerifyDataService.validateString(token)) {
            return new ArrayList<>();
        }
        List<Course> courses = List.of();
        try {
            courses = moodleService.getEnrolledCourses(token, moodleService.getUserId(token));
            if (config.recentCourseProperty() != null) {
                course = config.getRecentCourse();
            }
        }
        catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.invalidurl.message");
            config.setRecentCourse(null);
            course = null;
        }

        //Do not show Moodle-courses which are already over.
        if (!courses.isEmpty()) {
            courses.removeIf(item -> (item.getEnddate() != 0 && (item.getEnddate() < System.currentTimeMillis() / 1000)));
        }

        return courses;
    }

    /**
     * Execute an API-call to get a choosen Moodle-courses course-sections.
     *
     * @return list containing course-sections.
     */
    protected List<Section> sections() {
        if (course == null) {
            return new ArrayList<>();
        }
        try {
            //List<Section> content = moodleService.getCourseContent(token, course.getId());
            //content.add(0, new Section(-2, this.context.getDictionary().get("start.sync.showall"), 1, "all", -1, -1,
                    //-1, true, null));
            //courseContent = content;
            //return content;
        }
        catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.invalidurl.message");
        }
        return new ArrayList<>();
    }

    /**
     * Method to "open" the Settings-page.

    protected void onSettings() {
        context.getEventBus().post(new ShowSettingsCommand(new Action() {
            @Override
            public void execute() {

            }
        }));
    }*/

    /**
     * Method to update the displayed Moodle-Courses.
     */
    protected void updateCourses() {
        //view.setData(setData());
    }

    /**
     * Method to refresh the course-list (in Combo box).
     */
    protected void refreshCourseList() {
        //TODO Listener hinzufügen um nicht immer Aktualisieren zu müssen
        view.setCourses(courses());
    }

    /*
    @Override
    public void onCreated(FileEvent event) {
        view.setData(setData());
    }

    @Override
    public void onModified(FileEvent event) {
        view.setData(setData());
    }

    @Override
    public void onDeleted(FileEvent event) {
        view.setData(setData());
    }

    protected void openCourseDirectory() {
        Desktop desktop = Desktop.getDesktop();
        try {
            File dirToOpen = new File(config.getSyncRootPath() + "/" + course.getDisplayname());
            desktop.open(dirToOpen);
        } catch (Throwable e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.path.unknown.message");
        }
    }

    //private List<FileServerFile> provideFileserverFiles(String pathname) throws Exception {
    //    List<FileServerFile> files = List.of();
    //    if (!VerifyDataService.validateString(config.getFileserver()) || !VerifyDataService.validateString(config
    //    .getUserFileserver()) || !VerifyDataService.validateString(config.getPasswordFileserver())) {
    //        showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.fileserver1
    //        .message");
    //    } else {
    //        try {
    //            fileClient = new FileServerClientFTP(config);
    //            fileClient.connect();
    //            files = fileClient.getFiles(/*config.getRecentSection().getName()*/// ""); //ToDo -> If there
    // should be
                // support for different upload-sections.
    //            fileClient.disconnect();
    //        } catch (Exception e) {
    //            logException(e, "Sync failed");
    //            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.fileserver2" +
    //                    ".message");
    //        }
    //    }
    //    fileServerRequired = true;

    //    return files;
    //}

}

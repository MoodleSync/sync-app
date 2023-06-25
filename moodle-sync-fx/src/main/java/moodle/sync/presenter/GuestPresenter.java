package moodle.sync.presenter;

import com.google.common.eventbus.Subscribe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import moodle.sync.core.config.DefaultConfiguration;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.fileserver.FileServerClientFTP;
import moodle.sync.core.fileserver.FileServerFile;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.Module;
import moodle.sync.core.model.json.Section;
import moodle.sync.core.util.FileDownloadService;
import moodle.sync.core.util.FileWatcherService.FileEvent;
import moodle.sync.core.util.FileWatcherService.FileListener;
import moodle.sync.core.util.FileWatcherService.FileWatcher;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.core.web.service.MoodleService;
import moodle.sync.event.DownloadItemEvent;
import moodle.sync.javafx.model.ReturnValue;
import moodle.sync.javafx.model.SyncTableElement;
import moodle.sync.presenter.command.ShowSettingsCommand;
import moodle.sync.util.FileService;
import moodle.sync.util.VerifyDataService;
import moodle.sync.view.GuestStartView;
import moodle.sync.view.StartView;
import org.apache.commons.io.FilenameUtils;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.beans.BooleanProperty;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;

import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.isNull;

/**
 * Class defining the logic of the "start-page".
 *
 * @author Daniel Schröter
 */
public class GuestPresenter extends Presenter<GuestStartView> implements FileListener {

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
    GuestPresenter(ApplicationContext context, GuestStartView view, ViewContextFactory viewFactory,
                   MoodleService moodleService) {
        super(context, view);
        this.moodleService = moodleService;
        this.config = (MoodleSyncConfiguration) context.getConfiguration();
        this.selectAll = new BooleanProperty(false);
    }

    @Override
    public void initialize() {
        System.out.println("GuestPresenter initialized");
        context.getEventBus().register(this);

        //Initialising all functions of the "start-page" with the help of the configuration.
        String syncPath = config.getSyncRootPath();
        //Check whether a default path should be used to prevent unwanted behavior.
        if (!VerifyDataService.validateString(syncPath)) {
            DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
            config.setSyncRootPath(defaultConfiguration.getSyncRootPath());
        }

        view.setOnUpdate(this::updateCourses);
        //view.setOnSettings(this::onSettings);
        view.setOnDownloadCourse(this::onDownloadCourse);
        view.setCourse(config.recentCourseProperty());
        view.setCourses(courses());
        view.setSection(config.recentSectionProperty());
        view.setSections(sections());
        view.setOnCourseChanged(this::onCourseChanged);
        view.setData(setData());
        view.setOnFolder(this::openCourseDirectory);
        updateBottomLine();
        view.setProgress(0.0);

        //Refresh Table if another section is chosen.
        config.recentSectionProperty().addListener((observable, oldSection, newSection) -> {
            section = config.getRecentSection();
            updateBottomLine();
            view.setData(setData());
        });

        //Refresh course-list if new URL is entered.
        config.moodleUrlProperty().addListener((observable, oldUrl, newUrl) -> {
            config.setRecentCourse(null);
            config.setRecentSection(null);
            course = null;
            section = null;
            //TODO: hier werden dann 4 mal getcontents ausgelöst
        });

        CompletableFuture
                .runAsync(() -> {initCourse();} )
                .exceptionally(e -> {
                    logException(e, "Inititialize course " + "failed");
                    return null;
                });

    }

    private void initCourse() {
        try{
            if(!isNull(config.recentCourseProperty().get())) {
                //selectCourse(config.recentCourseProperty().get());
            }
        } catch (Exception e){
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "Already choosen course not available");
            config.setRecentCourse(null);
        }
    }

    //Show selected section if a module is clicked.
    @Subscribe
    public void onElementClicked(SyncTableElement selectedSection) {
        view.setSectionId(selectedSection.getSection().toString());
    }

    /**
     * Method which prepares the displayed table.
     *
     * @return Returns a list of SyncTableElements, which each represents a course module or a section or a local
     * file/directory.
     */
    private ObservableList<SyncTableElement> setData() {
        view.setProgress(0.0);
        token = config.getMoodleToken();
        section = config.getRecentSection();
        if (isNull(course))
            return FXCollections.observableArrayList();
        try {
            //Close all existing watchers.
            if (watcher != null)
                watcher.close();
        }
        catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
        }

        //sectionList: if "all sections" is chosen, all section-directories are stored. -> Needed to detect new
        // sections.
        List<Path> sectionList = List.of();

        ObservableList<SyncTableElement> data = FXCollections.observableArrayList();

        try {
            //If no section is selected, or "all" are selected, directories are checked and coursecontent is set.
            if (isNull(section) || section.getId() == -2) {
                //Check if course-folder exists, otherwise create one.
                Path courseDirectory = Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname());
                FileService.directoryManager(courseDirectory);
                //Initialize sectionList with folders inside course-directory.
                sectionList = FileService.getPathsInDirectory(courseDirectory);
                if (!isNull(courseContent))
                    courseContent.clear();
                //Initialize courseContent with all sections.
                courseContent = sections();
            } //Handling if a specific section is chosen.
            else {
                if (!isNull(courseContent))
                    courseContent.clear();
                //Initialize courseContent only with the content of the specific section.
                courseContent.add(moodleService.getCourseContentSection(token, course.getId(), section.getId()).get(0));
            }


            for (Section section : courseContent) {
                if (section.getId() != -2) {
                    data.add(new SyncTableElement(section.getName(), section.getId(), section.getSection(), section.getId(), data.size(), section.getSummary(), "", false, false, MoodleAction.ExistingSection, section.getVisible() == 1, true));

                    Path execute =
                            Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname() + "/" + section.getSection() +
                                    "_" + section.getName());
                    FileService.directoryManager(execute);
                    List<List<Path>> localContent =
                            FileService.sortDirectoryFiles(FileService.getPathsInDirectory(execute),
                                    config.getFormatsMoodle(), config.getFormatsFileserver());
                    for (Module module : section.getModules()) {
                        if (!isNull(module.getContents()) && Objects.equals(module.getModname(), "resource")) {

                            ReturnValue elem = FileService.findResourceInFiles(localContent.get(0), module,
                                    section.getSection(), section.getId(), data.size());
                            localContent.set(0, elem.getFileList());
                            data.add(elem.getElement());
                            //SyncTableElement element = new SyncTableElement(module.getName(), module.getId(),
                              //      section.getSection(), section.getId(), data.size(), module.getModname(),
                                //    module.getContents().get(0).getTimemodified().toString(),
                                  //  module.getContents().get(0).getFilename(), false, false,
                                   // MoodleAction.NotLocalFile, module.getUservisible(), module.getUservisible());
                            if(elem.getElement().getDownloadable()) {
                                elem.getElement().setSectionName(section.getName());
                            }
                            //data.add(element);
                        } else {

                            data.add(new SyncTableElement(module.getName(), module.getId(), section.getSection(), section.getId(), data.size(), module.getModname(), "", false, false, MoodleAction.NotLocalFile, module.getUservisible(), module.getUservisible()));
                        }
                    }
                }
            }

            courseData = data;

            watcher = new FileWatcher(new File(config.getSyncRootPath() + "/" + course.getDisplayname()));
            watcher.addListener(this).watch();

        } catch (Exception e){
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
        }
        return data;
    }


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

    private List<FileServerFile> provideFileserverFiles(String pathname) throws Exception {
        List<FileServerFile> files = List.of();
        if (!VerifyDataService.validateString(config.getFileserver()) || !VerifyDataService.validateString(config.getUserFileserver()) || !VerifyDataService.validateString(config.getPasswordFileserver())) {
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.fileserver1.message");
        } else {
            try {
                fileClient = new FileServerClientFTP(config);
                fileClient.connect();
                files = fileClient.getFiles(/*config.getRecentSection().getName()*/ ""); //ToDo -> If there should be
                // support for different upload-sections.
                fileClient.disconnect();
            } catch (Exception e) {
                logException(e, "Sync failed");
                showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.fileserver2" +
                        ".message");
            }
        }
        fileServerRequired = true;

        return files;
    }

    //Folgende Methoden sollten eigentlich im StartPresenter sein -> Erben

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

    private void onDownloadCourse() {
        try{
            try{
                watcher.close();
            } catch (Exception e) {
                logException(e, "Sync failed");
            }
            Task task = new Task<Void>() {
                @Override
                public Void call() throws Exception {
                    double count = 0;
                    double counter = 0;
                    for (SyncTableElement courseData : courseData) {
                        if (courseData.getDownloadable()) {
                            count++;
                        }
                    }
                    view.setProgress(0.0);
                    for (SyncTableElement courseData : courseData) {
                        if (courseData.getDownloadable()) {
                            FileDownloadService.getFile(courseData.getFileUrl(), token, config.getSyncRootPath() + "/" + course.getDisplayname() + "/" + courseData.getSection() + "_" + courseData.getSectionName(), courseData.getExistingFileName(), courseData.getExistingFile());
                            counter++;
                            view.setProgress(counter / count);
                        }
                    }
                    return view.setData(setData());
                }
            };
            new Thread(task).start();
            } catch (Exception e) {
                logException(e, "Sync failed");
            }

            //view.setData(setData());
        }

    protected List<Section> sections() {
        System.out.println("Sections GuestPresenter");
        if (course == null) {
            return new ArrayList<>();
        }
        try {
            //List<Section> content = moodleService.getCourseContent(token, course.getId());
            //content.add(0, new Section(-2, this.context.getDictionary().get("start.sync.showall"), 1, "all", -1, -1,
            //        -1, true, null));
            //courseContent = content;
            //return content;
        }
        catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.invalidurl.message");
        }
        return new ArrayList<>();
    }

    //Update view if course was changed.
    protected void onCourseChanged(Course course) {
        //updateBottomLine();
        //view.setSections(sections());
        //super.close();
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


    /**
     * Method to "open" the Settings-page.
     */

    /**
     * Method to update the displayed Moodle-Courses.
     */
    protected void updateCourses() {
        view.setData(setData());
    }

    /**
     * Method to refresh the course-list (in Combo box).
     */
    protected void refreshCourseList() {
        //TODO Listener hinzufügen um nicht immer Aktualisieren zu müssen
        view.setCourses(courses());
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
}

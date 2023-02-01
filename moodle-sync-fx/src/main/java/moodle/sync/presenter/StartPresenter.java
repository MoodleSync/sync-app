package moodle.sync.presenter;

import javax.inject.Inject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import moodle.sync.core.config.DefaultConfiguration;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.model.json.*;
import moodle.sync.core.fileserver.FileServerClientFTP;
import moodle.sync.core.fileserver.FileServerFile;
import moodle.sync.core.model.json.Module;
import moodle.sync.util.VerifyDataService;
import moodle.sync.javafx.model.ReturnValue;
import moodle.sync.presenter.command.ShowSettingsCommand;
import moodle.sync.util.FileService;
import moodle.sync.core.util.FileWatcherService.FileEvent;
import moodle.sync.core.util.FileWatcherService.FileListener;
import moodle.sync.core.util.FileWatcherService.FileWatcher;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.javafx.model.syncTableElement;

import moodle.sync.util.SetModuleService;
import org.apache.commons.io.FilenameUtils;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.beans.BooleanProperty;
import org.lecturestudio.core.presenter.Presenter;

import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;

import moodle.sync.view.StartView;
import moodle.sync.core.web.service.MoodleService;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Class defining the logic of the "start-page".
 *
 * @author Daniel Schröter
 */
public class StartPresenter extends Presenter<StartView> implements FileListener {

    private final ViewContextFactory viewFactory;

    //Used MoodleService for executing Web Service API-Calls.
    private final MoodleService moodleService;

    //Configuration providing the settings.
    private final MoodleSyncConfiguration config;

    //Providing the content of a course. Used for the section-combobox.
    private List<Section> courseContent;

    //List representing the actual courseData with the planned/possible changes.
    private ObservableList<syncTableElement> courseData;

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
    StartPresenter(ApplicationContext context, StartView view, ViewContextFactory viewFactory,
                   MoodleService moodleService) {
        super(context, view);
        this.viewFactory = viewFactory;
        this.moodleService = moodleService;
        this.config = (MoodleSyncConfiguration) context.getConfiguration();
        this.selectAll = new BooleanProperty(false);
    }

    @Override
    public void initialize() {
        //Initialising all functions of the "start-page" with the help of the configuration.
        String syncPath = config.getSyncRootPath();
        //Check whether a default path should be used to prevent unwanted behavior.
        if (!VerifyDataService.validateString(syncPath)) {
            DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
            config.setSyncRootPath(defaultConfiguration.getSyncRootPath());
        }

        view.setOnUpdate(this::updateCourses);
        view.setOnSync(this::onSync);
        view.setOnSettings(this::onSettings);
        view.setCourse(config.recentCourseProperty());
        view.setCourses(courses());
        view.setSection(config.recentSectionProperty());
        view.setSections(sections());
        view.setOnCourseChanged(this::onCourseChanged);
        view.setData(setData());
        view.setOnFolder(this::openCourseDirectory);
        view.setSelectAll(selectAll);
        updateBottomLine();

        //Display the course-sections after Moodle-course is choosen.
        config.recentCourseProperty().addListener((observable, oldCourse, newCourse) -> {
            course = config.getRecentCourse(); //Todo hier schauen ob das ausreicht!
            config.setRecentSection(null);
            updateBottomLine();
            view.setData(setData());
        });

        config.recentSectionProperty().addListener((observable, oldSection, newSection) -> {
            section = config.getRecentSection();
            updateBottomLine();
            view.setData(setData());
        });

        config.moodleUrlProperty().addListener((observable, oldUrl, newUrl) -> {
            config.setRecentCourse(null);
            config.setRecentSection(null);
            course = null;
            section = null;
        });

        selectAll.addListener((observable, oldUrl, newUrl) -> {
            if (newUrl) {
                for (syncTableElement elem : courseData) {
                    if (elem.isSelectable()) {
                        elem.selectedProperty().setValue(true);
                    }
                }
            } else {
                for (syncTableElement elem : courseData) {
                    if (elem.isSelectable()) {
                        elem.selectedProperty().setValue(false);
                    }
                }
            }
        });
    }

    private void onCourseChanged(Course course) {
        updateBottomLine();
        view.setSections(sections());
    }

    private void updateBottomLine() {
        if (isNull(course)) {
            view.setCourseId("");
            view.setSectionId("");
        } else if (isNull(section) || section.getSection() == -1) {
            view.setCourseId(course.getId().toString());
            view.setSectionId("");
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
    private List<Course> courses() {
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
    private List<Section> sections() {
        if (course == null) {
            return new ArrayList<>();
        }
        try {
            List<Section> content = moodleService.getCourseContent(token, course.getId());
            content.add(0, new Section(-2, this.context.getDictionary().get("start.sync.showall"), 1, "all", -1, -1,
                    -1, true, null));
            courseContent = content;
            return content;
        }
        catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.invalidurl.message");
        }
        return new ArrayList<>();
    }

    private void onSettings() {
        context.getEventBus().post(new ShowSettingsCommand(this::refreshCourseList));
    }


    /**
     * Starts the sync-process.
     */
    private void onSync() {
        //Serveral security checks to prevent unwanted behaviour.
        if (config.getRecentCourse() == null) {
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.course.message");
            return;
        }
        if (!Files.isDirectory(Paths.get(config.getSyncRootPath()))) {
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.path.message");
            return;
        }
        for (syncTableElement courseData : courseData) {
            try {
                if (courseData.isSelected()) {
                    if (courseData.getModuleType().equals("resource")) {
                        SetModuleService.publishResource(moodleService, courseData, course, url, token);
                    }
                    else if (courseData.getAction() == MoodleAction.FTPUpload) {
                        SetModuleService.publishFileserverResource(moodleService, courseData, course, token);
                    }
                    else if (courseData.getModuleType().equals("folder")) {
                        SetModuleService.handleFolderUpload(moodleService, courseData, course, url, token);
                    }
                    else if (courseData.getAction() != MoodleAction.UploadSection) {
                        SetModuleService.moveResource(moodleService, courseData, token);
                    }
                }
            }
            catch (Exception e) {
                logException(e, "Sync failed");

                showNotification(NotificationType.ERROR, "start.sync.error.title",
                        MessageFormat.format(context.getDictionary().get("start.sync.error.upload.message"),
                                courseData.getModuleName()));
            }
        }
        //Adding of new sections at the end of the sync-process to prevent new section-numbers
        for (syncTableElement courseData : courseData) {
            if (courseData.getAction() == MoodleAction.UploadSection && courseData.isSelected()) {
                //Logic for Section-Upload
                try {
                    SetModuleService.createSection(moodleService, courseData, course, token);
                } catch (Exception e) {
                    logException(e, "Sync failed");

                    showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.upload" +
                            ".message");
                }
            }
        }
        updateCourses();
    }

    /**
     * Method to update the displayed Moodle-Courses.
     */
    private void updateCourses() {
        view.setData(setData());
    }

    private void refreshCourseList() {
        view.setCourses(courses());
    }

    private ObservableList<syncTableElement> setData() {
        token = config.getMoodleToken();
        section = config.getRecentSection();
        if (/*isNull(courseContent)||*/ isNull(course))
            return FXCollections.observableArrayList();

        try {
            if (watcher != null)
                watcher.close();
        } catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
        }

        //sectionList: if all sections are chosen, all section-directories are stored. Needed to detect new sections.
        List<Path> sectionList = List.of();

        ObservableList<syncTableElement> data = FXCollections.observableArrayList();

        try {
            //If no section is selected, or "all" are selected, directories are checked and coursecontent is set.
            if (isNull(section) || section.getId() == -2) {
                //Check if course-folder exists, otherwise create one.
                Path courseDirectory = Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname());
                FileService.directoryManager(courseDirectory);
                sectionList = FileService.getPathsInDirectory(courseDirectory);
                if (!isNull(courseContent)) courseContent.clear();
                courseContent = sections();
            } //Handling if a specific section is chosen.
            else {
                if (!isNull(courseContent)) courseContent.clear();
                //TODO replace this
                courseContent.add(moodleService.getCourseContentSection(token, course.getId(), section.getId()).get(0));
            }

            //first: check if user has permission to see full course-content incl. not available files
            try {
                for (Section section : courseContent) {
                    if (section.getId() != -2) {
                        for (Module module : section.getModules()) {
                            if(module.getModname().equals("resource")){
                                module.getContents().get(0);
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                logException(e, "Sync failed");
                showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.permissions");
                return setGuestData();
            }


            for (Section section : courseContent) {
                //Handle every section on its own
                //Section "select all" should not be considered
                if (section.getId() != -2) {
                    String sectionName = section.getName();
                    int sectionNum = section.getSection();
                    int sectionId = section.getId();

                    //Add section element
                    data.add(new syncTableElement(sectionName, sectionId, sectionNum, sectionId, data.size(),
                            section.getSummary(), false, false, MoodleAction.ExistingSection,
                            section.getVisible() == 1));

                    //Create or sort section directory

                    Path execute = Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname() + "/" +
                            section.getSection() + "_" + sectionName);
                    sectionList = FileService.formatSectionFolder(sectionList, section);
                    FileService.directoryManager(execute);

                    //Initialize the fileServerRequired variable todo.
                    fileServerRequired = false;
                    List<FileServerFile> files = List.of();

                    //Watcher is added to choosen section-directory.
                    watcher = new FileWatcher(new File(execute.toString()));
                    watcher.addListener(this).watch();

                    //Sort files inside section-directory by format types: MoodleFormats, FileserverFormats,
                    // Directories and Other
                    List<List<Path>> localContent =
                            FileService.sortDirectoryFiles(FileService.getPathsInDirectory(execute),
                                    config.getFormatsMoodle(), config.getFormatsFileserver());
                    //Iterate over section-content on Moodle
                    for (Module module : section.getModules()) {
                        if (module.getModname().equals("resource")) {
                            //If file is local, it must be in localContent[0]
                            ReturnValue elem = FileService.findResourceInFiles(localContent.get(0), module,
                                    sectionNum, sectionId, data.size());
                            localContent.set(0, elem.getFileList());
                            data.add(elem.getElement());
                        } else if (module.getModname().equals("url")) {
                            //TODO
                            data.add(new syncTableElement(module.getName(), module.getId(), sectionNum, sectionId,
                                    data.size(), module.getModname(), false, false, MoodleAction.NotLocalFile,
                                    module.getVisible() == 1));
                        } else if (module.getModname().equals("folder")) {
                            int pos = FileService.findModuleInList(localContent.get(2), module);
                            if (pos >= 0) {
                                data.add(FileService.checkDirectoryForUpdates(localContent.get(2).get(pos), module,
                                        sectionNum, sectionId, data.size(), config.getFormatsMoodle()));
                                localContent.get(2).remove(pos);
                            } else {
                                data.add(new syncTableElement(module.getName(), module.getId(), sectionNum, sectionId
                                        , data.size(), module.getModname(), false, false, MoodleAction.NotLocalFile,
                                        module.getVisible() == 1));
                            }
                        } else {
                            data.add(new syncTableElement(module.getName(), module.getId(), sectionNum, sectionId,
                                    data.size(), module.getModname(), false, false, MoodleAction.NotLocalFile,
                                    module.getVisible() == 1));
                        }
                    }

                    //If localContent[X] is not empty, those elements need to be uploaded.
                    if (!localContent.get(0).isEmpty()) {
                        for (Path file : localContent.get(0)) {
                            data.add(new syncTableElement(file.getFileName().toString(), -1, sectionNum, sectionId,
                                    data.size(), "resource", file, true, false, MoodleAction.MoodleUpload, true));
                        }
                    }
                    if (!localContent.get(1).isEmpty()) {
                        for (Path file : localContent.get(1)) {
                            data.add(new syncTableElement(file.getFileName().toString(), -1, sectionNum, sectionId,
                                    data.size(), "url", file, true, false, MoodleAction.FTPUpload, true));
                        }
                    }
                    if (!localContent.get(2).isEmpty()) {
                        String formatsMoodle = config.getFormatsMoodle();
                        for (Path directory : localContent.get(2)) {
                            List<Path> content = FileService.getPathsInDirectory(directory);
                            content.removeIf(file -> !formatsMoodle.contains(FilenameUtils.getExtension(String.valueOf(file))));
                            data.add(new syncTableElement(directory.getFileName().toString(), -1, sectionNum,
                                    sectionId, data.size(), "folder", directory, true, false,
                                    MoodleAction.FolderUpload, true, -1, null, content, -1));
                        }
                    }
                    if (!localContent.get(3).isEmpty()) {
                        for (Path file : localContent.get(3)) {
                            if (config.getShowUnknownFormats()) {
                                data.add(new syncTableElement(file.getFileName().toString(), -1, sectionNum,
                                        sectionId, data.size(), "", file, false, false, MoodleAction.DatatypeNotKnown
                                        , false));
                            }
                        }
                    }

                }
            }
            if (!sectionList.isEmpty()) {
                for (Path elem : sectionList) {
                    data.add(new syncTableElement(elem.getFileName().toString(), -1, -1, -1, data.size(), "section",
                            elem, true, false, MoodleAction.UploadSection, true));
                }
            }
        } catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
        }

        courseData = data;

        //Add FileWatcher
        watcher = new FileWatcher(new File(config.getSyncRootPath() + "/" + course.getDisplayname()));
        watcher.addListener(this).watch();

        return data;

    }

    private ObservableList<syncTableElement> setGuestData() {
        ObservableList<syncTableElement> data = FXCollections.observableArrayList();
        for (Section section : courseContent) {
            if (section.getId() != -2) {
                data.add(new syncTableElement(section.getName(), section.getId(), section.getSection(),
                        section.getId(), data.size(), section.getSummary(), false, false,
                        MoodleAction.ExistingSection, section.getVisible() == 1));

                for (Module module : section.getModules()) {
                    data.add(new syncTableElement(module.getName(), module.getId(), section.getSection(),
                            section.getId(), data.size(), module.getModname(), false, false,
                            MoodleAction.NotLocalFile, module.getVisible() == 1));
                }
            }
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

    private void openCourseDirectory() {
        Desktop desktop = Desktop.getDesktop();
        try {
            File dirToOpen = new File(config.getSyncRootPath() + "/" + course.getDisplayname());
            desktop.open(dirToOpen);
        } catch (Throwable e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.path.unknown.message");
        }
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
}

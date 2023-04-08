package moodle.sync.presenter;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;
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
import moodle.sync.util.SetModuleService;
import moodle.sync.util.FileService;
import moodle.sync.view.StartView;
import moodle.sync.presenter.command.ShowSettingsCommand;

import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.beans.BooleanProperty;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;

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
        context.getEventBus().register(this);

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

        //Display the course-sections after Moodle-course is chosen.
        config.recentCourseProperty().addListener((observable, oldCourse, newCourse) -> {
            course = config.getRecentCourse();
            config.setRecentSection(null);
            updateBottomLine();
            view.setData(setData());
        });

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
        });

        //"Select-All"-Button clicked.
        selectAll.addListener((observable, oldUrl, newUrl) -> {
            if (newUrl) {
                for (SyncTableElement elem : courseData) {
                    if (elem.isSelectable()) {
                        elem.selectedProperty().setValue(true);
                    }
                }
            } else {
                for (SyncTableElement elem : courseData) {
                    if (elem.isSelectable()) {
                        elem.selectedProperty().setValue(false);
                    }
                }
            }
        });
    }

    //Show selected section if a module is clicked.
    @Subscribe
    public void onSectionClicked(SyncTableElement selectedSection) {
        view.setSectionId(selectedSection.getSection().toString());
    }

    //Update view if course was changed.
    private void onCourseChanged(Course course) {
        updateBottomLine();
        view.setSections(sections());
    }

    //Show course-id and section number at the bottom.
    private void updateBottomLine() {
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

    /**
     * Method to "open" the Settings-page.
     */
    private void onSettings() {
        context.getEventBus().post(new ShowSettingsCommand(this::refreshCourseList));
    }


    /**
     * Starts the sync-process.
     */
    private void onSync() {
        //Several security checks to prevent unwanted behaviour.
        if (config.getRecentCourse() == null) {
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.course.message");
            return;
        }
        //Checks whether Root-Directory is existing.
        if (!Files.isDirectory(Paths.get(config.getSyncRootPath()))) {
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.path.message");
            return;
        }
        //Calls the API-Call functions depending on the "selected" property and the MoodleAction.
        for (SyncTableElement courseData : courseData) {
            try {
                if (courseData.isSelected()) {
                    if (courseData.getModuleType().equals("resource")) {
                        SetModuleService.publishResource(moodleService, courseData, course, url, token);
                    }
                    else if (courseData.getAction() == MoodleAction.FTPUpload) {
                        SetModuleService.publishFileserverResource(moodleService, courseData, course, token);
                    }
                    else if (courseData.getModuleType().equals("folder") && courseData.getAction() != MoodleAction.ExistingFile) {
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
        //Adding of new sections at the end of the sync-process to prevent new section-numbers.
        for (SyncTableElement courseData : courseData) {
            if (courseData.getAction() == MoodleAction.UploadSection && courseData.isSelected()) {
                //Logic for Section-Upload
                try {
                    SetModuleService.createSection(moodleService, courseData, course, token);
                }
                catch (Exception e) {
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

    /**
     * Method to refresh the course-list (in Combo box).
     */
    private void refreshCourseList() {
        //TODO Listener hinzufügen um nicht immer Aktualisieren zu müssen
        view.setCourses(courses());
    }

    /**
     * Method which prepares the displayed table.
     *
     * @return Returns a list of SyncTableElements, which each represents a course module or a section or a local
     * file/directory.
     */
    private ObservableList<SyncTableElement> setData() {
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
                Path courseDirectory = Paths.get(config.getSyncRootPath() + "/" + course.getShortname());
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

            //Iterate over each section and put content in List "data" -> create SyncTableElements.
            for (Section section : courseContent) {
                //Handle every section on its own
                //Section "select all" should not be considered
                if (section.getId() != -2) {
                    String sectionName = section.getName();
                    int sectionNum = section.getSection();
                    int sectionId = section.getId();

                    //Add section element
                    data.add(new SyncTableElement(sectionName, sectionId, sectionNum, sectionId, data.size(),
                            section.getSummary(), false, false, MoodleAction.ExistingSection,
                            section.getVisible() == 1, true));

                    //Create or sort section directory
                    Path execute =
                            Paths.get(config.getSyncRootPath() + "/" + course.getShortname() + "/" + section.getSection() + "_" + sectionName);
                    sectionList = FileService.formatSectionFolder(sectionList, section); //Formats section
                        // folder-list -> if Section 3 in Moodle names "Test", inside the course directory, the
                        // sections-directory should be called 3_Test.
                    FileService.directoryManager(execute); //Create section directory if no directory with the
                        // sections name exists.

                    //Initialize the fileServerRequired variable, used to check if the fileserver is required.
                    fileServerRequired = false;
                    List<FileServerFile> files = List.of();

                    //Watcher is added to chosen section-directory.
                    watcher = new FileWatcher(new File(execute.toString()));
                    watcher.addListener(this).watch();

                    //Sort files inside section-directory by format types: MoodleFormats, FileserverFormats,
                    // Directories and Other.
                    List<List<Path>> localContent =
                            FileService.sortDirectoryFiles(FileService.getPathsInDirectory(execute),
                                    config.getFormatsMoodle(), config.getFormatsFileserver());
                    //Iterate over section-content on Moodle
                    for (Module module : section.getModules()) {
                        switch (module.getModname()) {
                            case "resource" -> {
                                //If file is local, it must be in localContent[0]
                                //check if user has permission to see full course-content incl. not available files.
                                try {
                                    ReturnValue elem = FileService.findResourceInFiles(localContent.get(0), module,
                                            sectionNum, sectionId, data.size());
                                    localContent.set(0, elem.getFileList());
                                    data.add(elem.getElement());
                                } catch (Exception e) {
                                    showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync" +
                                            ".error" + ".permissions");
                                    return setGuestData();
                                }
                            }
                            case "url" ->
                                // TODO: FileServerSupport not functional. -> Check if file in Link is newer than the
                                // link-module.
                                    data.add(new SyncTableElement(module.getName(), module.getId(), sectionNum,
                                            sectionId, data.size(), module.getModname(), false, false,
                                            MoodleAction.NotLocalFile, module.getVisible() == 1, module.getUservisible()));
                            case "folder" -> {
                                //Check if folder is existent in section-directory.
                                int pos = FileService.findModuleInList(localContent.get(2), module);
                                if (pos >= 0) {
                                    //If it exists, check if it should be updated.
                                    data.add(FileService.checkDirectoryForUpdates(localContent.get(2).get(pos),
                                            module, sectionNum, sectionId, data.size(), config.getFormatsMoodle()));
                                    localContent.get(2).remove(pos);
                                } else {
                                    data.add(new SyncTableElement(module.getName(), module.getId(), sectionNum,
                                            sectionId, data.size(), module.getModname(), false, false,
                                            MoodleAction.NotLocalFile, module.getVisible() == 1, module.getUservisible()));
                                }
                            }
                            default ->
                                    data.add(new SyncTableElement(module.getName(), module.getId(), sectionNum,
                                            sectionId, data.size(), module.getModname(), false, false,
                                            MoodleAction.NotLocalFile, module.getVisible() == 1, module.getUservisible()));
                        }
                    }

                    //If localContent[X] is not empty, those elements need to be uploaded.
                    if (!localContent.get(0).isEmpty()) {
                        for (Path file : localContent.get(0)) {
                            data.add(new SyncTableElement(file.getFileName().toString(), -1, sectionNum, sectionId,
                                    data.size(), "resource", file, true, false, MoodleAction.MoodleUpload, true, true));
                        }
                    }
                    if (!localContent.get(1).isEmpty()) {
                        for (Path file : localContent.get(1)) {
                            data.add(new SyncTableElement(file.getFileName().toString(), -1, sectionNum, sectionId,
                                    data.size(), "url", file, true, false, MoodleAction.FTPUpload, true, true));
                        }
                    }
                    if (!localContent.get(2).isEmpty()) {
                        String formatsMoodle = config.getFormatsMoodle();
                        for (Path directory : localContent.get(2)) {
                            List<Path> content = FileService.getPathsInDirectory(directory);
                            content.removeIf(file -> !formatsMoodle.contains(FilenameUtils.getExtension(String.valueOf(file))));
                            data.add(new SyncTableElement(directory.getFileName().toString(), -1, sectionNum,
                                    sectionId, data.size(), "folder", directory, true, false,
                                    MoodleAction.FolderUpload, true, -1, null, content, -1, true));
                        }
                    }
                    if (!localContent.get(3).isEmpty()) {
                        for (Path file : localContent.get(3)) {
                            if (config.getShowUnknownFormats()) {
                                data.add(new SyncTableElement(file.getFileName().toString(), -1, sectionNum,
                                        sectionId, data.size(), "", file, false, false, MoodleAction.DatatypeNotKnown
                                        , false, true));
                            }
                        }
                    }

                }
            }
            //If sectionList is not empty, the remaining directories are new sections which could be created.
            if (!sectionList.isEmpty()) {
                for (Path elem : sectionList) {
                    data.add(new SyncTableElement(elem.getFileName().toString(), -1, -1, -1, data.size(), "section",
                            elem, true, false, MoodleAction.UploadSection, true, true));
                }
            }
        } catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
        }

        courseData = data;

        //Add FileWatcher in course-directory to detect added sections.
        watcher = new FileWatcher(new File(config.getSyncRootPath() + "/" + course.getShortname()));
        watcher.addListener(this).watch();

        return data;

    }

    /**
     * Method which is used to display a course if the user only has "visitor" / "student" permissions.
     *
     * @return Initializes the list with only a reduced view (Hidden Modules are not shown, changes are not allowed).
     */
    private ObservableList<SyncTableElement> setGuestData() {
        ObservableList<SyncTableElement> data = FXCollections.observableArrayList();
        for (Section section : courseContent) {
            if (section.getId() != -2) {
                data.add(new SyncTableElement(section.getName(), section.getId(), section.getSection(),
                        section.getId(), data.size(), section.getSummary(), false, false,
                        MoodleAction.ExistingSection, section.getVisible() == 1, true));

                for (Module module : section.getModules()) {
                    data.add(new SyncTableElement(module.getName(), module.getId(), section.getSection(),
                            section.getId(), data.size(), module.getModname(), false, false,
                            MoodleAction.NotLocalFile, module.getUservisible(), module.getUservisible()));
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
            File dirToOpen = new File(config.getSyncRootPath() + "/" + course.getShortname());
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

package moodle.sync.presenter;

import org.apache.commons.io.FilenameUtils;
import com.google.common.eventbus.Subscribe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
import moodle.sync.util.*;
import moodle.sync.view.TrainerStartView;

import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.beans.BooleanProperty;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.view.NotificationType;
import org.lecturestudio.core.view.ViewContextFactory;
import org.lecturestudio.javafx.util.FxUtils;

import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
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
public class TrainerPresenter extends Presenter<TrainerStartView> implements FileListener {

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

    //If users view should be guest- or trainer-mode.
    private Boolean guest;

    //To check if the courseCombo needs to be refreshed.
    private int courseCount = 0;

    //To check if the sectionCombo needs to be refreshed.
    private int sectionCount = 0;


    @Inject
    TrainerPresenter(ApplicationContext context, TrainerStartView view, ViewContextFactory viewFactory,
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
        view.setOnDownloadCourse(this::onDownloadCourse);
        view.setOnOpenWiki(this::onWiki);
        view.setCourse(config.recentCourseProperty());
        view.setCourses(courses());
        view.setSection(config.recentSectionProperty());
        view.setOnCourseChanged(this::changeCourse);
        view.setOnSectionChanged(this::changeSection);
        view.setProgress(0.0);
        view.setOnFolder(this::openCourseDirectory);
        view.setSelectAll(selectAll);

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

        //Init course
        CompletableFuture
                .runAsync(() -> {changeCourse(config.getRecentCourse());} )
                .exceptionally(e -> {
                    logException(e, "Initialize course " + "failed");
                    return null;
                });

    }

    //Show selected section if a module is clicked.
    @Subscribe
    public void onElementClicked(SyncTableElement selectedSection) {
        view.setSectionId(selectedSection.getSection().toString());
    }

    //A specific file should be downloaded.
    @Subscribe
    public void onDownloadItem(DownloadItemEvent event) {
        onDownloadFile(event.getElement());
    }

    //Show a popup when a course-download is finished.
    private void popUpDownload() {
        PopupUtil.popUpDownload(context);
    }

    //Webservice call to get all sections of a course.
    private List<Section> sections() {
        if (course == null) {
            return new ArrayList<>();
        }
        try {
            return moodleService.getCourseContent(token, course.getId());
        }
        catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.invalidurl.message");
        }
        return new ArrayList<>();
    }

    //Webservice call to get the content of a specific section of a course.
    private List<Section> section() {
        if (course == null) {
            return new ArrayList<>();
        }
        try {
            return moodleService.getCourseContentSection(token, course.getId(), section.getId());
        }
        catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.invalidurl.message");
        }
        return new ArrayList<>();
    }

    //Show course-id and section number at the bottom.
    private void updateBottomLine() {
        String emptySection = this.context.getDictionary().get("start.labelsection.empty");
        if (isNull(course)) {
            view.setCourseId(this.context.getDictionary().get("start.labelcourse.empty"));
            view.setSectionId(emptySection);
        } else if (isNull(section) || section.getSection() == -1) {
            view.setCourseId(course.getId().toString());
            view.setSectionId(emptySection);
        } else {
            view.setCourseId(course.getId().toString());
            view.setSectionId(section.getSection().toString());
        }
    }

    //Webservice call to get all users courses.
    private List<Course> courses() {
        url = config.getMoodleUrl();
        moodleService.setApiUrl(url);
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
            courseCount = courses.size();
        }
        catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error" +
                    ".invalidurl.message");
            config.setRecentCourse(null);
            course = null;
            guest = true;
            clearView();
        }

        //Do not show Moodle-courses which are already over.
        if (!courses.isEmpty()) {
            courses.removeIf(item -> (item.getEnddate() != 0 && (item.getEnddate() < System.currentTimeMillis() / 1000)));
        }

        return courses;
    }

    //Update-Button clicked.
    private void updateCourses() {
        changeCourse(course);
    }

    //Used to display "Show-All" section as selected in sectionCombo.
    private void selectFirstSection() {
        view.selectFirstSection();
    }

    //Method to "open" the Settings-page.
    private void onSettings() {
        context.getEventBus().post(new ShowSettingsCommand(this::checkSettings));
    }

    //Method after settingsPresenter is closed, processing changes.
    private void checkSettings(MoodleSyncConfiguration settingsConfig) {
        if(!config.equals(settingsConfig)) {
            //Cant be moved to another class.
            config.setSyncRootPath(settingsConfig.getSyncRootPath());
            config.setMoodleToken(settingsConfig.getMoodleToken());
            config.setMoodleUrl(settingsConfig.getMoodleUrl());
            config.setFormatsMoodle(settingsConfig.getFormatsMoodle());
            config.setFormatsFileserver(settingsConfig.getFormatsFileserver());
            config.setFileserver(settingsConfig.getFileserver());
            config.setUserFileserver(settingsConfig.getUserFileserver());
            config.setPasswordFileserver(settingsConfig.getPasswordFileserver());
            config.setPortFileserver(settingsConfig.getPortFileserver());
            config.setShowUnknownFormats(settingsConfig.getShowUnknownFormats());
            config.setLocale(settingsConfig.getLocale());

            int oldCount = courseCount;
            List<Course> newCourses = courses();
            if(oldCount != courseCount) {
                config.setRecentCourse(null);
                config.setRecentSection(null);
                view.setCourses(newCourses);
                clearView();
            } else {
                changeCourse(course);
            }

        }
    }

    //Used to open wiki.
    private void onWiki() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/MoodleSync/sync-app/wiki/Getting-started-with-MoodleSync"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Used to remove content in tables.
    private void clearView() {
        if(isNull(guest)) {
            view.setDataGuest(FXCollections.observableArrayList());
        } else if (guest) {
            view.setDataGuest(FXCollections.observableArrayList());
        } else {
            view.setDataTrainer(FXCollections.observableArrayList());
        }
    }

    //Method used to open the course-directory.
    private void openCourseDirectory() {
        Desktop desktop = Desktop.getDesktop();
        try {
            File dirToOpen =  Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname()).toFile();
            desktop.open(dirToOpen);
        } catch (Throwable e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.path.unknown.message");
        }
    }

    //Method used when a course is changed or when it should be refreshed.
    private void changeCourse(Course newCourse) {
        try {
            if(isNull(newCourse)) return;
            else if (course != newCourse) {
                config.setRecentCourse(newCourse);
                course = newCourse;
                config.setRecentSection(null);
            }
            section = config.getRecentSection();
            //Check if Trainer or Student
            if (moodleService.getPermissions(config.getMoodleToken(), course.getId())) {
                guest = false;
            } else {
                guest = true;
            }

            courseContent = sections();

            courseContent.add(0, new Section(-2, this.context.getDictionary().get("start.sync.showall"), 1, "all", -1, -1, -1, true, null));
            //Add to Sectioncombo
            List<Section> courseSections = courseContent;
            if((courseSections.size() != sectionCount)) {
                view.setSections(courseSections);
                sectionCount = courseSections.size();
                selectFirstSection();
            }
            if (isNull(section)) {
                section = courseContent.get(0);
                config.setRecentSection(section);
                selectFirstSection();
            }
            if (section.getId() != -2) {
                courseContent = section();
            }
            //next Step: setData according to Permissions
            if (guest) {
                view.setDataGuest(setGuestData());
            } else {
                view.setDataTrainer(setTrainerData());
            }
            //Update Bottom line
            view.setProgress(0.0);
            updateBottomLine();
            //view.setCourse(config.recentCourseProperty());
        } catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
        }
    }

    //Method used when a section is changed or when it should be refreshed.
    private void changeSection(Section newSection) {
        try {
            if (isNull(section)) {
                return;
            }
            if (!isNull(newSection)) {
                if ((!newSection.getId().equals(section.getId())) && (newSection.getId() != -2)) {
                    section = newSection;
                    config.setRecentSection(newSection);
                    courseContent = section();
                } else if (newSection.getId() == -2) {
                    if(newSection != section) {
                        section = newSection;
                        config.setRecentSection(newSection);
                        courseContent = sections();
                    } else {
                        return;
                    }
                } else {
                    return;
                }

                if (guest) {
                    view.setDataGuest(setGuestData());
                } else {
                    view.setDataTrainer(setTrainerData());
                }
                //Update Bottom line
                view.setProgress(0.0);
                updateBottomLine();
            }
        } catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
        }
    }


    //Sets table-data corresponding to the users permissions.
    private ObservableList<SyncTableElement> setGuestData() {
        token = config.getMoodleToken();
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

        List<Path> sectionList = List.of();

        ObservableList<SyncTableElement> data = FXCollections.observableArrayList();

        try {
            if (isNull(section) || section.getId() == -2) {
                //Check if course-folder exists, otherwise create one.
                Path courseDirectory = Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname());
                FileService.directoryManager(courseDirectory);
                //Initialize sectionList with folders inside course-directory.
                sectionList = FileService.getPathsInDirectory(courseDirectory);
            }


            for (Section section : courseContent) {
                if (section.getId() != -2) {
                    data.add(new SyncTableElement(section.getName(), section.getId(), section.getSection(), section.getId(), data.size(), section.getSummary(), "", false, false, MoodleAction.ExistingSection, section.getVisible() == 1, true));

                    Path execute =
                            Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname() + "/" + section.getSection() +
                                    "_" + section.getName());
                    //Create Section-Folder if not exists
                    FileService.directoryManager(execute);
                    List<List<Path>> localContent =
                            FileService.sortDirectoryFilesAllFormats(FileService.getPathsInDirectory(execute),
                                    config.getFormatsMoodle(), config.getFormatsFileserver());
                    for (Module module : section.getModules()) {
                        if (!isNull(module.getContents()) && Objects.equals(module.getModname(), "resource")) {

                            ReturnValue elem = FileService.findResourceInFilesGuest(localContent.get(0), module,
                                    section.getSection(), section.getId(), data.size());
                            localContent.set(0, elem.getFileList());
                            data.add(elem.getElement());
                            if(elem.getElement().getDownloadable()) {
                                elem.getElement().setSectionName(section.getName());
                            }
                        } else {
                            data.add(new SyncTableElement(module.getName(), module.getId(), section.getSection(), section.getId(), data.size(), module.getModname(), "", false, false, MoodleAction.NotLocalFile, module.getUservisible(), module.getUservisible()));
                        }
                    }
                }
            }

            courseData = data;

            watcher = new FileWatcher(Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname()).toFile());
            watcher.addListener(this).watch();

        } catch (Exception e){
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
        }
        return data;
    }

    //Sets table-data corresponding to the users permissions.
    private ObservableList<SyncTableElement> setTrainerData() {
        token = config.getMoodleToken();
        //section = config.getRecentSection();
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
            } //Handling if a specific section is chosen.

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
                            section.getSummary(), "",false, false, MoodleAction.ExistingSection,
                            section.getVisible() == 1, true));

                    //Create or sort section directory
                    Path execute =
                            Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname() + "/" + section.getSection() +
                                    "_" + sectionName);
                    sectionList = FileService.formatSectionFolder(sectionList, section); //Formats section
                    // folder-list -> if Section 3 in Moodle names "Test", inside the course directory, the
                    // sections-directory should be called 3_Test.
                    FileService.directoryManager(execute); //Create section directory if no directory with the
                    // sections name exists.

                    //Initialize the fileServerRequired variable, used to check if the fileserver is required.
                    fileServerRequired = false;
                    List<FileServerFile> files = List.of();

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
                                ReturnValue elem = FileService.findResourceInFiles(localContent.get(0), module,
                                        sectionNum, sectionId, data.size());
                                if(elem.getElement().getDownloadable()) {
                                    elem.getElement().setSectionName(sectionName);
                                }
                                localContent.set(0, elem.getFileList());
                                data.add(elem.getElement());
                            }
                            case "url" ->
                                // TODO: FileServerSupport not functional. -> Check if file in Link is newer than the
                                // link-module.
                                    data.add(new SyncTableElement(module.getName(), module.getId(), sectionNum,
                                            sectionId, data.size(), module.getModname(), "",false, false,
                                            MoodleAction.NotLocalFile, module.getVisible() == 1, module.getUservisible()));
                            case "folder" -> {
                                //Check if folder is existent in section-directory.
                                int pos = FileService.findModuleInList(localContent.get(2), module);
                                if (pos >= 0) {
                                    //If it exists, check if it should be updated.
                                    watcher =
                                            new FileWatcher(Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname() + "/" + section.getSection() + "_" + section.getName() + "/" + localContent.get(2).get(pos).getFileName()).toFile());
                                    watcher.addListener(this).watch();
                                    data.add(FileService.checkDirectoryForUpdates(localContent.get(2).get(pos),
                                            module, sectionNum, sectionId, data.size(), config.getFormatsMoodle()));
                                    localContent.get(2).remove(pos);
                                } else {
                                    data.add(new SyncTableElement(module.getName(), module.getId(), sectionNum,
                                            sectionId, data.size(), module.getModname(), "",false, false,
                                            MoodleAction.NotLocalFile, module.getVisible() == 1, module.getUservisible()));
                                }
                            }
                            default ->
                                    data.add(new SyncTableElement(module.getName(), module.getId(), sectionNum,
                                            sectionId, data.size(), module.getModname(), "",false, false,
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
                            watcher =
                                    new FileWatcher(Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname() + "/" + section.getSection() + "_" + section.getName() + "/" + directory.getFileName()).toFile());
                            watcher.addListener(this).watch();
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
        watcher = new FileWatcher(Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname()).toFile());
        watcher.addListener(this).watch();

        return data;
    }

    //Method used to download a single file. Will be saved in section-folder.
    private void onDownloadFile(SyncTableElement file) {
        try {
            FileDownloadService.getFile(file.getFileUrl(), token,
                    config.getSyncRootPath() + "/" + course.getDisplayname() + "/" + file.getSection() + "_"+ file.getSectionName(),
                    file.getExistingFileName(), file.getExistingFile());
        } catch (Exception e) {
            logException(e, "Sync failed");
        }
        changeCourse(course);
    }

    //Method used to initiate the download of a complete course. Will create a zip-archive of the courses folder
    // afterward.
    private void onDownloadCourse() {
        CompletableFuture
                .supplyAsync(() -> {downloadCourse();
                    return null;} )
                .thenRun(() -> {zipDirectory(Path.of(config.getSyncRootPath() + "/" + course.getDisplayname()));})
                .thenRun(() -> {popUpDownload();})
                .thenRun(() -> {changeCourse(course);})
                .exceptionally(e -> {
                    logException(e, "Download course " + "failed");
                    return null;
                });
    }

    //Downloads each file of a course.
    private void downloadCourse() {
        try {
            try {
                watcher.close();
            } catch (Exception e) {
                logException(e, "Sync failed");
            }
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
        } catch (Exception e) {
            logException(e, "Sync failed");
        }
    }




    //Starts the sync-process.
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

    //Method used to create a zip-archive.
    private void zipDirectory (Path source) {
        try {
            List<Path> files = new ArrayList<>();
            files.add(source);
            ZipUtil.zip(files, config.getSyncRootPath() + "/" +course.getDisplayname() +".zip");
        }
        catch (Exception e) {
            logException(e, "Sync failed");
        }
    }

    @Override
    public void onCreated(FileEvent event) {
            changeCourse(course);
    }

    @Override
    public void onModified(FileEvent event) {
        changeCourse(course);
    }

    @Override
    public void onDeleted(FileEvent event) {
        changeCourse(course);
    }

}

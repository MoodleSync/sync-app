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
import moodle.sync.javafx.model.TimeDateElement;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
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

        //Display the course-sections after Moodle-course is choosen.
        config.recentCourseProperty().addListener((observable, oldCourse, newCourse) -> {
            course = config.getRecentCourse(); //Todo hier schauen ob das ausreicht!
            config.setRecentSection(null);
            view.setData(setData());
        });

        config.recentSectionProperty().addListener((observable, oldSection, newSection) -> {
            section = config.getRecentSection();
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
        view.setSections(sections());
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
        //Todo überprüfen neu
        if (!VerifyDataService.validateString(url) || !VerifyDataService.validateString(token)) {
            return new ArrayList<>();
        }
        List<Course> courses = List.of();
        try {
            courses = moodleService.getEnrolledCourses(token, moodleService.getUserId(token));
            if(config.recentCourseProperty() != null){
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
            courses.removeIf(item -> (item.getEnddate() != 0 && (item.getEnddate() < System.currentTimeMillis()
            /1000)));
        }
        //Sort Courses if Possible
        /*if(courses.get(0).getShortname().contains("SoSe") || courses.get(0).getShortname().contains("WiSe")){
            if(courses.get(0).getShortname().contains("20")){

            }
        }*/
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

        List<Path> sectionList = List.of();

        try {
            //If no section is selected, or "all" are selected, directories are checked and coursecontent is set.
            if (isNull(section) || section.getId() == -2) {
                //Check if course-folder exists, otherwise create one.
                Path courseDirectory =
                        Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname());
                FileService.directoryManager(courseDirectory);
                sectionList = FileService.getPathsInDirectory(courseDirectory);
                courseContent = sections();
            } //Handling if a specific section is chosen.
            else{
                courseContent.clear();
                courseContent.add(moodleService.getCourseContentSection(token, course.getId(),
                        section.getId()).get(0));
            }
        } catch (Exception e) {
            logException(e, "Sync failed");
            showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
        }

        ObservableList<syncTableElement> data = FXCollections.observableArrayList();

        for (Section section : courseContent) {
            try {
                if (section.getId() != -2) {
                    String sectionName = section.getName();
                    int sectionNum = section.getSection();
                    int sectionId = section.getId();

                    data.add(new syncTableElement(sectionName, sectionId, sectionNum, sectionId, data.size(),
                            section.getSummary(), false, false, MoodleAction.ExistingSection,
                            section.getVisible() == 1));

                    sectionList = FileService.formatSectionFolder(sectionList, section);

                    //Section-directory is eventually created.
                    Path execute =
                            Paths.get(config.getSyncRootPath() + "/" + course.getDisplayname() + "/" + section.getSection() + "_" + sectionName);
                    FileService.directoryManager(execute);

                    //Initialize the fileServerRequired variable.
                    fileServerRequired = false;
                    List<FileServerFile> files = List.of();

                    //Watcher is added to choosen course-/ or section-directory.
                    watcher = new FileWatcher(new File(execute.toString()));
                    watcher.addListener(this);
                    watcher.watch();

                    //Categorize Moodle-, FileServer- and local-files.
                    try {
                        List<Path> fileList = FileService.getPathsInDirectory(execute);
                        for (Module module : section.getModules()) {
                            if (module.getModname().equals("resource")) {
                                ReturnValue resource = FileService.findModuleInFiles(fileList, module, sectionNum,
                                        sectionId, data.size());
                                data.add(resource.getElement());
                                fileList = resource.getFileList();
                            }
                            else if (module.getModname().equals("url") && !config.getFormatsFileserver().isEmpty()) {
                                boolean found = false;
                                for (Path file : fileList) {
                                    if (module.getName().equals(file.getFileName().toString())) {
                                        found = true;
                                        //File is found in the moodle-course
                                        //Initialize the fileServer files.
                                        if (!fileServerRequired) {
                                            files = provideFileserverFiles(/*config.getRecentSection().getName()
                                            */ ""); //ToDo -> If there should be support for different upload-sections.
                                        }
                                        for (FileServerFile fileServerFile : files) {
                                            if (fileServerFile.getFilename().equals(file.getFileName().toString())) {
                                                //File additionally uploaded to fileserver
                                                if (fileServerFile.getLastTimeModified() < Files.getLastModifiedTime(file).toMillis()) {
                                                    //File not up-to-date at fileserver
                                                    if (module.getAvailability() != null) {
                                                        var JsonB = new JsonConfigProvider().getContext(null);
                                                        JsonB.fromJson(module.getAvailability(),
                                                                ModuleAvailability.class);
                                                        LocalDateTime time =
                                                                LocalDateTime.ofInstant(Instant.ofEpochMilli(JsonB.fromJson(module.getAvailability().
                                                                        replaceAll("\\\\", ""), ModuleAvailability.class).getTimeDateCondition().getT() * 1000L),
                                                                        ZoneId.systemDefault());
                                                        data.add(new syncTableElement(module.getName(),
                                                                module.getId(), sectionNum, sectionId, data.size(),
                                                                module.getModname(), file, true, false,
                                                                MoodleAction.FTPSynchronize,
                                                                getPriorityVisibility(module.getVisible() == 1,
                                                                        JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""),
                                                                                ModuleAvailability.class).getConditionVisibility()),
                                                                new TimeDateElement(time.toLocalDate(), time.toLocalTime()), module.getId()));
                                                    } else {
                                                        data.add(new syncTableElement(module.getName(),
                                                                module.getId(), sectionNum, sectionId, data.size(),
                                                                module.getModname(), file, true, false,
                                                                MoodleAction.FTPSynchronize, module.getVisible() == 1));
                                                    }
                                                    fileList.remove(file);
                                                    break;
                                                } else {
                                                    //File up to date at the fileserver
                                                    if (module.getAvailability() != null) {
                                                        var JsonB = new JsonConfigProvider().getContext(null);
                                                        JsonB.fromJson(module.getAvailability(),
                                                                ModuleAvailability.class);
                                                        LocalDateTime time =
                                                                LocalDateTime.ofInstant(Instant.ofEpochMilli(JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""),
                                                                        ModuleAvailability.class).getTimeDateCondition().getT() * 1000L), ZoneId.systemDefault());
                                                        data.add(new syncTableElement(module.getName(),
                                                                module.getId(), sectionNum, sectionId, data.size(),
                                                                module.getModname(), file, false, false,
                                                                MoodleAction.ExistingFile,
                                                                getPriorityVisibility(module.getVisible() == 1,
                                                                        JsonB.fromJson(module.getAvailability().replaceAll("\\\\", ""), ModuleAvailability.class)
                                                                                .getConditionVisibility()), new TimeDateElement(time.toLocalDate(), time.toLocalTime())));
                                                    } else {
                                                        data.add(new syncTableElement(module.getName(),
                                                                module.getId(), sectionNum, sectionId, data.size(),
                                                                module.getModname(), file, false, false,
                                                                MoodleAction.ExistingFile, module.getVisible() == 1));
                                                    }
                                                    fileList.remove(file);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                if (!found) {
                                    data.add(new syncTableElement(module.getName(), module.getId(), sectionNum,
                                            sectionId, data.size(), module.getModname(), false, false,
                                            MoodleAction.NotLocalFile, module.getVisible() == 1));
                                }
                            } else {
                                //Other modules which are not "url" or "resource".
                                data.add(new syncTableElement(module.getName(), module.getId(), sectionNum, sectionId
                                        , data.size(), module.getModname(), false, false, MoodleAction.NotLocalFile,
                                        module.getVisible() == 1));
                            }
                        }

                        if (fileList.size() != 0) {
                            //All files inside here should be uploaded, if DataType is known.
                            for (Path path : fileList) {
                                if (contains(config.getFormatsMoodle().split(","),
                                        FilenameUtils.getExtension(path.getFileName().toString()))) {
                                    data.add(new syncTableElement(path.getFileName().toString(), -1, sectionNum,
                                            sectionId, data.size(), "resource", path, true, false,
                                            MoodleAction.MoodleUpload, true));
                                }
                                //More Complicated: all Files for the Fileserver-Upload (if new upload oder update)
                                // are found here:
                                else if ((contains(config.getFormatsFileserver().split(","),
                                        FilenameUtils.getExtension(path.getFileName().toString()))) && !config.getFormatsFileserver().isBlank()) {
                                    //Local files which are not uploaded to moodle.
                                    if (!fileServerRequired) {
                                        files = provideFileserverFiles(/*config.getRecentSection().getName()*/ "");
                                        //ToDo -> If there should be support for different upload-sections.
                                    }
                                    //Array files containing all files uploaded to the fileserver (name and date).
                                    for (FileServerFile fileServerFile : files) {
                                        if (fileServerFile.getFilename().equals(path.getFileName())) {
                                            if (fileServerFile.getLastTimeModified() < Files.getLastModifiedTime(path).toMillis()) {
                                                //File to up-to-date
                                                data.add(new syncTableElement(path.getFileName().toString(), -1,
                                                        sectionNum, sectionId, data.size(), "url", path, true, false,
                                                        MoodleAction.FTPUpload, true));
                                            } else {
                                                data.add(new syncTableElement(path.getFileName().toString(), -1,
                                                        sectionNum, sectionId, data.size(), "url", path, true, false,
                                                        MoodleAction.FTPLink, true));
                                            }
                                        }
                                    }
                                    //File not on moodle nor on fileserver
                                    data.add(new syncTableElement(path.getFileName().toString(), -1, sectionNum,
                                            sectionId, data.size(), "url", path, true, false, MoodleAction.FTPUpload,
                                            true));

                                }
                                else {
                                    data.add(new syncTableElement(path.getFileName().toString(), -1, sectionNum,
                                            sectionId, data.size(), "resource", path, false, false,
                                            MoodleAction.DatatypeNotKnown, false));
                                }
                            }
                        }
                    }
                    catch (Throwable e) {
                        logException(e, "Sync failed");
                        showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
                    }
                }
            }
            catch (Exception e) {
                logException(e, "Sync failed");
                showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
            }
        }

        if (!sectionList.isEmpty()) {
            for (Path elem : sectionList) {
                data.add(new syncTableElement(elem.getFileName().toString(), -1, -1, -1, data.size(), "section", elem
                        , true, false, MoodleAction.UploadSection, true));
            }
        }

        courseData = data;

        //Add FileWatcher
        watcher = new FileWatcher(new File(config.getSyncRootPath() + "/" + course.getDisplayname()));
        watcher.addListener(this);
        watcher.watch();

        return data;

    }

    /*private void deleteModule(syncTableElement element){
        if(element.getAction() == MoodleAction.ExistingFile || element.getAction() == MoodleAction.NotLocalFile ||
        element.getAction() == MoodleAction.MoodleSynchronize || element.getAction() == MoodleAction.FTPSynchronize){
            if(element.getDelete()){
                //Hier nochmal Nutzerdialog
                context.getEventBus().post(new ShowPresenterCommand<>(ConfirmDeleteModulePresenter.class));
            }
        }
    }*/

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

    private Boolean getPriorityVisibility(Boolean visible, Boolean availability) {
        if (!visible || !availability) {
            return false;
        }
        return true;
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

    public static boolean contains(final String[] arr, final String key) {
        return Arrays.asList(arr).contains(key);
    }

    private List<FileServerFile> provideFileserverFiles(String pathname) throws Exception {
        List<FileServerFile> files = List.of();
        if (!VerifyDataService.validateString(config.getFileserver()) || !VerifyDataService.validateString(config.getUserFileserver()) ||
                !VerifyDataService.validateString(config.getPasswordFileserver())) {
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
                showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.fileserver2.message");
            }
        }
        fileServerRequired = true;

        return files;
    }
}

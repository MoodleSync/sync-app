package moodle.sync.presenter;

import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.beans.BooleanProperty;
import moodle.sync.core.beans.ObjectProperty;
import moodle.sync.core.model.json.*;
import moodle.sync.core.presenter.Presenter;
import moodle.sync.core.presenter.command.ShowPresenterCommand;
import moodle.sync.core.view.NotificationType;
import moodle.sync.core.view.ProgressView;
import moodle.sync.core.view.ViewContextFactory;
import moodle.sync.core.web.model.TokenProvider;
import moodle.sync.core.web.panopto.PanoptoService;
import moodle.sync.event.AddFileListenerEvent;
import moodle.sync.view.*;
import com.google.common.eventbus.Subscribe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import moodle.sync.core.config.DefaultConfiguration;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.fileserver.FileServerClientFTP;
import moodle.sync.core.util.FileDownloadService;
import moodle.sync.core.util.FileWatcherService.FileEvent;
import moodle.sync.core.util.FileWatcherService.FileListener;
import moodle.sync.core.util.FileWatcherService.FileWatcher;
import moodle.sync.core.web.service.MoodleService;
import moodle.sync.event.DownloadItemEvent;
import moodle.sync.javafx.model.SyncTableElement;
import moodle.sync.presenter.command.ShowSettingsCommand;
import moodle.sync.util.*;

import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
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
public class SyncPresenter extends Presenter<SyncStartView> implements FileListener {

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

    private PanoptoService panoptoService;

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

    private ObjectProperty<PanoptoCourse> panoptoCourse = new ObjectProperty<>();

    private List<PanoptoCourse> panoptoCourses;

    private boolean courseChanged = true;

    private StudentTablePresenter studentTablePresenter;

    private TrainerTablePresenter trainerTablePresenter;


    @Inject
    SyncPresenter(ApplicationContext context, SyncStartView view, ViewContextFactory viewFactory,
                  MoodleService moodleService) {
        super(context, view);
        this.viewFactory = viewFactory;
        this.moodleService = moodleService;
        this.config = (MoodleSyncConfiguration) context.getConfiguration();
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
        config.recentSectionProperty().set(null);
        view.setSection(config.recentSectionProperty());
        view.setOnCourseChanged(this::changeCourse);
        view.setOnSectionChanged(this::changeSection);
        view.setProgress(0.0);
        view.setOnFolder(this::openCourseDirectory);
        view.setOnPanoptoChanged(this::panoptoCourseChanged);

        if(config.fileServerTypeProperty().get().equals("Panopto") && !config.getPanoptoConfiguration().getPanoptoFormats().isEmpty()) {
            view.setPanoptoFileserver();
            panoptoService = new PanoptoService(config.getPanoptoConfiguration().getPanoptoServer(),
                    new TokenProvider(config.getPanoptoConfiguration().getPanoptoClientId()
                    , config.getPanoptoConfiguration().getPanoptoSecret()));
        } else {
            view.removePanoptoFileserver();
        }



        //Init course
        CompletableFuture
                .runAsync(() -> {changeCourse(config.getRecentCourse());} )
                .exceptionally(e -> {
                    logException(e, "Initialize course " + "failed");
                    return null;
                });

    }

    public void getPanoptoCourses() {
        List panCourses = new ArrayList();
        String panoptoDefaultFolder = config.getPanoptoConfiguration().getPanoptoDefaultFolder();
        try {
            panCourses = panoptoService.getSearchFolder(config.getRecentCourse().getDisplayname()).getResults();
            if(!isNull(panoptoDefaultFolder) && !panoptoDefaultFolder.isBlank()) {
                panCourses.add(new PanoptoCourse("", "", new PanoptoUrls("", "", ""), panoptoDefaultFolder, "My Folder"));
            }
        } catch (Exception e) {
            if(!isNull(panoptoDefaultFolder)) {
                panCourses.add(new PanoptoCourse("", "", new PanoptoUrls("", "", ""), panoptoDefaultFolder, "My Folder"));
            }
        }
        panoptoCourses = panCourses;
    }

    //Show selected section if a module is clicked.
    @Subscribe
    public void onElementClicked(SyncTableElement selectedSection) {
        view.setSectionId(selectedSection.getSection().toString());
    }

    //A specific file or folder should be downloaded.
    @Subscribe
    public void onDownloadItem(DownloadItemEvent event) {
        if(event.getElement().getModuleType().equals("resource")) {
            onDownloadFile(event.getElement());
        }
        else {
            onDownloadFolder(event.getElement());
        }
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
            context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.invalidurl" +
                    ".message");
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
            context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.invalidurl" +
                    ".message");
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
            context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error" +
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
        if(isNull(course)) {
            view.setCourses(courses());
        } else {
            changeCourse(course);
        }
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

            config.setFtpConfiguration(settingsConfig.getFtpConfiguration());
            config.setPanoptoConfiguration(settingsConfig.getPanoptoConfiguration());

            config.setRecentFileServerType(settingsConfig.getFileServerType());
            config.setShowUnknownFormats(settingsConfig.getShowUnknownFormats());
            config.setLocale(settingsConfig.getLocale());

            if(config.fileServerTypeProperty().get().equals("Panopto") && !config.getPanoptoConfiguration().getPanoptoServer().isEmpty()) {
                view.setPanoptoFileserver();
                panoptoService = new PanoptoService(config.getPanoptoConfiguration().getPanoptoServer(),
                        new TokenProvider(config.getPanoptoConfiguration().getPanoptoClientId(),
                                config.getPanoptoConfiguration().getPanoptoSecret()));
                getPanoptoCourses();
                view.setPanoptoCourses(panoptoCourses);
                config.getPanoptoConfiguration().panoptoCourseProperty().set(panoptoCourses.get(0));
                view.setPanoptoCourse(config.getPanoptoConfiguration().panoptoCourseProperty());
            } else {
                view.removePanoptoFileserver();
            }

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
            File dirToOpen =  Paths.get(config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname())).toFile();
            desktop.open(dirToOpen);
        } catch (Throwable e) {
            logException(e, "Sync failed");
            context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.path.unknown.message");
        }
    }

    //private void onChangeCourse(Course newCourse, ProgressView progressView) {
    private void changeCourse(Course newCourse) {
        //CompletableFuture.runAsync(() -> {
                    //progressView.setProgress(-1.0);
                    try {
                        if (isNull(newCourse))
                            return;
                        else if (course != newCourse) {
                            courseChanged = true;
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
                        //if ((courseSections.size() != sectionCount)) {
                            view.setSections(courseSections);
                            sectionCount = courseSections.size();
                            selectFirstSection();
                        //}
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
                            try {
                                view.clearTable();
                                if (isNull(studentTablePresenter)) {
                                    System.out.println("is Null");
                                    studentTablePresenter = getStudentTablePresenter();
                                }
                                view.setStudent(studentTablePresenter.getView());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println("----------------------------- In studentTableView " +
                                    "------------------------");
                            //studentTablePresenter.setStudentData(setGuestData());
                            setGuestData();
                        } else {
                            try {
                                view.clearTable();
                                if (isNull(trainerTablePresenter)) {
                                    System.out.println("is Null");
                                    trainerTablePresenter = getTrainerTablePresenter();
                                }
                                view.setTrainer(trainerTablePresenter.getView());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println("----------------------------- In trainerTableView " +
                                    "------------------------");
                            //trainerTablePresenter.setTrainerData(setTrainerData());
                            setTrainerData();
                        }
                        //Update Bottom line
                        view.setProgress(0.0);
                        updateBottomLine();
                        //view.setCourse(config.recentCourseProperty());
                    } catch (Exception e) {
                        logException(e, "Sync failed");
                        context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
                    }
    }

    private StudentTablePresenter getStudentTablePresenter() {
        StudentTablePresenter studentTablePresenter = null;
        try {
            studentTablePresenter =
                    viewFactory.getInstance(StudentTablePresenter.class);
            studentTablePresenter.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentTablePresenter;
    }

    private TrainerTablePresenter getTrainerTablePresenter() {
        TrainerTablePresenter trainerTablePresenter = null;
        try {
            trainerTablePresenter =
                    viewFactory.getInstance(TrainerTablePresenter.class);
            trainerTablePresenter.initialize();
            //trainerView = trainerTablePresenter.getView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trainerTablePresenter;
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
                    if(newSection != section && section.getId() != -2) {
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
                    setGuestData();
                } else {
                    setTrainerData();
                }
                //Update Bottom line
                view.setProgress(0.0);
                updateBottomLine();
            }
        } catch (Exception e) {
            logException(e, "Sync failed");
            context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
        }
    }

    private void setGuestData() {
        view.removePanoptoFileserver();
        token = config.getMoodleToken();
        if (isNull(course))
            //return FXCollections.observableArrayList();
            return;
        try {
            //Close all existing watchers.
            if (watcher != null) {
                watcher.close();
            }

            courseData = studentTablePresenter.setGuestData(courseContent, course, section);

            watcher = new FileWatcher(Paths.get(config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname())).toFile());
            watcher.addListener(this).watch();

        } catch (Exception e){
            logException(e, "Sync failed");
            context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
        }
    }

    private void setTrainerData() {
        if(config.fileServerTypeProperty().get().equals("Panopto")) {
            view.setPanoptoFileserver();
            if(isNull(config.getPanoptoConfiguration().panoptoCourseProperty()) || courseChanged ) {
                getPanoptoCourses();
                view.setPanoptoCourses(panoptoCourses);
                config.getPanoptoConfiguration().panoptoCourseProperty().set(panoptoCourses.get(0));
                courseChanged = false;
            }
            panoptoCourse.set(config.getPanoptoConfiguration().panoptoCourseProperty().get());
            view.setPanoptoCourse(config.getPanoptoConfiguration().panoptoCourseProperty());
        }
        token = config.getMoodleToken();
        if (isNull(course))
            return;
        try {
            //Close all existing watchers.
            if (watcher != null) {
                watcher.close();
            }

            courseData = trainerTablePresenter.setTrainerData(courseContent,course,section,panoptoService);

            //Add FileWatcher in course-directory to detect added sections.
            watcher = new FileWatcher(Paths.get(config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname())).toFile());
            watcher.addListener(this).watch();

        } catch (Exception e){
            logException(e, "Sync failed");
            context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
        }
    }

    //Method used to download a single file. Will be saved in section-folder.
    private void onDownloadFile(SyncTableElement file) {
        try {
            FileDownloadService.getFile(file.getContentsOnline().get(0).getFileurl(), token,
                    config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname()) + "/" + file.getSection() + "_"+ file.getSectionName(),
                    file.getExistingFileName(), file.getExistingFile());
        } catch (Exception e) {
            logException(e, "Sync failed");
        }
        changeCourse(course);
    }

    //Method used to download a single folder. Will be saved in section-folder.
    private void onDownloadFolder(SyncTableElement folder) {
        try {
            //First create folder
            String path =
                    config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname()) + "/" + folder.getSection() + "_"+ folder.getSectionName() + "/" + folder.getModuleName();
            FileService.directoryManager(Path.of(path));
            for(Content content : folder.getContentsOnline()) {
                FileDownloadService.getFile(content.getFileurl(), token,
                        path , content.getFilename(), String.valueOf(content.getTimemodified()));
            }
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
                .thenRun(() -> {zipDirectory(Path.of(config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname())));})
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
                    if(courseData.getModuleType().equals("resource")) {
                        FileDownloadService.getFile(courseData.getContentsOnline().get(0).getFileurl(), token,
                                config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname()) + "/" + courseData.getSection() + "_" + courseData.getSectionName(), courseData.getExistingFileName(), courseData.getExistingFile());
                    } else if (courseData.getModuleType().equals("folder")) {
                        //First create folder
                        String path =
                                config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname()) + "/" + courseData.getSection() + "_"+ courseData.getSectionName() + "/" + courseData.getModuleName();
                        FileService.directoryManager(Path.of(path));
                        for(Content content : courseData.getContentsOnline()) {
                            FileDownloadService.getFile(content.getFileurl(), token,
                                    path , content.getFilename(), String.valueOf(content.getTimemodified()));
                        }
                    }
                    counter++;
                    view.setProgress(counter / count);
                }
            }
        } catch (Exception e) {
            logException(e, "Sync failed");
        }
    }


    private void onSync() {
        context.getEventBus().post(new ShowPresenterCommand<>(ProgressPresenter.class) {
            @Override
            public void execute(ProgressPresenter presenter) {
                ProgressView progressView = presenter.getView();
                //progressView.setTitle(context.getDictionary().get("save.document.title"));
                progressView.setTitle("Sync");
                progressView.setMessage(context.getDictionary().get("start.sync.progress"));
                progressView.setOnHideClose(new BooleanProperty(false));
                progressView.setOnViewShown(() -> {
                    executeSync(progressView);
                });
            }
        });
    }

    //Starts the sync-process.
    private void executeSync(ProgressView progressView) {
        //Several security checks to prevent unwanted behaviour.
        if (config.getRecentCourse() == null) {
            context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.course.message");
            progressView.setProgress(1.0);
            progressView.setError(context.getDictionary().get("start.sync.error.title"));
            progressView.setOnHideClose(new BooleanProperty(true));
            return;
        }
        //Checks whether Root-Directory is existing.
        if (!Files.isDirectory(Paths.get(config.getSyncRootPath()))) {
            context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.path.message");
            progressView.setProgress(1.0);
            progressView.setError(context.getDictionary().get("start.sync.error.title"));
            progressView.setOnHideClose(new BooleanProperty(true));
            return;
        }

        /*ApplicationContext dummyContext = new ApplicationContext(null,
                context.getConfiguration(), context.getDictionary(),
                new EventBus(), new EventBus()) {

            @Override
            public void saveConfiguration() {

            }
        };*/

        CompletableFuture
                .runAsync(() -> {
                    try {
                        SyncService.executeSync(courseData, course, url, token, context, moodleService, panoptoService, progressView);
                    } catch (Exception e) {
                        context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.upload" + ".message");
                        progressView.setProgress(1.0);
                        progressView.setError(context.getDictionary().get("start.sync.error.title"));
                        progressView.setOnHideClose(new BooleanProperty(true));
                    }
                })
                .thenRun(this::updateCourses)
                .exceptionally(throwable -> {
                    logException(throwable, "Sync Failed");
                    progressView.setProgress(1.0);
                    progressView.setError(context.getDictionary().get("start.sync.error.title"));
                    progressView.setOnHideClose(new BooleanProperty(true));
                    return null;
                });
    }

    private void panoptoCourseChanged(PanoptoCourse course) {
        if(isNull(panoptoCourse.get())) {
            panoptoCourse.set(course);
            updateCourses();
            return;
        }
        if(!Objects.equals(course.getId(), panoptoCourse.get().getId()) && !courseChanged) {
            panoptoCourse.set(course);
            updateCourses();
        }
    }

    //Method used to create a zip-archive.
    private void zipDirectory (Path source) {
        try {
            List<Path> files = new ArrayList<>();
            files.add(source);
            ZipUtil.zip(files, config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname()) +
                    ".zip");
        }
        catch (Exception e) {
            logException(e, "Sync failed");
        }
    }


    @Subscribe
    public void onWatcherAdded(AddFileListenerEvent event) {
        event.getElement().addListener(this).watch();
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

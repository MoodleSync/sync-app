package moodle.sync.presenter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.beans.BooleanProperty;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.fileserver.FileServerFile;
import moodle.sync.core.fileserver.panopto.PanoptoException;
import moodle.sync.core.model.json.*;
import moodle.sync.core.model.json.Module;
import moodle.sync.core.presenter.Presenter;
import moodle.sync.core.util.FileWatcherService.FileWatcher;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.core.view.NotificationType;
import moodle.sync.core.view.ViewContextFactory;
import moodle.sync.core.web.panopto.PanoptoService;
import moodle.sync.core.web.service.MoodleService;
import moodle.sync.event.AddFileListenerEvent;
import moodle.sync.javafx.model.ReturnValue;
import moodle.sync.javafx.model.SyncTableElement;
import moodle.sync.util.FileService;
import moodle.sync.view.TrainerTableView;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.Objects.isNull;

public class TrainerTablePresenter extends Presenter<TrainerTableView> {

    private final ViewContextFactory viewFactory;

    //Used MoodleService for executing Web Service API-Calls.
    private final MoodleService moodleService;

    //Configuration providing the settings.
    private final MoodleSyncConfiguration config;

    //Select all possible changes.
    private BooleanProperty selectAll;

    private ObservableList<SyncTableElement> courseData;

    @Inject
    TrainerTablePresenter(ApplicationContext context, TrainerTableView view, ViewContextFactory viewFactory,
                          MoodleService moodleService) {
        super(context, view);
        this.viewFactory = viewFactory;
        this.moodleService = moodleService;
        this.config = (MoodleSyncConfiguration) context.getConfiguration();
        this.selectAll = new BooleanProperty(false);
    }

    @Override
    public void initialize() {
        view.setSelectAll(selectAll);
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

    public ObservableList<SyncTableElement> setTrainerData(List<Section> courseContent, Course course,
                                                           Section actualSection,
                                                           PanoptoService panoptoService) {

        //sectionList: if "all sections" is chosen, all section-directories are stored. -> Needed to detect new
        // sections.
        List<Path> sectionList = List.of();

        ObservableList<SyncTableElement> data = FXCollections.observableArrayList();

        try {
            //If no section is selected, or "all" are selected, directories are checked and coursecontent is set.
            if (isNull(actualSection) || actualSection.getId() == -2) {
                //Check if course-folder exists, otherwise create one.
                Path courseDirectory = Paths.get(config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname()));
                FileService.directoryManager(courseDirectory);
                //Initialize sectionList with folders inside course-directory.
                sectionList = FileService.getPathsInDirectory(courseDirectory);
            } //Handling if a specific section is chosen.

            //Iterate over each section and put content in List "data" -> create SyncTableElements.
            for (Section section : courseContent) {
                //Handle every section on its own
                //Section "select all" should not be considered
                if (section.getId() != -2) {
                    String sectionName = section.getName().trim();
                    int sectionNum = section.getSection();
                    int sectionId = section.getId();

                    //Add section element
                    data.add(new SyncTableElement(sectionName, sectionId, sectionNum, sectionId, data.size(), section.getSummary(), "", false, false, MoodleAction.ExistingSection, section.getVisible() == 1, true));

                    //Create or sort section directory
                    Path execute = Paths.get(config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname()) + "/" + section.getSection() + "_" + sectionName);
                    sectionList = FileService.formatSectionFolder(sectionList, section); //Formats section
                    // folder-list -> if Section 3 in Moodle names "Test", inside the course directory, the
                    // sections-directory should be called 3_Test.
                    FileService.directoryManager(execute); //Create section directory if no directory with the
                    // sections name exists.

                    //Initialize the fileServerRequired variable, used to check if the fileserver is required.
                    boolean fileServerRequired = false;
                    List<FileServerFile> files = List.of();
                    List<PanoptoContent> panoptoContent = List.of();

                    //Sort files inside section-directory by format types: MoodleFormats, FileserverFormats,
                    // Directories and Other.
                    String formatsFileserver;
                    if (config.getFileServerType().equals("FTP")) {
                        formatsFileserver = config.getFtpConfiguration().getFtpFormats();
                    } else if (config.getFileServerType().equals("Panopto")) {
                        formatsFileserver = config.getPanoptoConfiguration().getPanoptoFormats();
                    } else {
                        formatsFileserver = "";
                    }
                    List<List<Path>> localContent = FileService.sortDirectoryFiles(FileService.getPathsInDirectory(execute), config.getFormatsMoodle(), formatsFileserver);
                    //Iterate over section-content on Moodle
                    for (Module module : section.getModules()) {
                        switch (module.getModname()) {
                            case "resource" -> {
                                //If file is local, it must be in localContent[0]
                                //check if user has permission to see full course-content incl. not available files.
                                ReturnValue elem = FileService.findResourceInFiles(localContent.get(0), module, sectionNum, sectionId, data.size());
                                if (elem.getElement().getDownloadable()) {
                                    elem.getElement().setSectionName(sectionName);
                                }
                                localContent.set(0, elem.getFileList());
                                data.add(elem.getElement());
                            }
                            case "url" -> {
                                //differentiate between Types: No, Panopto, FTP
                                if (config.getFileServerType().equals("Panopto")) {
                                    //first we need to find the video on panopto
                                    boolean found = false;
                                    try {
                                        if (!fileServerRequired) {
                                            panoptoContent = panoptoService.getFolderContents(new PanoptoFolder(config.getPanoptoConfiguration().panoptoCourseProperty().get().getId())).getResults();
                                            fileServerRequired = true;
                                        }
                                    } catch (Exception e) {
                                        throw new PanoptoException();
                                    }
                                    for (PanoptoContent content : panoptoContent) {
                                        if (content.getUrls().getViewerUrl().equals(module.getContents().get(0).getFileurl())) {
                                            //Video auf Panopto gefunden -> remove aus localContent
                                            int pos = FileService.findFileInFiles(localContent.get(1), content.getName());
                                            Path existingFile = Path.of("");
                                            if (pos != -1) {
                                                List<Path> fileserverElements = localContent.get(1);
                                                existingFile = fileserverElements.get(pos);
                                                fileserverElements.remove(pos);
                                                localContent.set(1, fileserverElements);
                                            }

                                            data.add(new SyncTableElement(module.getName(), module.getId(), sectionNum, sectionId, data.size(), module.getModname(), existingFile, false, false, MoodleAction.ExistingFile, module.getVisible() == 1, module.getUservisible()));
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        data.add(new SyncTableElement(module.getName(), module.getId(), sectionNum, sectionId, data.size(), module.getModname(), "", false, false, MoodleAction.NotLocalFile, module.getVisible() == 1, module.getUservisible()));
                                    }
                                } else if (config.getFileServerType().equals("FTP")) {
                                    // TODO: FTP not functional. -> Check if file in Link is newer than the link-module.
                                    data.add(new SyncTableElement(module.getName(), module.getId(), sectionNum, sectionId, data.size(), module.getModname(), "", false, false, MoodleAction.NotLocalFile, module.getVisible() == 1, module.getUservisible()));
                                } else {
                                    data.add(new SyncTableElement(module.getName(), module.getId(), sectionNum, sectionId, data.size(), module.getModname(), "", false, false, MoodleAction.NotLocalFile, module.getVisible() == 1, module.getUservisible()));
                                }
                                //If "No" -> just ignore those
                            }
                            case "folder" -> {
                                //Check if folder is existent in section-directory.
                                int pos = FileService.findModuleInList(localContent.get(2), module);
                                if (pos >= 0) {
                                    //If it exists, check if it should be updated.
                                    FileWatcher watcher =
                                            new FileWatcher(Paths.get(config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname()) + "/" + section.getSection() + "_" + section.getName().trim() + "/" + localContent.get(2).get(pos).getFileName()).toFile());
                                    context.getEventBus().post(new AddFileListenerEvent(watcher));
                                    data.add(FileService.checkDirectoryForUpdates(localContent.get(2).get(pos), module, sectionNum, sectionId, data.size(), config.getFormatsMoodle()));
                                    localContent.get(2).remove(pos);
                                } else {
                                    SyncTableElement folder = new SyncTableElement(module.getName(), module.getId(), sectionNum, sectionId, data.size(), module.getModname(), "", false, false, MoodleAction.NotLocalFile, module.getVisible() == 1, module.getUservisible());
                                    if (module.getContents().size() != 0) {
                                        folder.setDownloadable(true);
                                        folder.setSectionName(sectionName);
                                        for (Content content : module.getContents()) {
                                            folder.addContentOnline(content);
                                        }
                                    }
                                    data.add(folder);
                                }
                            }
                            case "label" -> {
                                data.add(new SyncTableElement(module.getName(), module.getId(), sectionNum, sectionId, data.size(), module.getModname(), Jsoup.parse(module.getDescription()).text(), false, false, MoodleAction.NotLocalFile, module.getVisible() == 1, module.getUservisible()));
                            }
                            default ->
                                    data.add(new SyncTableElement(module.getName(), module.getId(), sectionNum, sectionId, data.size(), module.getModname(), "", false, false, MoodleAction.NotLocalFile, module.getVisible() == 1, module.getUservisible()));
                        }
                    }

                    //If localContent[X] is not empty, those elements need to be uploaded.
                    if (!localContent.get(0).isEmpty()) {
                        for (Path file : localContent.get(0)) {
                            data.add(new SyncTableElement(file.getFileName().toString(), -1, sectionNum, sectionId, data.size(), "resource", file, true, false, MoodleAction.MoodleUpload, true, true));
                        }
                    }
                    if (!localContent.get(1).isEmpty()) {
                        for (Path file : localContent.get(1)) {
                            data.add(new SyncTableElement(file.getFileName().toString(), -1, sectionNum, sectionId, data.size(), "url", file, true, false, MoodleAction.FTPUpload, true, true));
                        }
                    }
                    if (!localContent.get(2).isEmpty()) {
                        String formatsMoodle = config.getFormatsMoodle();
                        for (Path directory : localContent.get(2)) {
                            List<Path> content = FileService.getPathsInDirectory(directory);
                            content.removeIf(file -> !formatsMoodle.contains(FilenameUtils.getExtension(String.valueOf(file))));
                            data.add(new SyncTableElement(directory.getFileName().toString(), -1, sectionNum, sectionId, data.size(), "folder", directory, true, false, MoodleAction.FolderUpload, true, -1, null, content, -1, true));
                            FileWatcher watcher =
                                    new FileWatcher(Paths.get(config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname()) + "/" + section.getSection() + "_" + section.getName().trim() + "/" + directory.getFileName()).toFile());
                            context.getEventBus().post(new AddFileListenerEvent(watcher));
                        }
                    }
                    if (!localContent.get(3).isEmpty()) {
                        for (Path file : localContent.get(3)) {
                            if (config.getShowUnknownFormats()) {
                                data.add(new SyncTableElement(file.getFileName().toString(), -1, sectionNum, sectionId, data.size(), "", file, false, false, MoodleAction.DatatypeNotKnown, false, true));
                            }
                        }
                    }

                }
            }
            //If sectionList is not empty, the remaining directories are new sections which could be created.
            if (!sectionList.isEmpty()) {
                for (Path elem : sectionList) {
                    data.add(new SyncTableElement(elem.getFileName().toString(), -1, -1, -1, data.size(), "section", elem, true, false, MoodleAction.UploadSection, true, true));
                }
            }
            view.setTrainerTableData(data);
        } catch (Exception e) {
            logException(e, "Sync failed");
            data = null;
            if (e instanceof PanoptoException) {
                context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.fileserver2.message");
            } else {
                context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
            }
        }

        courseData = data;
        return data;
    }

}

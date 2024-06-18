package moodle.sync.presenter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.model.json.Content;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.Module;
import moodle.sync.core.model.json.Section;
import moodle.sync.core.presenter.Presenter;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.core.view.NotificationType;
import moodle.sync.core.view.ViewContextFactory;
import moodle.sync.core.web.service.MoodleService;
import moodle.sync.javafx.model.ReturnValue;
import moodle.sync.javafx.model.SyncTableElement;
import moodle.sync.util.FileService;
import moodle.sync.view.StudentTableView;
import org.jsoup.Jsoup;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

public class StudentTablePresenter extends Presenter<StudentTableView>  {

    private final ViewContextFactory viewFactory;

    //Used MoodleService for executing Web Service API-Calls.
    private final MoodleService moodleService;

    //Configuration providing the settings.
    private final MoodleSyncConfiguration config;

    @Inject
    StudentTablePresenter(ApplicationContext context, StudentTableView view, ViewContextFactory viewFactory,
                     MoodleService moodleService) {
        super(context, view);
        this.viewFactory = viewFactory;
        this.moodleService = moodleService;
        this.config = (MoodleSyncConfiguration) context.getConfiguration();
    }

    public ObservableList<SyncTableElement> setGuestData(List<Section> courseContent, Course course,
                                                          Section actualSection) {

        List<Path> sectionList = List.of();

        ObservableList<SyncTableElement> data = FXCollections.observableArrayList();

        try {
            if (isNull(actualSection) || actualSection.getId() == -2) {
                //Check if course-folder exists, otherwise create one.
                Path courseDirectory = Paths.get(config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname()));
                FileService.directoryManager(courseDirectory);
                //Initialize sectionList with folders inside course-directory.
                sectionList = FileService.getPathsInDirectory(courseDirectory);
            }

            for (Section section : courseContent) {
                if (section.getId() != -2) {
                    data.add(new SyncTableElement(section.getName().trim(), section.getId(), section.getSection(),
                            section.getId(), data.size(), section.getSummary(), "", false, false, MoodleAction.ExistingSection, section.getVisible() == 1, true));

                    Path execute =
                            Paths.get(config.getSyncRootPath() + "/" + FileService.removeSlash(course.getDisplayname()) + "/" + section.getSection() +
                                    "_" + section.getName().trim());
                    //Create Section-Folder if not exists
                    FileService.directoryManager(execute);
                    String formatsFileserver;
                    if(config.getFileServerType().equals("FTP")) {
                        formatsFileserver = config.getFtpConfiguration().getFtpFormats();
                    } else if(config.getFileServerType().equals("Panopto")) {
                        formatsFileserver = config.getPanoptoConfiguration().getPanoptoFormats();
                    } else {
                        formatsFileserver = "";
                    }
                    List<List<Path>> localContent =
                            FileService.sortDirectoryFilesAllFormats(FileService.getPathsInDirectory(execute),
                                    config.getFormatsMoodle(), formatsFileserver);
                    for (Module module : section.getModules()) {
                        if (!isNull(module.getContents()) && Objects.equals(module.getModname(), "resource")) {

                            ReturnValue elem = FileService.findResourceInFilesGuest(localContent.get(0), module,
                                    section.getSection(), section.getId(), data.size());
                            localContent.set(0, elem.getFileList());
                            data.add(elem.getElement());
                            if(elem.getElement().getDownloadable()) {
                                elem.getElement().setSectionName(section.getName().trim());
                            }
                        }
                        else if (!isNull(module.getContents()) && Objects.equals(module.getModname(), "folder")) {
                            int pos = FileService.findModuleInList(localContent.get(2), module);
                            if (pos == -1) {
                                SyncTableElement folder = new SyncTableElement(module.getName(), module.getId(),
                                        section.getSection(), section.getId(), data.size(), module.getModname(), "", false, false, MoodleAction.NotLocalFile, module.getUservisible(), module.getUservisible());
                                if(module.getContents().size() != 0) {
                                    folder.setDownloadable(true);
                                    folder.setSectionName(section.getName().trim());
                                    for(Content content : module.getContents()) {
                                        folder.addContentOnline(content);
                                    }
                                }
                                data.add(folder);
                            }
                        }
                        else if (Objects.equals(module.getModname(), "label")) {
                            data.add(new SyncTableElement(module.getName(), module.getId(), section.getSection(), section.getId(), data.size(), module.getModname(), Jsoup.parse(module.getDescription()).text(), false, false, MoodleAction.NotLocalFile, module.getUservisible(), module.getUservisible()));

                        }
                        else {
                            data.add(new SyncTableElement(module.getName(), module.getId(), section.getSection(), section.getId(), data.size(), module.getModname(), "", false, false, MoodleAction.NotLocalFile, module.getUservisible(), module.getUservisible()));
                        }
                    }
                }
            }
            view.setStudentTableData(data);
        } catch (Exception e) {
            logException(e, "Sync failed");
            context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.message");
        }
        return data;
    }

}

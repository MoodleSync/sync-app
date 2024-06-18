package moodle.sync.util;

import javafx.collections.ObservableList;
import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.beans.BooleanProperty;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.fileserver.panopto.PanoptoException;
import moodle.sync.core.fileserver.panopto.PanoptoUploader;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.PanoptoFolder;
import moodle.sync.core.model.json.PanoptoFolderContent;
import moodle.sync.core.util.MoodleAction;

import moodle.sync.core.view.NotificationType;
import moodle.sync.core.view.ProgressView;
import moodle.sync.core.web.panopto.PanoptoService;
import moodle.sync.core.web.service.MoodleService;
import moodle.sync.javafx.model.SyncTableElement;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class SyncService {

    public static void executeSync(ObservableList<SyncTableElement> syncData, Course course, String url,
                                   String token, ApplicationContext context,
                                   MoodleService moodleService, PanoptoService panoptoService,
                                   ProgressView progressView) throws Exception{

        MoodleSyncConfiguration config = (MoodleSyncConfiguration) context.getConfiguration();

        //Calls the API-Call functions depending on the "selected" property and the MoodleAction.
                    progressView.setProgress(-1.0);
                    double progressSteps = 0.0;
                    double progress = 0;
                    for(SyncTableElement courseData : syncData) {
                        if (courseData.isSelected()) {
                            progressSteps++;
                        }
                    }

                    for (SyncTableElement courseData : syncData) {

                            if (courseData.isSelected()) {
                                if (courseData.getModuleType().equals("resource")) {
                                    progressView.setMessage(context.getDictionary().get("start.sync.progress"));
                                    SetModuleService.publishResource(moodleService, courseData, course, url, token);
                                } else if (courseData.getAction() == MoodleAction.FTPUpload) {
                                    if(config.getFileServerType().equals("Panopto")) {
                                        progressView.setMessage(context.getDictionary().get("start.sync.upload") + courseData.getExistingFileName());
                                        try {
                                            String sessionId = PanoptoUploader.uploadVideo(panoptoService,
                                                    config.getPanoptoConfiguration().getPanoptoServer()
                                                    , config.getPanoptoConfiguration().panoptoCourseProperty().get().getId(),
                                                    Path.of(courseData.getExistingFile()),
                                                    courseData.getExistingFileName(), "Description");
                                            int state;
                                            int count = 0;
                                            while (count < 3) {
                                                sleep(1000);
                                                state = Integer.parseInt(panoptoService.getStatusSession(sessionId).getState());
                                                if (state == 3) {
                                                    count++;
                                                }
                                                if (state == 2 || state > 4) {
                                                    throw new Exception();
                                                }
                                            }
                                            PanoptoFolderContent content =
                                                    panoptoService.getFolderContents(new PanoptoFolder(config.getPanoptoConfiguration().panoptoCourseProperty().get().getId()));
                                            System.out.println(content.getResults().get(0).getUrls().getViewerUrl());
                                            SetModuleService.publishFileserverResource(moodleService, courseData, course, token, content.getResults().get(0).getUrls().getViewerUrl());
                                        } catch (PanoptoException e) {
                                            context.showNotification(NotificationType.ERROR, "start.sync.error.title", "start.sync.error.fileserver2.message");
                                        }
                                    }
                                    else if(config.getFileServerType().equals("FTP")) {
                                        //TODO: implement URL-Generation and Upload -> functions in moodle-sync-core
                                        // -> fileserver -> FileServerClientFTP
                                        SetModuleService.publishFileserverResource(moodleService, courseData, course,
                                                token, "https://wikipedia.org");
                                    }

                                } else if (courseData.getModuleType().equals("folder") && courseData.getAction() != MoodleAction.ExistingFile) {
                                    progressView.setMessage(context.getDictionary().get("start.sync.progress"));
                                    SetModuleService.handleFolderUpload(moodleService, courseData, course, url, token);
                                } else if (courseData.getAction() != MoodleAction.UploadSection) {
                                    progressView.setMessage(context.getDictionary().get("start.sync.progress"));
                                    SetModuleService.moveResource(moodleService, courseData, token);
                                }
                                progress++;
                                //progressView.setProgress(progress/progressSteps);
                            }
                    }
                    //Adding of new sections at the end of the sync-process to prevent new section-numbers.
                    for (SyncTableElement courseData : syncData) {
                        if (courseData.getAction() == MoodleAction.UploadSection && courseData.isSelected()) {
                            progressView.setMessage(context.getDictionary().get("start.sync.progress"));
                            //Logic for Section-Upload
                            SetModuleService.createSection(moodleService, courseData, course, token);
                            progress++;
                        }
                    }
                    progressView.setMessage(context.getDictionary().get("start.sync.finished"));
                    progressView.setProgress(1.0);
                    progressView.setOnHideClose(new BooleanProperty(true));
                /*.thenRun(this::updateCourses)
                .exceptionally(throwable -> {
                    logException(throwable, "Sync Failed");

                    progressView.setProgress(1.0);
                    progressView.setError(context.getDictionary().get("start.sync.error.title"));
                    progressView.setOnHideClose(new BooleanProperty(true));
                    return null;
                });*/
    }

}

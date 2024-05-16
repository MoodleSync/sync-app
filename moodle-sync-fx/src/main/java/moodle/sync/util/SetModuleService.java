package moodle.sync.util;

import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.MoodleUpload;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.core.web.service.MoodleService;
import moodle.sync.core.web.service.MoodleUploadTemp;
import moodle.sync.javafx.model.SyncTableElement;


public final class SetModuleService {

    /**
     * Method to publish/ update a course-module "resource". Helps to adjust the API-Call regarding visibility and
     * availability.
     */
    public static void publishResource(MoodleService moodleService, SyncTableElement courseData, Course course,
                                       String url, String token) throws Exception {
        if (courseData.getAction() == MoodleAction.MoodleUpload) {
            MoodleUploadTemp uploader = new MoodleUploadTemp();
            //Upload of the file to the Moodle-platform.
            MoodleUpload upload = uploader.upload(courseData.getExistingFileName(), courseData.getExistingFile(), url
                    , token);
            //Publish it in the Moodle-course.
            if (courseData.getUnixTimeStamp() > System.currentTimeMillis() / 1000L) {
                //Time in future
                moodleService.setResource(token, course.getId(), courseData.getSection(), upload.getItemid(),
                        courseData.getUnixTimeStamp(), courseData.getVisible(), courseData.getModuleName(),
                        courseData.getBeforemod());
            }
            else {
                //Time not modified, time should be null
                moodleService.setResource(token, course.getId(), courseData.getSection(), upload.getItemid(), null,
                        courseData.getVisible(), courseData.getModuleName(), courseData.getBeforemod());
            }
        }
        else if (courseData.getAction() == MoodleAction.MoodleSynchronize) {
            MoodleUploadTemp uploader = new MoodleUploadTemp();
            //Upload of the new file to the Moodle-platform.
            MoodleUpload upload = uploader.upload(courseData.getExistingFileName(), courseData.getExistingFile(), url
                    , token);
            //Publish it in the Moodle-course above the old course-module containing the old file.
            if (courseData.getUnixTimeStamp() > System.currentTimeMillis() / 1000L) {
                moodleService.setResource(token, course.getId(), courseData.getSection(), upload.getItemid(),
                        courseData.getUnixTimeStamp(), courseData.getVisible(), courseData.getModuleName(),
                        courseData.getBeforemod());
            }
            else {
                moodleService.setResource(token, course.getId(), courseData.getSection(), upload.getItemid(), null,
                        courseData.getVisible(), courseData.getModuleName(), courseData.getBeforemod());
            }
            //Removal of the old course-module.
            moodleService.removeResource(token, courseData.getCmid());
        } else {
            moodleService.setMoveModule(token, courseData.getCmid(), courseData.getSectionId(),
                    courseData.getBeforemod());
        }
    }

    /**
     *  Method used to publish a course-Module "url". Helps to adjust the API-Call regarding visibility and
     *  availability.
     *
     *  NOT FUNCTIONAL -> LINK IS NOT CORRECT.
     */
    public static void publishFileserverResource(MoodleService moodleService, SyncTableElement courseData,
                                                 Course course, String token, String url) {
        //TODO: Implement dynamic URLs, depends on fileserver
        // url = fileservice.....
        if (courseData.getUnixTimeStamp() > System.currentTimeMillis() / 1000L) {
            moodleService.setUrl(token, course.getId(), courseData.getSection(), courseData.getModuleName(), url,
                    courseData.getUnixTimeStamp(), courseData.getVisible(), courseData.getBeforemod());
        } else {
            moodleService.setUrl(token, course.getId(), courseData.getSection(), courseData.getModuleName(), url,
                    null, courseData.getVisible(), courseData.getBeforemod());
        }
    }

    /**
     * Method used to move a course-module.
     */
    public static void moveResource(MoodleService moodleService, SyncTableElement courseData, String token) throws Exception {
        moodleService.setMoveModule(token, courseData.getCmid(), courseData.getSectionId(), courseData.getBeforemod());
    }

    /**
     * Method used to create a new course-section. Section is added to the bottom of the course.
     */
    public static void createSection(MoodleService moodleService, SyncTableElement courseData, Course course,
                                     String token) throws Exception {
        moodleService.setSection(token, course.getId(), courseData.getModuleName(), courseData.getSection());
    }

    /**
     * Method to publish/ update a course-module "folder". Helps to adjust the API-Call regarding visibility and
     * availability. Furthermore, handles file-upload to Moodle.
     */
    public static void handleFolderUpload(MoodleService moodleService, SyncTableElement courseData, Course course,
                                          String url , String token) throws Exception {
        if(courseData.getAction() == MoodleAction.FolderUpload){
            if (courseData.getUnixTimeStamp() > System.currentTimeMillis() / 1000L) {
                moodleService.setFolder(token, course.getId(), courseData.getSection(), uploadFile(courseData, url,
                        token), courseData.getModuleName(), courseData.getUnixTimeStamp(),
                        courseData.getVisible(), courseData.getBeforemod());
            }
            else {
                moodleService.setFolder(token, course.getId(), courseData.getSection(), uploadFile(courseData, url,
                        token), courseData.getModuleName(), null, courseData.getVisible(), courseData.getBeforemod());
            }
        }
        else if (courseData.getAction() == MoodleAction.FolderSynchronize) {
            moodleService.addFilesToFolder(token, course.getId(), uploadFile(courseData, url,
                    token), courseData.getContextId());
        }
    }

    /**
     * Method used to upload files to Moodle. Returns one itemid which represents all files.
     */
    private static Long uploadFile(SyncTableElement courseData, String url, String token) {
        Long result = 0L;
        if(courseData.getContent().size() > 0) {
            MoodleUploadTemp uploader = new MoodleUploadTemp();
            MoodleUpload upload = uploader.upload(courseData.getContent().get(0).getFileName().toString(), courseData.getContent().get(0).toString(), url, token);
            result = upload.getItemid();
            if (courseData.getContent().size() > 1) {
                for (int i = 1; i < courseData.getContent().size(); i++) {
                    uploader.upload(courseData.getContent().get(i).getFileName().toString(),
                            courseData.getContent().get(i).toString(), url, token,
                            result);
                }
            }
        }
        return result;
    }
}

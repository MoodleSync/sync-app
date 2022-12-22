package moodle.sync.util;

import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.MoodleUpload;
import moodle.sync.javafx.model.syncTableElement;
import moodle.sync.core.util.MoodleAction;
import moodle.sync.core.web.service.MoodleService;
import moodle.sync.core.web.service.MoodleUploadTemp;

import java.io.File;
import java.nio.file.Path;


public final class SetModuleService {

    public static void publishResource(MoodleService moodleService, syncTableElement courseData, Course course,
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

    public static void publishFileserverResource(MoodleService moodleService, syncTableElement courseData,
                                                 Course course, String token) throws Exception {
        //TODO: konkreter fileserver upload hinzufügen
        // url = fileservice.....
        String url = "https://wikipedia.org";
        if (courseData.getUnixTimeStamp() > System.currentTimeMillis() / 1000L) {
            moodleService.setUrl(token, course.getId(), courseData.getSection(), courseData.getModuleName(), url,
                    courseData.getUnixTimeStamp(), courseData.getVisible(), courseData.getBeforemod());
        } else {
            moodleService.setUrl(token, course.getId(), courseData.getSection(), courseData.getModuleName(), url,
                    null, courseData.getVisible(), courseData.getBeforemod());
        }
    }

    public static void moveResource(MoodleService moodleService, syncTableElement courseData, String token) throws Exception {
        moodleService.setMoveModule(token, courseData.getCmid(), courseData.getSectionId(), courseData.getBeforemod());
    }

    public static void createSection(MoodleService moodleService, syncTableElement courseData, Course course,
                                     String token) throws Exception {
        moodleService.setSection(token, course.getId(), courseData.getModuleName(), courseData.getSection());
        if (!courseData.getExistingFileName().split("_", 2)[0].matches("\\d+")) {
            File temp = new File(courseData.getExistingFile());
            temp.renameTo(new File(Path.of(courseData.getExistingFile()).getParent().toString() + "/" + courseData.getSection() + "_" + courseData.getExistingFileName()));
        }
    }
}

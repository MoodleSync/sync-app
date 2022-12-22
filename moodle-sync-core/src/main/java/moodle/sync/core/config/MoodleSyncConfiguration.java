package moodle.sync.core.config;


import moodle.sync.core.model.json.Course;
import org.lecturestudio.core.beans.BooleanProperty;
import moodle.sync.core.model.json.Section;
import org.lecturestudio.core.app.configuration.Configuration;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.beans.StringProperty;

import java.util.Locale;

/**
 * This class represents a configuration containing several settings.
 */
public class MoodleSyncConfiguration extends Configuration {

    //The path where the synchronized files are stored at.
    private final StringProperty syncRootPath = new StringProperty();

    //The previously selected Course.
    private final ObjectProperty<Course> recentCourse = new ObjectProperty<>();

    //The users Moodle-token.
    private final StringProperty moodleToken = new StringProperty();

    //The previously selected Section.
    private final ObjectProperty<Section> recentSection = new ObjectProperty<>();

    //The Url of the Moodle-Platform.
    private final StringProperty moodleUrl = new StringProperty();

    //If a file belongs to this format, it should be synchronized with the Moodle-plattform.
    private final StringProperty formatsMoodle = new StringProperty();

    //If a file belongs to this format, it should be synchronized with the fileserver.
    private final StringProperty formatsFileserver = new StringProperty();

    //The Url of the fileserver.
    private final StringProperty ftpserver = new StringProperty();

    //The users fileserver-username.
    private final StringProperty ftpuser = new StringProperty();

    //The users fileserver-password.
    private final StringProperty ftppassword = new StringProperty();

    //The choosen port for the fileserver-communication.
    private final StringProperty ftpport = new StringProperty();

    //Whether files of unknown fileformat should be displayed.
    private final BooleanProperty showUnknownFormats = new BooleanProperty();

    //Language
    private final ObjectProperty<Locale> locale = new ObjectProperty();

    //Delete file property - still in work
    private final BooleanProperty executeDeletion = new BooleanProperty();

    public String getSyncRootPath() {
        return syncRootPath.get();
    }

    public void setSyncRootPath(String path) {
        this.syncRootPath.set(path);
    }

    public StringProperty syncRootPathProperty() {
        return syncRootPath;
    }

    public Course getRecentCourse() {
        return recentCourse.get();
    }

    public void setRecentCourse(Course course) {
        this.recentCourse.set(course);
    }

    public ObjectProperty<Course> recentCourseProperty() {
        return recentCourse;
    }

    public String getMoodleToken() {
        return moodleToken.get();
    }

    public void setMoodleToken(String token) {
        this.moodleToken.set(token);
    }

    public StringProperty moodleTokenProperty() {
        return moodleToken;
    }

    public Section getRecentSection() {
        return recentSection.get();
    }

    public void setRecentSection(Section section) {
        this.recentSection.set(section);
    }

    public ObjectProperty<Section> recentSectionProperty() {
        return recentSection;
    }

    public String getMoodleUrl() {
        return moodleUrl.get();
    }

    public void setMoodleUrl(String url) {
        this.moodleUrl.set(url);
    }

    public StringProperty moodleUrlProperty() {
        return moodleUrl;
    }

    public String getFormatsMoodle() {
        return formatsMoodle.get();
    }

    public void setFormatsMoodle(String formats) {
        this.formatsMoodle.set(formats);
    }

    public StringProperty formatsMoodleProperty() {
        return formatsMoodle;
    }

    public String getFormatsFileserver() {
        return formatsFileserver.get();
    }

    public void setFormatsFileserver(String formats) {
        this.formatsFileserver.set(formats);
    }

    public StringProperty formatsFileserverProperty() {
        return formatsFileserver;
    }

    public String getFileserver() {
        return ftpserver.get();
    }

    public void setFileserver(String fileserver) {
        this.ftpserver.set(fileserver);
    }

    public StringProperty FileserverProperty() {
        return ftpserver;
    }

    public String getUserFileserver() {
        return ftpuser.get();
    }

    public void setUserFileserver(String user) {
        this.ftpuser.set(user);
    }

    public StringProperty userFileserverProperty() {
        return ftpuser;
    }

    public String getPasswordFileserver() {
        return ftppassword.get();
    }

    public void setPasswordFileserver(String formats) {
        this.ftppassword.set(formats);
    }

    public StringProperty passwordFileserverProperty() {
        return ftppassword;
    }

    public String getPortFileserver() {
        return ftpport.get();
    }

    public void setPortFileserver(String port) {
        this.ftpport.set(port);
    }

    public StringProperty portFileserverProperty() {
        return ftpport;
    }

    public Boolean getShowUnknownFormats() {
        return showUnknownFormats.get();
    }

    public void setShowUnknownFormats(Boolean unknownFormats) {
        this.showUnknownFormats.set(unknownFormats);
    }

    public BooleanProperty showUnknownFormatsProperty() {
        return showUnknownFormats;
    }

    public Locale getLocale() {
        return (Locale)this.locale.get();
    }

    public void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    public ObjectProperty<Locale> localeProperty() {
        return this.locale;
    }

    public Boolean getExecuteDeletion() {
        return executeDeletion.get();
    }

    public void setExecuteDeletion(Boolean executeDeletion) {
        this.executeDeletion.set(executeDeletion);
    }

    public BooleanProperty executeDeletionProperty() {
        return executeDeletion;
    }

}

package moodle.sync.core.config;


import moodle.sync.core.model.json.Course;
import moodle.sync.core.beans.BooleanProperty;
import moodle.sync.core.model.json.Section;
import moodle.sync.core.app.configuration.Configuration;
import moodle.sync.core.beans.ObjectProperty;
import moodle.sync.core.beans.StringProperty;

import java.util.Locale;
import java.util.Objects;

/**
 * This class represents a configuration containing several settings.
 */
public class MoodleSyncConfiguration extends Configuration {

    //Language
    private final ObjectProperty<Locale> locale = new ObjectProperty<>();

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

    private final StringProperty fileServerType = new StringProperty();

    private final ObjectProperty<FileserverFTPConfiguration> ftpConfiguration = new ObjectProperty<>();

    private final ObjectProperty<FileserverPanoptoConfiguration> panoptoConfiguration = new ObjectProperty<>();

    //Whether files of unknown fileformat should be displayed.
    private final BooleanProperty showUnknownFormats = new BooleanProperty();


    public MoodleSyncConfiguration() {
    }


    public MoodleSyncConfiguration (MoodleSyncConfiguration config) {
        this.locale.set(config.locale.get());
        this.syncRootPath.set(config.syncRootPath.get());
        this.recentCourse.set(config.recentCourse.get());
        this.moodleToken.set(config.moodleToken.get());
        this.recentSection.set(config.recentSection.get());
        this.moodleUrl.set(config.moodleUrl.get());
        this.formatsMoodle.set(config.formatsMoodle.get());
        this.fileServerType.set(config.fileServerType.get());
        this.ftpConfiguration.set(new FileserverFTPConfiguration(config.ftpConfiguration.get()));
        this.panoptoConfiguration.set(new FileserverPanoptoConfiguration(config.panoptoConfiguration.get()));
        this.showUnknownFormats.set(config.showUnknownFormats.get());

    }

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

    public String getFileServerType() {
        return fileServerType.get();
    }

    public void setRecentFileServerType(String fileServerType) {
        this.fileServerType.set(fileServerType);
    }

    public StringProperty fileServerTypeProperty() {
        return fileServerType;
    }

    public FileserverFTPConfiguration getFtpConfiguration() {
        return ftpConfiguration.get();
    }

    public void setFtpConfiguration(FileserverFTPConfiguration ftpConfiguration) {
        this.ftpConfiguration.set(ftpConfiguration);
    }

    public ObjectProperty<FileserverFTPConfiguration> FtpConfigurationProperty() {
        return ftpConfiguration;
    }

    public FileserverPanoptoConfiguration getPanoptoConfiguration() {
        return panoptoConfiguration.get();
    }

    public void setPanoptoConfiguration(FileserverPanoptoConfiguration panoptoConfiguration) {
        this.panoptoConfiguration.set(panoptoConfiguration);
    }

    public ObjectProperty<FileserverPanoptoConfiguration> PanoptoConfigurationProperty() {
        return panoptoConfiguration;
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

    public boolean equals(MoodleSyncConfiguration o) {
        return Objects.equals(this.syncRootPath.get(), o.syncRootPath.get()) &&
                Objects.equals(this.recentCourse.get(), o.recentCourse.get()) &&
                Objects.equals(this.moodleToken.get(), o.moodleToken.get()) &&
                Objects.equals(this.recentSection.get(), o.recentSection.get()) &&
                Objects.equals(this.moodleUrl.get(), o.moodleUrl.get()) &&
                Objects.equals(this.formatsMoodle.get(), o.formatsMoodle.get()) &&
                Objects.equals(this.fileServerType.get(), o.fileServerType.get()) &&
                (this.ftpConfiguration.get().equals(o.ftpConfiguration.get())) &&
                (this.panoptoConfiguration.get().equals(o.panoptoConfiguration.get())) &&
                Objects.equals(this.showUnknownFormats.get(), o.showUnknownFormats.get()) &&
                Objects.equals(this.locale.get(), o.locale.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(syncRootPath, recentCourse, moodleToken, recentSection, moodleUrl, formatsMoodle,
                ftpConfiguration, panoptoConfiguration, showUnknownFormats, locale);
    }

}

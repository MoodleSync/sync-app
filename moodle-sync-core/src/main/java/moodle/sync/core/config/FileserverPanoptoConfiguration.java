package moodle.sync.core.config;

import moodle.sync.core.beans.ObjectProperty;
import moodle.sync.core.beans.StringProperty;
import moodle.sync.core.model.json.PanoptoCourse;

import java.util.Objects;

public class FileserverPanoptoConfiguration {

    //The Url of the fileserver.
    private final StringProperty panoptoServer = new StringProperty();

    //The users fileserver-username.
    private final StringProperty panoptoClientId = new StringProperty();

    //The users fileserver-password.
    private final StringProperty panoptoSecret = new StringProperty();

    private final StringProperty panoptoDefaultFolder = new StringProperty();

    //If a file belongs to this format, it should be synchronized with the fileserver.
    private final StringProperty panoptoFormats = new StringProperty();

    private final ObjectProperty<PanoptoCourse> panoptoCourse = new ObjectProperty<>();

    public FileserverPanoptoConfiguration() {
        this.panoptoServer.set("");
        this.panoptoClientId.set("");
        this.panoptoSecret.set("");
        this.panoptoDefaultFolder.set("");
        this.panoptoFormats.set("");
        this.panoptoCourse.set(new PanoptoCourse());
    }

    public FileserverPanoptoConfiguration (FileserverPanoptoConfiguration config) {
        this.panoptoServer.set(config.panoptoServer.get());
        this.panoptoClientId.set(config.panoptoClientId.get());
        this.panoptoSecret.set(config.panoptoSecret.get());
        this.panoptoDefaultFolder.set(config.panoptoDefaultFolder.get());
        this.panoptoFormats.set(config.panoptoFormats.get());
        this.panoptoCourse.set(config.panoptoCourse.get());
    }

    public String getPanoptoServer() {
        return panoptoServer.get();
    }

    public void setPanoptoServer(String fileserver) {
        this.panoptoServer.set(fileserver);
    }

    public StringProperty panoptoServerProperty() {
        return panoptoServer;
    }

    public String getPanoptoClientId() {
        return panoptoClientId.get();
    }

    public void setPanoptoClientId(String user) {
        this.panoptoClientId.set(user);
    }

    public StringProperty panoptoClientIdProperty() {
        return panoptoClientId;
    }

    public String getPanoptoSecret() {
        return panoptoSecret.get();
    }

    public void setPanoptoSecret(String formats) {
        this.panoptoSecret.set(formats);
    }

    public StringProperty panoptoSecretProperty() {
        return panoptoSecret;
    }

    public String getPanoptoDefaultFolder() {
        return panoptoDefaultFolder.get();
    }

    public void setPanoptoDefaultFolder(String folder) {
        this.panoptoDefaultFolder.set(folder);
    }

    public StringProperty panoptoDefaultFolderProperty() {
        return panoptoDefaultFolder;
    }

    public String getPanoptoFormats() {
        return panoptoFormats.get();
    }

    public void setPanoptoFormats(String formats) {
        this.panoptoFormats.set(formats);
    }

    public StringProperty panoptoFormatsProperty() {
        return panoptoFormats;
    }

    public PanoptoCourse getPanoptoCourse() {
        return panoptoCourse.get();
    }

    public void setPanoptoCourse(PanoptoCourse panoptoCourse) {
        this.panoptoCourse.set(panoptoCourse);
    }

    public ObjectProperty<PanoptoCourse> panoptoCourseProperty() {
        return panoptoCourse;
    }

    @Override
    public int hashCode() {
        return Objects.hash(panoptoServer, panoptoClientId, panoptoSecret, panoptoDefaultFolder, panoptoFormats, panoptoCourse);
    }

    public boolean equals(FileserverPanoptoConfiguration o) {
        return Objects.equals(this.panoptoServer.get(), o.panoptoServer.get()) &&
                Objects.equals(this.panoptoClientId.get(), o.panoptoClientId.get()) &&
                Objects.equals(this.panoptoSecret.get(), o.panoptoSecret.get()) &&
                Objects.equals(this.panoptoDefaultFolder.get(), o.panoptoDefaultFolder.get()) &&
                Objects.equals(this.panoptoFormats.get(), o.panoptoFormats.get()) &&
                Objects.equals(this.panoptoCourse.get(), o.panoptoCourse.get());
    }
}

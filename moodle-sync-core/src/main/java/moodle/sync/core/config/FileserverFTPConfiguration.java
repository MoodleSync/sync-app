package moodle.sync.core.config;

import moodle.sync.core.beans.StringProperty;

import java.util.Objects;

public class FileserverFTPConfiguration {

    //The Url of the fileserver.
    private final StringProperty ftpServer = new StringProperty();

    //The users fileserver-username.
    private final StringProperty ftpUser = new StringProperty();

    //The users fileserver-password.
    private final StringProperty ftpPassword = new StringProperty();

    //The choosen port for the fileserver-communication.
    private final StringProperty ftpPort = new StringProperty();

    //If a file belongs to this format, it should be synchronized with the fileserver.
    private final StringProperty ftpFormats = new StringProperty();

    public FileserverFTPConfiguration() {
        this.ftpServer.set("");
        this.ftpUser.set("");
        this.ftpPassword.set("");
        this.ftpPort.set("");
        this.ftpFormats.set("");
    }

    public FileserverFTPConfiguration (FileserverFTPConfiguration config) {
        this.ftpServer.set(config.ftpServer.get());
        this.ftpUser.set(config.ftpUser.get());
        this.ftpPassword.set(config.ftpPassword.get());
        this.ftpPort.set(config.ftpPort.get());
        this.ftpFormats.set(config.ftpFormats.get());
    }

    public String getFtpServer() {
        return ftpServer.get();
    }

    public void setFtpServer(String fileserver) {
        this.ftpServer.set(fileserver);
    }

    public StringProperty ftpServerProperty() {
        return ftpServer;
    }

    public String getFtpUser() {
        return ftpUser.get();
    }

    public void setFtpUser(String user) {
        this.ftpUser.set(user);
    }

    public StringProperty ftpUserProperty() {
        return ftpUser;
    }

    public String getFtpPassword() {
        return ftpPassword.get();
    }

    public void setFtpPassword(String formats) {
        this.ftpPassword.set(formats);
    }

    public StringProperty ftpPasswordProperty() {
        return ftpPassword;
    }

    public String getFtpPort() {
        return ftpPort.get();
    }

    public void setFtpPort(String port) {
        this.ftpPort.set(port);
    }

    public StringProperty ftpPortProperty() {
        return ftpPort;
    }

    public String getFtpFormats() {
        return ftpFormats.get();
    }

    public void setFtpFormats(String formats) {
        this.ftpFormats.set(formats);
    }

    public StringProperty ftpFormatsProperty() {
        return ftpFormats;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ftpServer, ftpUser, ftpPassword, ftpPort, ftpFormats);
    }

    public boolean equals(FileserverFTPConfiguration o) {
        return Objects.equals(this.ftpServer.get(), o.ftpServer.get()) &&
                Objects.equals(this.ftpUser.get(), o.ftpUser.get()) &&
                Objects.equals(this.ftpPassword.get(), o.ftpPassword.get()) &&
                Objects.equals(this.ftpPort.get(), o.ftpPort.get()) &&
                Objects.equals(this.ftpFormats.get(), o.ftpFormats.get());
    }
}

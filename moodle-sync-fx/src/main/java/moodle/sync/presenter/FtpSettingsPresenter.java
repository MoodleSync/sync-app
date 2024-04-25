package moodle.sync.presenter;

import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.config.FileserverFTPConfiguration;
import moodle.sync.core.presenter.Presenter;
import moodle.sync.view.FtpSettingsView;

import javax.inject.Inject;


public class FtpSettingsPresenter extends Presenter<FtpSettingsView> {

    private FileserverFTPConfiguration config;

    @Inject
    FtpSettingsPresenter(ApplicationContext context, FtpSettingsView view) {
        super(context, view);
    }

    @Override
    public void initialize() throws Exception{

        view.setFtpField(config.ftpServerProperty());
        view.setFtpUser(config.ftpUserProperty());
        view.setFtpPassword(config.ftpPasswordProperty());
        view.setFtpPort(config.ftpPortProperty());
        view.setFormatsFTP(config.ftpFormatsProperty());

    }

    public void setFtpConfig(FileserverFTPConfiguration fileserverFtpConfiguration) {
        this.config = fileserverFtpConfiguration;
    }

}

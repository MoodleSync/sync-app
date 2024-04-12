package moodle.sync.presenter;

import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.app.LocaleProvider;
import moodle.sync.core.beans.ObjectProperty;
import moodle.sync.core.beans.StringProperty;
import moodle.sync.core.config.DefaultConfiguration;
import moodle.sync.core.config.FileserverFTPConfiguration;
import moodle.sync.core.config.FileserverPanoptoConfiguration;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.fileserver.FileServerType;
import moodle.sync.core.presenter.Presenter;
import moodle.sync.core.view.ConsumerAction;
import moodle.sync.core.view.DirectoryChooserView;
import moodle.sync.core.view.NotificationType;
import moodle.sync.core.view.ViewContextFactory;
import moodle.sync.core.web.model.TokenProvider;
import moodle.sync.core.web.panopto.PanoptoService;
import moodle.sync.javafx.model.SyncTableElement;
import moodle.sync.view.SettingsView;
import moodle.sync.core.web.service.MoodleService;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * Class defining the logic of the "settings-page".
 *
 * @author Daniel Schröter
 */
public class SettingsPresenter extends Presenter<SettingsView> {

    private final ViewContextFactory viewFactory;

    private final MoodleService moodleService;

    private ConsumerAction<MoodleSyncConfiguration> closeAction;

    private MoodleSyncConfiguration settingsConfig;

    private final MoodleSyncConfiguration config;

    @Inject
    SettingsPresenter(ApplicationContext context, SettingsView view,
                      ViewContextFactory viewFactory, MoodleService moodleService) {
        super(context, view);

        this.config = (MoodleSyncConfiguration) context.getConfiguration();
        this.moodleService = moodleService;
        this.viewFactory = viewFactory;
    }

    @Override
    public void initialize() throws Exception{
        LocaleProvider localeProvider = new LocaleProvider();

        this.settingsConfig = new MoodleSyncConfiguration(config);

        //Initialising all functions of the "settings-page" with the help of the configuration.
        String type = settingsConfig.fileServerTypeProperty().get();
        if(type.equals("No")) {
            view.setNoFileserver();
        } else if (type.equals("FTP")){
            view.setFtp();
        } else {
            view.setPanopto();
        }

        FileserverFTPConfiguration ftpConfig = config.getFtpConfiguration();
        FileserverPanoptoConfiguration panoptoConfig = config.getPanoptoConfiguration();
        view.setOnExit(this::close);
        view.setLocales(localeProvider.getLocales());
        view.setLocale(settingsConfig.localeProperty());
        view.setMoodleField(settingsConfig.moodleUrlProperty());
        view.setFormatsMoodle(settingsConfig.formatsMoodleProperty());
        view.setMoodleToken(settingsConfig.moodleTokenProperty());
        view.setOnCheckToken(this::checkToken);
        view.setOnCheckPanopto(this::checkPanopto);
        view.setSyncRootPath(settingsConfig.syncRootPathProperty());
        view.setSelectSyncRootPath(this::selectSyncPath);
        view.setShowUnknownFormats(settingsConfig.showUnknownFormatsProperty());
        view.setFileserver(settingsConfig.fileServerTypeProperty());
        view.setFileservers(List.of("No", "FTP", "Panopto"));


        view.setFtpField(ftpConfig.ftpServerProperty());
        view.setFtpUser(ftpConfig.ftpUserProperty());
        view.setFtpPassword(ftpConfig.ftpPasswordProperty());
        view.setFtpPort(ftpConfig.ftpPortProperty());
        view.setFormatsFTP(ftpConfig.ftpFormatsProperty());

        view.setPanoptoField(panoptoConfig.panoptoServerProperty());
        view.setPanoptoClient(panoptoConfig.panoptoClientIdProperty());
        view.setPanoptoSecret(panoptoConfig.panoptoSecretProperty());
        view.setFormatsPanopto(panoptoConfig.panoptoFormatsProperty());
        view.setFileserverDefaultFolder(panoptoConfig.panoptoDefaultFolderProperty());

        settingsConfig.fileServerTypeProperty().addListener((observable, oldType, newType) -> {
            if (newType.equals("Panopto"))  {
                //viewFactory.getInstance()
                view.setPanopto();
            } else if(newType.equals("FTP")){
                view.setFtp();
            } else {
                view.setNoFileserver();
            }
        });
    }

    /**
     * Function to close the "settings-page".
     *
     */
    @Override
    public void close() {
        MoodleSyncConfiguration config = (MoodleSyncConfiguration) settingsConfig;
        try {
            if (config.getFileServerType().equals("Panopto")) {
                if (config.getPanoptoConfiguration().getPanoptoServer().isBlank() ||
                        config.getPanoptoConfiguration().getPanoptoClientId().isBlank() ||
                        config.getPanoptoConfiguration().getPanoptoSecret().isBlank()) {
                    throw new Exception();
                }
                if (Objects.nonNull(this.closeAction) && this.isCloseable()) {
                    this.closeAction.execute(settingsConfig);
                }
                super.close();
            } else if (config.getFileServerType().equals("FTP")) {
                if (config.getFtpConfiguration().getFtpServer().isBlank() ||
                        config.getFtpConfiguration().getFtpUser().isBlank() ||
                        config.getFtpConfiguration().getFtpPassword().isBlank() ||
                        config.getFtpConfiguration().getFtpPort().isBlank()) {
                    throw new Exception();
                }
                if (Objects.nonNull(this.closeAction) && this.isCloseable()) {
                    this.closeAction.execute(settingsConfig);
                }
                super.close();
            }
            else {
                //config.setFormatsFileserver("");
                if (Objects.nonNull(this.closeAction) && this.isCloseable()) {
                    this.closeAction.execute(settingsConfig);
                }
                super.close();
            }
        } catch (Exception e) {
            logException(e, "Sync failed");
            context.showNotification(NotificationType.ERROR, "settings.close.error.title", "settings.close.error" +
                    ".panopto");
        }

    }

    /**
     * Providing the functionality to choose a Root-Directory.
     */
    private void selectSyncPath() {
        String syncPath = settingsConfig.getSyncRootPath();
        //Check whether a default path should be used to prevent unwanted behavior.
        if (syncPath == null || syncPath.isEmpty() || syncPath.isBlank()) {
            DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
            syncPath = defaultConfiguration.getSyncRootPath();
        }
        File initDirectory = new File(syncPath);
        DirectoryChooserView dirChooser = viewFactory.createDirectoryChooserView();
        File selectedFile;
        try {
            dirChooser.setInitialDirectory(initDirectory);
            selectedFile = dirChooser.show(view);
        }
        catch (IllegalArgumentException e) {
            //Show working directory if an illegal path is entered.
            DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
            dirChooser.setInitialDirectory(new File(defaultConfiguration.getSyncRootPath()));
            selectedFile = dirChooser.show(view);
        }
        if (nonNull(selectedFile)) {
            settingsConfig.setSyncRootPath(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Used to verify the inserted token.
     */
    private void checkToken() {
        try{
            //MoodleSyncConfiguration settingsConfig = (MoodleSyncConfiguration) context.getConfiguration();
            moodleService.setApiUrl(settingsConfig.getMoodleUrl());
            moodleService.getUserId(settingsConfig.getMoodleToken());
            view.setTokenValid(true);
        } catch (Exception e){
            view.setTokenValid(false);
        }
    }

    private void checkPanopto() {
        try {
            PanoptoService panoptoService = new PanoptoService(settingsConfig.getPanoptoConfiguration().getPanoptoServer(),
                    new TokenProvider(config.getPanoptoConfiguration().getPanoptoClientId()
                            , config.getPanoptoConfiguration().getPanoptoSecret()));
            panoptoService.getSearchFolder("test");
            view.setPanoptoValid(true);
        } catch (Exception e) {
            view.setPanoptoValid(false);
        }
    }

    /**
     * Closes the presenter and hands over the settingsConfig.
     *
     * @param settingsConfig config to hand over.
     */
    public void setOnClose(ConsumerAction<MoodleSyncConfiguration> settingsConfig) {
        this.closeAction = ConsumerAction.concatenate(this.closeAction, settingsConfig);
    }
}

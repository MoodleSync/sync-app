package moodle.sync.presenter;

import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.app.LocaleProvider;
import moodle.sync.core.config.DefaultConfiguration;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.fileserver.ftp.FtpException;
import moodle.sync.core.fileserver.panopto.PanoptoException;
import moodle.sync.core.presenter.Presenter;
import moodle.sync.core.view.ConsumerAction;
import moodle.sync.core.view.DirectoryChooserView;
import moodle.sync.core.view.NotificationType;
import moodle.sync.core.view.ViewContextFactory;
import moodle.sync.view.FtpSettingsView;
import moodle.sync.view.PanoptoSettingsView;
import moodle.sync.view.SettingsView;
import moodle.sync.core.web.service.MoodleService;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
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

    private FtpSettingsView ftpSettingsView;

    private PanoptoSettingsView panoptoSettingsView;

    @Inject
    SettingsPresenter(ApplicationContext context, SettingsView view, ViewContextFactory viewFactory, MoodleService moodleService) {
        super(context, view);

        this.config = (MoodleSyncConfiguration) context.getConfiguration();
        this.moodleService = moodleService;
        this.viewFactory = viewFactory;
    }

    @Override
    public void initialize() throws Exception {
        LocaleProvider localeProvider = new LocaleProvider();

        this.settingsConfig = new MoodleSyncConfiguration(config);

        //Initialising all functions of the "settings-page" with the help of the configuration.
        String type = settingsConfig.fileServerTypeProperty().get();
        if (type.equals(context.getDictionary().get("settings.choosefileserver.none"))) {
            view.clearFileservers();
        } else if (type.equals("FTP")) {
            view.clearFileservers();
            ftpSettingsView = getFtpSettingsView();
            view.setFtp(ftpSettingsView);
        } else {
            view.clearFileservers();
            panoptoSettingsView = getPanoptoSettingsView();
            view.setPanopto(panoptoSettingsView);
        }

        view.setOnExit(this::close);
        view.setLocales(localeProvider.getLocales());
        view.setLocale(settingsConfig.localeProperty());
        view.setMoodleField(settingsConfig.moodleUrlProperty());
        view.setFormatsMoodle(settingsConfig.formatsMoodleProperty());
        view.setMoodleToken(settingsConfig.moodleTokenProperty());
        view.setOnCheckToken(this::checkToken);
        view.setSyncRootPath(settingsConfig.syncRootPathProperty());
        view.setSelectSyncRootPath(this::selectSyncPath);
        view.setShowUnknownFormats(settingsConfig.showUnknownFormatsProperty());
        view.setFileserver(settingsConfig.fileServerTypeProperty());
        view.setFileservers(List.of(context.getDictionary().get("settings.choosefileserver.none"), "FTP", "Panopto"));

        settingsConfig.fileServerTypeProperty().addListener((observable, oldType, newType) -> {
            if (newType.equals("Panopto")) {
                try {
                    view.clearFileservers();
                    if (isNull(panoptoSettingsView)) {
                        panoptoSettingsView = getPanoptoSettingsView();
                    }
                    view.setPanopto(panoptoSettingsView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (newType.equals("FTP")) {
                try {
                    view.clearFileservers();
                    if (isNull(ftpSettingsView)) {
                        ftpSettingsView = getFtpSettingsView();
                    }
                    view.setFtp(ftpSettingsView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                view.clearFileservers();
            }
        });
    }

    /**
     * Function to close the "settings-page".
     */
    @Override
    public void close() {
        try {
            if (settingsConfig.getFileServerType().equals("Panopto")) {

                if (settingsConfig.getPanoptoConfiguration().getPanoptoServer().isBlank() ||
                        settingsConfig.getPanoptoConfiguration().getPanoptoClientId().isBlank() ||
                        settingsConfig.getPanoptoConfiguration().getPanoptoSecret().isBlank()) {
                    throw new PanoptoException();
                }
                if (Objects.nonNull(this.closeAction) && this.isCloseable()) {
                    this.closeAction.execute(settingsConfig);
                }
                super.close();
            } else if (settingsConfig.getFileServerType().equals("FTP")) {
                if (settingsConfig.getFtpConfiguration().getFtpServer().isBlank() ||
                        settingsConfig.getFtpConfiguration().getFtpUser().isBlank() ||
                        settingsConfig.getFtpConfiguration().getFtpPassword().isBlank() ||
                        settingsConfig.getFtpConfiguration().getFtpPort().isBlank()) {
                    throw new FtpException();
                }
                if (Objects.nonNull(this.closeAction) && this.isCloseable()) {
                    this.closeAction.execute(settingsConfig);
                }
                super.close();
            } else {
                //config.setFormatsFileserver("");
                if (Objects.nonNull(this.closeAction) && this.isCloseable()) {
                    this.closeAction.execute(settingsConfig);
                }
                super.close();
            }
        } catch (PanoptoException e) {
            logException(e, "Sync failed");
            context.showNotification(NotificationType.ERROR, "settings.close.error.title",
                    "settings.close.error" + ".panopto");
        } catch (FtpException e) {
            logException(e, "Sync failed");
            context.showNotification(NotificationType.ERROR, "settings.close.error.title",
                    "settings.close.error" + ".ftp");
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (IllegalArgumentException e) {
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
        try {
            moodleService.setApiUrl(settingsConfig.getMoodleUrl());
            moodleService.getUserId(settingsConfig.getMoodleToken());
            view.setTokenValid(true);
        } catch (Exception e) {
            view.setTokenValid(false);
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

    private FtpSettingsView getFtpSettingsView() {
        FtpSettingsView ftpView = null;
        try {
            FtpSettingsPresenter ftpSettingsPresenter = viewFactory.getInstance(FtpSettingsPresenter.class);
            ftpSettingsPresenter.setFtpConfig(settingsConfig.getFtpConfiguration());
            ftpSettingsPresenter.initialize();
            ftpView = ftpSettingsPresenter.getView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ftpView;
    }

    private PanoptoSettingsView getPanoptoSettingsView() {
        PanoptoSettingsView panoptoView = null;
        try {
            PanoptoSettingsPresenter panoptoSettingsPresenter = viewFactory.getInstance(PanoptoSettingsPresenter.class);
            panoptoSettingsPresenter.setPanoptoConfig(settingsConfig.getPanoptoConfiguration());
            panoptoSettingsPresenter.initialize();
            panoptoView = panoptoSettingsPresenter.getView();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return panoptoView;
    }
}

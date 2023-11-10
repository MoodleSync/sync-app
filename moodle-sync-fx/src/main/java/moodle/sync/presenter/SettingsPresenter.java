package moodle.sync.presenter;

import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.app.LocaleProvider;
import moodle.sync.core.config.DefaultConfiguration;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.presenter.Presenter;
import moodle.sync.core.view.ConsumerAction;
import moodle.sync.core.view.DirectoryChooserView;
import moodle.sync.core.view.ViewContextFactory;
import moodle.sync.view.SettingsView;
import moodle.sync.core.web.service.MoodleService;

import javax.inject.Inject;
import java.io.File;
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
        view.setOnExit(this::close);
        view.setLocales(localeProvider.getLocales());
        view.setLocale(settingsConfig.localeProperty());
        view.setMoodleField(settingsConfig.moodleUrlProperty());
        view.setFormatsMoodle(settingsConfig.formatsMoodleProperty());
        view.setFormatsFileserver(settingsConfig.formatsFileserverProperty());
        view.setMoodleToken(settingsConfig.moodleTokenProperty());
        view.setOnCheckToken(this::checkToken);
        view.setSyncRootPath(settingsConfig.syncRootPathProperty());
        view.setSelectSyncRootPath(this::selectSyncPath);
        view.setFtpField(settingsConfig.FileserverProperty());
        view.setFtpPort(settingsConfig.portFileserverProperty());
        view.setFtpUser(settingsConfig.userFileserverProperty());
        view.setFtpPassword(settingsConfig.passwordFileserverProperty());
        view.setShowUnknownFormats(settingsConfig.showUnknownFormatsProperty());
    }

    /**
     * Function to close the "settings-page".
     *
     */
    @Override
    public void close() {
        if (Objects.nonNull(this.closeAction) && this.isCloseable()) {
            this.closeAction.execute(settingsConfig);
        }
        super.close();
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

    /**
     * Closes the presenter and hands over the settingsConfig.
     *
     * @param settingsConfig config to hand over.
     */
    public void setOnClose(ConsumerAction<MoodleSyncConfiguration> settingsConfig) {
        this.closeAction = ConsumerAction.concatenate(this.closeAction, settingsConfig);
    }
}

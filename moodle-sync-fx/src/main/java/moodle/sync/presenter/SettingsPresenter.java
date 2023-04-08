package moodle.sync.presenter;

import moodle.sync.core.config.DefaultConfiguration;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.view.SettingsView;
import moodle.sync.core.web.service.MoodleService;

import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.app.LocaleProvider;
import org.lecturestudio.core.presenter.Presenter;
import org.lecturestudio.core.view.DirectoryChooserView;
import org.lecturestudio.core.view.ViewContextFactory;

import javax.inject.Inject;
import java.io.File;

import static java.util.Objects.nonNull;

/**
 * Class defining the logic of the "settings-page".
 *
 * @author Daniel Schröter
 */
public class SettingsPresenter extends Presenter<SettingsView> {

    private final ViewContextFactory viewFactory;

    //Used MoodleService for executing Web Service API-Calls.
    private final MoodleService moodleService;

    @Inject
    SettingsPresenter(ApplicationContext context, SettingsView view,
                      ViewContextFactory viewFactory, MoodleService moodleService) {
        super(context, view);

        this.moodleService = moodleService;
        this.viewFactory = viewFactory;
    }

    @Override
    public void initialize() throws Exception{
        MoodleSyncConfiguration config = (MoodleSyncConfiguration) context.getConfiguration();
        LocaleProvider localeProvider = new LocaleProvider();

        //Initialising all functions of the "settings-page" with the help of the configuration.
        view.setOnExit(this::close);
        view.setLocales(localeProvider.getLocales());
        view.setLocale(config.localeProperty());
        view.setMoodleField(config.moodleUrlProperty());
        view.setFormatsMoodle(config.formatsMoodleProperty());
        view.setFormatsFileserver(config.formatsFileserverProperty());
        view.setMoodleToken(config.moodleTokenProperty());
        view.setOnCheckToken(this::checkToken);
        view.setSyncRootPath(config.syncRootPathProperty());
        view.setSelectSyncRootPath(this::selectSyncPath);
        view.setFtpField(config.FileserverProperty());
        view.setFtpPort(config.portFileserverProperty());
        view.setFtpUser(config.userFileserverProperty());
        view.setFtpPassword(config.passwordFileserverProperty());
        view.setShowUnknownFormats(config.showUnknownFormatsProperty());
    }

    /**
     * Function to close the "settings-page".
     */
    @Override
    public void close() {
        MoodleSyncConfiguration config = (MoodleSyncConfiguration) context.getConfiguration();
        //Reconstruct the MoodleService with the new settings.
        moodleService.setApiUrl(config.getMoodleUrl());
        super.close();
    }

    /**
     * Providing the functionality to choose a Root-Directory.
     */
    private void selectSyncPath() {
        MoodleSyncConfiguration config = (MoodleSyncConfiguration) context.getConfiguration();
        String syncPath = config.getSyncRootPath();
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
            config.setSyncRootPath(selectedFile.getAbsolutePath());
        }
    }

    private void checkToken() {
        try{
            MoodleSyncConfiguration config = (MoodleSyncConfiguration) context.getConfiguration();
            moodleService.setApiUrl(config.getMoodleUrl());
            moodleService.getUserId(config.getMoodleToken());
            view.setTokenValid(true);
        } catch (Exception e){
            view.setTokenValid(false);
        }
    }
}

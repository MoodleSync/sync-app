package moodle.sync.presenter.command;

import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.presenter.SettingsPresenter;
import org.lecturestudio.core.presenter.command.ShowPresenterCommand;
import org.lecturestudio.core.view.ConsumerAction;

/**
 * Class used to display and close the settings Page.
 */
public class ShowSettingsCommand extends ShowPresenterCommand<SettingsPresenter> {

    private final ConsumerAction<MoodleSyncConfiguration> closeAction;

    public ShowSettingsCommand(ConsumerAction<MoodleSyncConfiguration> closeAction) {
        super(SettingsPresenter.class);

        this.closeAction = closeAction;
    }

    @Override
    public void execute(SettingsPresenter presenter) {
        presenter.setOnClose(closeAction);
    }

}

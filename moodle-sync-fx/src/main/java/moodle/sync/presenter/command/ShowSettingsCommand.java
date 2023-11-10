package moodle.sync.presenter.command;

import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.presenter.command.ShowPresenterCommand;
import moodle.sync.core.view.ConsumerAction;
import moodle.sync.presenter.SettingsPresenter;

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

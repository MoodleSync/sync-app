package moodle.sync.presenter.command;

import moodle.sync.presenter.SettingsPresenter;
import org.lecturestudio.core.presenter.command.ShowPresenterCommand;
import org.lecturestudio.core.view.Action;

public class ShowSettingsCommand extends ShowPresenterCommand<SettingsPresenter> {
    private final Action closeAction;


    public ShowSettingsCommand(Action closeAction) {
        super(SettingsPresenter.class);

        this.closeAction = closeAction;
    }

    @Override
    public void execute(SettingsPresenter presenter) {
        presenter.setOnClose(closeAction);
    }
}

package moodle.sync.presenter.command;

import moodle.sync.presenter.GuestPresenter;
import moodle.sync.presenter.SettingsPresenter;
import org.lecturestudio.core.presenter.command.ShowPresenterCommand;
import org.lecturestudio.core.view.Action;

public class ShowGuestCommand extends ShowPresenterCommand<GuestPresenter> {

    private final Action closeAction;


    public ShowGuestCommand(Action closeAction) {
        super(GuestPresenter.class);

        this.closeAction = closeAction;
    }

    @Override
    public void execute(GuestPresenter presenter) {
        presenter.setOnClose(closeAction);
    }
}

package moodle.sync.presenter.command;

import moodle.sync.presenter.GuestPresenter;
import moodle.sync.presenter.TrainerPresenter;
import org.lecturestudio.core.presenter.command.ShowPresenterCommand;
import org.lecturestudio.core.view.Action;

public class ShowTrainerCommand extends ShowPresenterCommand<TrainerPresenter> {

    private final Action closeAction;


    public ShowTrainerCommand(Action closeAction) {
        super(TrainerPresenter.class);

        this.closeAction = closeAction;
    }

    @Override
    public void execute(TrainerPresenter presenter) {
        presenter.setOnClose(closeAction);
    }
}

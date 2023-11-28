package moodle.sync.core.presenter;

import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.view.Action;
import moodle.sync.core.view.ConfirmationNotificationView;
import moodle.sync.core.view.NotificationType;
import moodle.sync.core.view.ViewLayer;

import javax.inject.Inject;


/**
 * Generic notification class used for notification windows with both an accept and decline option.
 */
public class ConfirmationNotificationPresenter extends Presenter<ConfirmationNotificationView> {

	@Inject
	public ConfirmationNotificationPresenter(ApplicationContext context, ConfirmationNotificationView view) {
		super(context, view);
	}

	public void setNotificationType(NotificationType type) {
		view.setType(type);
	}

	public void setTitle(String title) {
		view.setTitle(title);
	}

	public void setMessage(String message) {
		view.setMessage(message);
	}

	public void setConfirmationAction(Action action) {
		view.setOnConfirm(() -> {
			action.execute();
			close();
		});
	}

	public void setDiscardAction(Action action) {
		view.setOnDiscard(() -> {
			action.execute();
			close();
		});
	}

	@Override
	public void initialize() {
	}

	@Override
	public ViewLayer getViewLayer() {
		return ViewLayer.Notification;
	}

	public void setConfirmButtonText(String confirmButtonText) {
		view.setConfirmButtonText(confirmButtonText);
	}

	public void setDiscardButtonText(String closeButtonText) {
		view.setDiscardButtonText(closeButtonText);
	}
}

/*
 * Copyright (C) 2020 TU Darmstadt, Department of Computer Science,
 * Embedded Systems and Applications Group.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package moodle.sync.core.presenter;

import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.view.NotificationType;
import moodle.sync.core.view.NotificationView;
import moodle.sync.core.view.ViewLayer;

import javax.inject.Inject;

/**
 * Notification presenter implementation. Notifications will be shown on the
 * {@code Notification} layer at the top-most view layer.
 *
 * @author Alex Andres
 */
public class NotificationPresenter extends Presenter<NotificationView> {

	@Inject
    NotificationPresenter(ApplicationContext context, NotificationView view) {
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

	@Override
	public void initialize() {
		view.setOnClose(this::close);
	}

	@Override
	public ViewLayer getViewLayer() {
		return ViewLayer.Notification;
	}
}

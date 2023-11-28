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

package moodle.sync.core.app;


import moodle.sync.core.app.configuration.Configuration;
import moodle.sync.core.app.dictionary.Dictionary;
import moodle.sync.core.beans.BooleanProperty;
import moodle.sync.core.bus.EventBus;

import moodle.sync.core.geometry.Position;
import moodle.sync.core.presenter.command.ConfirmationNotificationCommand;
import moodle.sync.core.presenter.command.NotificationCommand;
import moodle.sync.core.presenter.command.NotificationPopupCommand;
import moodle.sync.core.view.Action;
import moodle.sync.core.view.NotificationType;

import java.text.MessageFormat;


import static java.util.Objects.requireNonNull;

/**
 * Base application context implementation that holds data object required by
 * the application. Such objects are, for instance, the {@link Configuration},
 * the {@link Dictionary}
 *
 * @author Alex Andres
 */
public abstract class ApplicationContext {

	/** Indicates whether the application is in fullscreen mode. */
	private final BooleanProperty fullscreen = new BooleanProperty();

	/** The application resource data locator. */
	private final AppDataLocator dataLocator;

	/** The application configuration. */
	private final Configuration configuration;

	/** The application dictionary. */
	private final Dictionary dictionary;

	/** The application event data bus. */
	private final EventBus eventBus;

	/** The audio event bus. */
	private final EventBus audioBus;


	/**
	 * This method is meant to be implemented by concrete application context
	 * class that implement their own configuration handling, like specific
	 * configuration paths and names.
	 *
	 * @throws Exception If a fatal error occurs while saving the configuration.
	 */
	public abstract void saveConfiguration() throws Exception;


	/**
	 * Create a new {@link ApplicationContext} instance with the given parameters.
	 *
	 * @param dataLocator The application resource data locator.
	 * @param config      The application configuration.
	 * @param dict        The application dictionary.
	 * @param eventBus    The application event data bus.
	 * @param audioBus    The audio event bus.
	 */
	protected ApplicationContext(AppDataLocator dataLocator, Configuration config,
                                 Dictionary dict, EventBus eventBus, EventBus audioBus) {
		this.dataLocator = dataLocator;
		this.configuration = config;
		this.dictionary = dict;
		this.eventBus = eventBus;
		this.audioBus = audioBus;
	}

	/**
	 * Obtain the application configuration.
	 *
	 * @return the application configuration.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Obtain the application dictionary.
	 *
	 * @return the application dictionary.
	 */
	public Dictionary getDictionary() {
		return dictionary;
	}

	/**
	 * Obtain the application event data bus.
	 *
	 * @return the application event data bus.
	 */
	public EventBus getEventBus() {
		return eventBus;
	}

	/**
	 * Obtain the audio event bus.
	 *
	 * @return the audio event bus.
	 */
	public EventBus getAudioBus() {
		return audioBus;
	}

	/**
	 * Obtain the AppDataLocator to access application specific data.
	 *
	 * @return the AppDataLocator.
	 */
	public AppDataLocator getDataLocator() {
		return dataLocator;
	}


	/**
	 * Puts the application in full screen mode.
	 *
	 * @param active True to set full screen mode.
	 */
	public void setFullscreen(boolean active) {
		fullscreen.set(active);
	}

	/**
	 * Returns the observable fullscreen property.
	 *
	 * @return The fullscreen property.
	 */
	public BooleanProperty fullscreenProperty() {
		return fullscreen;
	}


	public final void showError(String title, String message) {
		requireNonNull(title);

		showNotification(NotificationType.ERROR, title, message);
	}

	public final void showError(String title, String message, Object... messageParams) {
		showNotification(NotificationType.ERROR, title, message, messageParams);
	}

	public final void showNotification(NotificationType type, String title, String message) {
		if (getDictionary().contains(title)) {
			title = getDictionary().get(title);
		}
		if (getDictionary().contains(message)) {
			message = getDictionary().get(message);
		}

		getEventBus().post(new NotificationCommand(type, title, message));
	}

	public final void showNotification(NotificationType type, String title, String message, Object... messageParams) {
		if (getDictionary().contains(message)) {
			message = getDictionary().get(message);
		}

		message = MessageFormat.format(message, messageParams);

		showNotification(type, title, message);
	}

	public final void showNotificationPopup(String title) {
		showNotificationPopup(title, null);
	}

	public final void showNotificationPopup(String title, String message) {
		if (getDictionary().contains(title)) {
			title = getDictionary().get(title);
		}
		if (getDictionary().contains(message)) {
			message = getDictionary().get(message);
		}

		getEventBus().post(new NotificationPopupCommand(Position.TOP_RIGHT, title, message));
	}

	/**
	 * Opens a notification pop with an accept and decline option.
	 *
	 * @param type          The Notification Type
	 * @param title         The title of the notification
	 * @param message       The message of the notification
	 * @param confirmAction The action when the user clicks the confirm button
	 * @param discardAction The action when the user clicks the close button
	 */
	public final void showConfirmationNotification(NotificationType type, String title, String message,
	                                               Action confirmAction, Action discardAction) {
		showConfirmationNotification(type, title, message, confirmAction, discardAction, "button.confirm", "button.close");
	}

	/**
	 * Opens a notification pop with an accept and decline option.
	 *
	 * @param type          The Notification Type
	 * @param title         The title of the notification
	 * @param message       The message of the notification
	 * @param confirmAction The action when the user clicks the confirm button
	 * @param discardAction The action when the user clicks the close button
	 */
	public final void showConfirmationNotification(NotificationType type, String title, String message,
	                                               Action confirmAction, Action discardAction,
	                                               String confirmButtonText, String discardButtonText) {
		if (getDictionary().contains(title)) {
			title = getDictionary().get(title);
		}
		if (getDictionary().contains(message)) {
			message = getDictionary().get(message);
		}
		if (getDictionary().contains(confirmButtonText)) {
			confirmButtonText = getDictionary().get(confirmButtonText);
		}
		if (getDictionary().contains(discardButtonText)) {
			discardButtonText = getDictionary().get(discardButtonText);
		}


		getEventBus().post(new ConfirmationNotificationCommand(type, title, message, confirmAction,
				discardAction, confirmButtonText, discardButtonText));
	}
}

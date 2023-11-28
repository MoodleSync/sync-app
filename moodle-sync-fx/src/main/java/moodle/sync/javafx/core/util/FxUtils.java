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

package moodle.sync.javafx.core.util;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import moodle.sync.core.view.Action;

public final class FxUtils {

	public static Parent load(String fxmlPath, ResourceBundle resources, Object controller, Object root) {
		URL fxmlURL = FxUtils.class.getResource(fxmlPath);

		FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL, resources);
		fxmlLoader.setController(controller);
		fxmlLoader.setRoot(root);

		Parent parent;

		try {
			parent = fxmlLoader.load();
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return parent;
	}

	public static void callback(Runnable callback) {
		if (nonNull(callback)) {
			callback.run();
		}
	}

	/**
	 * Run this Runnable in the JavaFX Application Thread. This method can be
	 * called whether or not the current thread is the JavaFX Application
	 * Thread.
	 *
	 * @param runnable The code to be executed in the JavaFX Application Thread.
	 */
	public static void invoke(Runnable runnable) {
		if (isNull(runnable)) {
			return;
		}

		try {
			if (Platform.isFxApplicationThread()) {
				runnable.run();
			}
			else {
				Platform.runLater(runnable);
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void bindAction(ButtonBase button, Action action) {
		requireNonNull(button);
		requireNonNull(action);

		button.addEventHandler(ActionEvent.ACTION, event -> action.execute());
	}
}
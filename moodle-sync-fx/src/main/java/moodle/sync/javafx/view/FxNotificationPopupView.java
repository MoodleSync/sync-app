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

package moodle.sync.javafx.view;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import moodle.sync.core.geometry.Position;
import moodle.sync.core.view.Action;
import moodle.sync.core.view.NotificationPopupView;
import moodle.sync.javafx.control.NotificationPane;

import javax.inject.Inject;


public class FxNotificationPopupView extends NotificationPane implements NotificationPopupView {

    private Action closeAction;

    private Position position;


    @Inject
    public FxNotificationPopupView(ResourceBundle resources) {
        super(resources);
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setOnClose(Action action) {
        this.closeAction = Action.concatenate(closeAction, action);
    }

    @FXML
    protected void initialize() {
        super.initialize();

        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        getStylesheets().add(getClass().getResource("/resources/css/notification-popup.css").toExternalForm());
    }

}
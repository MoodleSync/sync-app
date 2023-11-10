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

package moodle.sync.javafx.inject;

import javafx.util.Builder;

import moodle.sync.core.inject.Injector;
import moodle.sync.core.presenter.Presenter;
import moodle.sync.core.view.View;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ViewPresenterBuilder implements Builder<View> {

    private final static Logger LOG = LogManager.getLogger(ViewPresenterBuilder.class);

    private final Injector injector;

    private final Class<? extends Presenter<?>> presenterClass;


    public ViewPresenterBuilder(Injector injector, Class<? extends Presenter<?>> presenterClass) {
        this.injector = injector;
        this.presenterClass = presenterClass;
    }

    @Override
    public View build() {
        if (!Presenter.class.equals(presenterClass)) {
            Presenter<?> presenter = injector.getInstance(presenterClass);

            try {
                presenter.initialize();
            }
            catch (Exception e) {
                LOG.error("Initialize presenter failed", e);
            }

            return presenter.getView();
        }

        return null;
    }

}
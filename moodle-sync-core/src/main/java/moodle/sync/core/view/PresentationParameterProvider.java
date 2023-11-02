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

package moodle.sync.core.view;

import moodle.sync.core.app.configuration.Configuration;
import moodle.sync.core.beans.ChangeListener;
import moodle.sync.core.graphics.Color;
import moodle.sync.core.model.Page;
import moodle.sync.core.model.listener.ParameterChangeListener;
import moodle.sync.core.view.PresentationParameter;
import moodle.sync.core.view.ViewType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import static java.util.Objects.nonNull;

/**
 * Manages a set of {@code PresentationParameter}s and notifies listeners
 * whenever a parameter has changed. Usually a {@code PresentationParameterProvider}
 * exists for each {@link ViewType}.
 *
 * @author Alex Andres
 * @author Tobias
 */
public class PresentationParameterProvider implements PropertyChangeListener {

	private final Map<Page, PresentationParameter> parameter = new HashMap<>();

	private final List<ParameterChangeListener> listener = new ArrayList<>();

	private final Configuration config;


	public PresentationParameterProvider(Configuration config) {
		this.config = config;

		ChangeListener<Color> colorListener = (observable, oldValue, newValue) -> {
			notifyAllParameterChangeListeners();
		};
		ChangeListener<Double> gridIntervalListener = (observable, oldValue, newValue) -> {
			notifyAllParameterChangeListeners();
		};
		ChangeListener<Boolean> gridLineListener = (observable, oldValue, newValue) -> {
			notifyAllParameterChangeListeners();
		};

		config.getWhiteboardConfig().gridColorProperty().addListener(colorListener);
		config.getWhiteboardConfig().horizontalLinesIntervalProperty().addListener(gridIntervalListener);
		config.getWhiteboardConfig().horizontalLinesVisibleProperty().addListener(gridLineListener);
		config.getWhiteboardConfig().verticalLinesIntervalProperty().addListener(gridIntervalListener);
		config.getWhiteboardConfig().verticalLinesVisibleProperty().addListener(gridLineListener);
		config.getWhiteboardConfig().backgroundColorProperty().addListener(colorListener);
	}

	/**
	 * Gets the PresentationParameters for a Page
	 * 
	 * @param page
	 *
	 * @return The PresentationParameter for p
	 */
	public PresentationParameter getParameter(Page page) {
		if (page == null) {
			return null;
		}
		
		PresentationParameter param = parameter.get(page);

		// If there isn't any, create a new one.
		if (param == null) {
			param = new PresentationParameter(config, page);
			setParameter(param);
		}
		return param;
	}

	public void clearParameter(Page p) {
		parameter.remove(p);
	}

	/**
	 * Gets all PresentationParameters
	 * 
	 * @return
	 */
	public Collection<PresentationParameter> getAllPresentationParameters() {
		return parameter.values();
	}

	/**
	 * Notify all listeners
	 */
	public void notifyAllParameterChangeListeners() {
		for (ParameterChangeListener l : listener) {
			PresentationParameter param = getParameter(l.forPage());

			if (nonNull(param)) {
				l.parameterChanged(param.getPage(), param);
			}
		}
	}

	/**
	 * Sets the PresentationParameter for the associated page
	 * 
	 * @param para
	 */
	public void setParameter(PresentationParameter para) {
		parameter.put(para.getPage(), para);

		para.removeParameterChangeListener(this);
		para.addParameterChangeListener(this);
	}

	/**
	 * listener is notified whenever a PresentationParameter for listener's
	 * current page is changed
	 * 
	 * @param l
	 */
	public void addParameterChangeListener(ParameterChangeListener l) {
		listener.add(l);
	}

	public void removeParameterChangeListener(ParameterChangeListener l) {
		listener.remove(l);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Forward notifications from Parameter to interested listeners
		PresentationParameter para = (PresentationParameter) evt.getSource();
		for (ParameterChangeListener l : listener) {
			if (l.forPage() == para.getPage()) {
				l.parameterChanged(para.getPage(), para);
			}
		}
	}

	public void clearParameters() {
		parameter.clear();
	}

}

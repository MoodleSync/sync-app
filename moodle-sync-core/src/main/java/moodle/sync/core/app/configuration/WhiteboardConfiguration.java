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

package moodle.sync.core.app.configuration;

import moodle.sync.core.beans.BooleanProperty;
import moodle.sync.core.beans.DoubleProperty;
import moodle.sync.core.beans.ObjectProperty;
import moodle.sync.core.graphics.Color;

/**
 * The WhiteboardConfiguration specifies whiteboard related properties.
 *
 * @author Alex Andres
 */
public class WhiteboardConfiguration {

	/** The background color of the whiteboard. */
	private final ObjectProperty<Color> backgroundColor = new ObjectProperty<>(Color.WHITE);

	/** Defines the vertical line spacing of the grid. */
	private final DoubleProperty verticalLinesInterval = new DoubleProperty(0.5);

	/** Indicates whether vertical lines of the grid are visible. */
	private final BooleanProperty verticalLinesVisible = new BooleanProperty(true);

	/** Defines the horizontal line spacing of the grid. */
	private final DoubleProperty horizontalLinesInterval = new DoubleProperty(0.5);

	/** Indicates whether horizontal lines of the grid are visible. */
	private final BooleanProperty horizontalLinesVisible = new BooleanProperty(true);

	/** The grid color. */
	private final ObjectProperty<Color> gridColor = new ObjectProperty<>(new Color(230, 230, 230));

	/** Indicates whether to show the grid on connected displays. */
	private final BooleanProperty showGridOnDisplays = new BooleanProperty(false);

	/** Indicates whether to show the grid automatically. */
	private final BooleanProperty showGridAutomatically = new BooleanProperty(false);


	/**
	 * Obtain background color of the whiteboard.
	 *
	 * @return the background color.
	 */
	public Color getBackgroundColor() {
		return backgroundColor.get();
	}

	/**
	 * Set the new background color of the whiteboard.
	 *
	 * @param color The new background color to set.
	 */
	public void setBackgroundColor(Color color) {
		this.backgroundColor.set(color);
	}

	/**
	 * Obtain background color property.
	 *
	 * @return background color property.
	 */
	public ObjectProperty<Color> backgroundColorProperty() {
		return backgroundColor;
	}

	/**
	 * Check whether vertical lines of the grid are visible.
	 *
	 * @return {@code true} if vertical lines of the grid should be visible,
	 * otherwise {@code false}.
	 */
	public Boolean getVerticalLinesVisible() {
		return verticalLinesVisible.get();
	}

	/**
	 * Set whether vertical lines of the grid should be visible.
	 *
	 * @param visible True to enable vertical lines, false otherwise.
	 */
	public void setVerticalLinesVisible(boolean visible) {
		this.verticalLinesVisible.set(visible);
	}

	/**
	 * Obtain the vertical lines visible property.
	 *
	 * @return the vertical lines visible property.
	 */
	public BooleanProperty verticalLinesVisibleProperty() {
		return verticalLinesVisible;
	}

	/**
	 * Obtain the vertical line spacing of the grid.
	 *
	 * @return the vertical line spacing of the grid.
	 */
	public Double getVerticalLinesInterval() {
		return verticalLinesInterval.get();
	}

	/**
	 * Set the vertical line spacing of the grid.
	 *
	 * @param spacing The vertical line spacing of the grid.
	 */
	public void setVerticalLinesInterval(double spacing) {
		this.verticalLinesInterval.set(spacing);
	}

	/**
	 * Obtain the vertical line spacing property.
	 *
	 * @return the vertical line spacing property.
	 */
	public DoubleProperty verticalLinesIntervalProperty() {
		return verticalLinesInterval;
	}

	/**
	 * Check whether horizontal lines of the grid are visible.
	 *
	 * @return {@code true} if horizontal lines of the grid should be visible,
	 * otherwise {@code false}.
	 */
	public Boolean getHorizontalLinesVisible() {
		return horizontalLinesVisible.get();
	}

	/**
	 * Set whether horizontal lines of the grid should be visible.
	 *
	 * @param visible True to enable horizontal lines, false otherwise.
	 */
	public void setHorizontalLinesVisible(boolean visible) {
		this.horizontalLinesVisible.set(visible);
	}

	/**
	 * Obtain the horizontal lines visible property.
	 *
	 * @return the horizontal lines visible property.
	 */
	public BooleanProperty horizontalLinesVisibleProperty() {
		return horizontalLinesVisible;
	}

	/**
	 * Obtain the horizontal line spacing of the grid.
	 *
	 * @return the horizontal line spacing of the grid.
	 */
	public Double getHorizontalLinesInterval() {
		return horizontalLinesInterval.get();
	}

	/**
	 * Set the horizontal line spacing of the grid.
	 *
	 * @param spacing The horizontal line spacing of the grid.
	 */
	public void setHorizontalLinesInterval(double spacing) {
		this.horizontalLinesInterval.set(spacing);
	}

	/**
	 * Obtain the horizontal line spacing property.
	 *
	 * @return the horizontal line spacing property.
	 */
	public DoubleProperty horizontalLinesIntervalProperty() {
		return horizontalLinesInterval;
	}

	/**
	 * Obtain the grid color.
	 *
	 * @return the grid color.
	 */
	public Color getGridColor() {
		return gridColor.get();
	}

	/**
	 * Set the new grid color.
	 *
	 * @param color The new color to set.
	 */
	public void setGridColor(Color color) {
		this.gridColor.set(color);
	}

	/**
	 * Obtain the grid color property.
	 *
	 * @return the grid color property.
	 */
	public ObjectProperty<Color> gridColorProperty() {
		return gridColor;
	}

	/**
	 * Check whether to show the grid on connected displays.
	 *
	 * @return {@code true} to show the grid, otherwise {@code false}.
	 */
	public Boolean getShowGridOnDisplays() {
		return showGridOnDisplays.get();
	}

	/**
	 * Set whether to show the grid on connected displays.
	 *
	 * @param show True to show the grid, false otherwise.
	 */
	public void setShowGridOnDisplays(boolean show) {
		this.showGridOnDisplays.set(show);
	}

	/**
	 * Obtain the show grid property.
	 *
	 * @return the show grid property.
	 */
	public BooleanProperty showGridOnDisplaysProperty() {
		return showGridOnDisplays;
	}

	/**
	 * Check whether to show the grid automatically
	 *
	 * @return {@code true} to show the grid automatically.
	 */
	public Boolean getShowGridAutomatically() {
		return showGridAutomatically.get();
	}

	/**
	 * Set whether to show the grid automatically.
	 *
	 * @param show True to show the grid automatically.
	 */
	public void setShowGridAutomatically(boolean show) {
		this.showGridAutomatically.set(show);
	}

	/**
	 * Obtain the show grid automatically property.
	 *
	 * @return the show-grid-auto property.
	 */
	public BooleanProperty showGridAutomaticallyProperty() {
		return showGridAutomatically;
	}
}

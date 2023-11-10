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

import moodle.sync.core.app.Theme;
import moodle.sync.core.beans.BooleanProperty;
import moodle.sync.core.beans.DoubleProperty;
import moodle.sync.core.beans.ObjectProperty;
import moodle.sync.core.beans.StringProperty;
import moodle.sync.core.geometry.Dimension2D;
import moodle.sync.core.util.ObservableHashMap;

import java.util.Locale;

/**
 * The Configuration specifies application wide properties. Context specific
 * properties are encapsulated in the respective separate configuration classes.
 *
 * @author Alex Andres
 */
public class Configuration {

	/** The name of the application. */
	private final StringProperty applicationName = new StringProperty();

	/** The theme of the UI of the application. */
	private final ObjectProperty<Theme> theme = new ObjectProperty<>();

	/** The locale of the application. */
	private final ObjectProperty<Locale> locale = new ObjectProperty<>();

	/** Indicates whether to check for a new version of the application. */
	private final BooleanProperty checkNewVersion = new BooleanProperty();

	/** The UI control size of the application. */
	private final DoubleProperty uiControlSize = new DoubleProperty();

	/** Indicates whether to open the application window maximized. */
	private final BooleanProperty startMaximized = new BooleanProperty();

	/** Indicates whether to open the application in fullscreen mode. */
	private final BooleanProperty startFullscreen = new BooleanProperty();

	/** Indicates whether to use native mouse input instead of pen/stylus input. */
	private final BooleanProperty useMouseInput = new BooleanProperty();

	/** Indicates whether to enable a virtual keyboard on tablet devices. */
	private final BooleanProperty tabletMode = new BooleanProperty();

	/** Enables/disables advanced settings visible in the settings UI view. */
	private final BooleanProperty advancedUIMode = new BooleanProperty();

	/** Hides/shows UI elements, like the menu, in fullscreen mode. */
	private final BooleanProperty extendedFullscreen = new BooleanProperty();

	/** Defines the extended drawing area of a page. */
	private final ObjectProperty<Dimension2D> extendPageDimension = new ObjectProperty<>();

	/** The mapping of a filesystem path to a related context. */
	private final ObservableHashMap<String, String> contextPaths = new ObservableHashMap<>();



	/**
	 * Obtain the name of the application.
	 *
	 * @return the application name.
	 */
	public String getApplicationName() {
		return applicationName.get();
	}

	/**
	 * Set the name of the application.
	 *
	 * @param name The application name.
	 */
	public void setApplicationName(String name) {
		this.applicationName.set(name);
	}

	/**
	 * Obtain the application name property.
	 *
	 * @return the application name property.
	 */
	public ObjectProperty<String> applicationNameProperty() {
		return applicationName;
	}

	/**
	 * Obtain the current theme of the UI of the application.
	 *
	 * @return the UI theme.
	 */
	public Theme getTheme() {
		return theme.get();
	}

	/**
	 * Set the new UI theme.
	 *
	 * @param theme The UI theme to set.
	 */
	public void setTheme(Theme theme) {
		this.theme.set(theme);
	}

	/**
	 * Obtain the theme property.
	 *
	 * @return the theme property.
	 */
	public ObjectProperty<Theme> themeProperty() {
		return theme;
	}

	/**
	 * Obtain the current locale of the application.
	 *
	 * @return the current locale.
	 */
	public Locale getLocale() {
		return locale.get();
	}

	/**
	 * Set the new locale of the application.
	 *
	 * @param locale The new locale to set.
	 */
	public void setLocale(Locale locale) {
		this.locale.set(locale);
	}

	/**
	 * Obtain the locale property.
	 *
	 * @return the locale property.
	 */
	public ObjectProperty<Locale> localeProperty() {
		return locale;
	}

	/**
	 * Obtain whether new version checking is enabled.
	 *
	 * @return {@code true} if version checking is enabled, otherwise {@code false}.
	 */
	public boolean getCheckNewVersion() {
		return checkNewVersion.get();
	}

	/**
	 * Set whether to check for new versions of the application.
	 *
	 * @param check True to check for new versions.
	 */
	public void setCheckNewVersion(boolean check) {
		this.checkNewVersion.set(check);
	}

	/**
	 * Obtain the property for new version checking.
	 *
	 * @return the new version checking property.
	 */
	public BooleanProperty checkNewVersionProperty() {
		return checkNewVersion;
	}

	/**
	 * Obtain the UI control size of the application.
	 *
	 * @return the UI control size.
	 */
	public double getUIControlSize() {
		return uiControlSize.get();
	}

	/**
	 * Set the new UI control size of the application.
	 *
	 * @param size The new UI control size.
	 */
	public void setUIControlSize(double size) {
		this.uiControlSize.set(size);
	}

	/**
	 * Obtain the UI control size property.
	 *
	 * @return the UI control size property.
	 */
	public DoubleProperty uiControlSizeProperty() {
		return uiControlSize;
	}

	/**
	 * Check whether to open the application window maximized.
	 *
	 * @return {@code true} if the application window should be opened maximized, otherwise {@code false}.
	 */
	public Boolean getStartMaximized() {
		return startMaximized.get();
	}

	/**
	 * Set whether to open the application window maximized.
	 *
	 * @param maximized True to open the application window maximized, false
	 *                  otherwise.
	 */
	public void setStartMaximized(boolean maximized) {
		this.startMaximized.set(maximized);
	}

	/**
	 * Obtain the start maximized property.
	 *
	 * @return the start maximized property.
	 */
	public BooleanProperty startMaximizedProperty() {
		return startMaximized;
	}

	/**
	 * Check whether to open the application window in fullscreen mode.
	 *
	 * @return {@code true} if the application window should be opened fullscreen, otherwise {@code false}.
	 */
	public Boolean getStartFullscreen() {
		return startFullscreen.get();
	}

	/**
	 * Set whether to open the application window in fullscreen mode.
	 *
	 * @param fullscreen True to open the application window fullscreen, false
	 *                   otherwise.
	 */
	public void setStartFullscreen(boolean fullscreen) {
		this.startFullscreen.set(fullscreen);
	}

	/**
	 * Obtain the start fullscreen property.
	 *
	 * @return the start fullscreen property.
	 */
	public BooleanProperty startFullscreenProperty() {
		return startFullscreen;
	}

	/**
	 * Check whether to use app native mouse input instead of the pen/stylus
	 * input.
	 *
	 * @return {@code true} if the application should use mouse input.
	 */
	public Boolean getUseMouseInput() {
		return useMouseInput.get();
	}

	/**
	 * Set whether to use app native mouse input instead of the pen/stylus
	 * input.
	 *
	 * @param useMouse True to use mouse input.
	 */
	public void setUseMouseInput(boolean useMouse) {
		this.useMouseInput.set(useMouse);
	}

	/**
	 * Obtain the mouse input property.
	 *
	 * @return the start mouse input property.
	 */
	public BooleanProperty useMouseInputProperty() {
		return useMouseInput;
	}

	/**
	 * Check whether to enable a virtual keyboard on tablet devices.
	 *
	 * @return {@code true} to enable a virtual keyboard, false otherwise.
	 */
	public Boolean getTabletMode() {
		return tabletMode.get();
	}

	/**
	 * Set whether to enable a virtual keyboard on tablet devices.
	 *
	 * @param enable True to enable a virtual keyboard, false otherwise.
	 */
	public void setTabletMode(boolean enable) {
		this.tabletMode.set(enable);
	}

	/**
	 * Obtain the tablet mode property.
	 *
	 * @return the tablet mode property.
	 */
	public BooleanProperty tabletModeProperty() {
		return tabletMode;
	}

	/**
	 * Check whether to hide/show UI elements, like the menu, in fullscreen
	 * mode.
	 *
	 * @return {@code true} if the extended fullscreen mode is enabled, otherwise {@code false}.
	 *
	 * @see #setExtendedFullscreen(boolean)
	 */
	public Boolean getExtendedFullscreen() {
		return extendedFullscreen.get();
	}

	/**
	 * Set whether to hide/show UI elements, like the menu, in fullscreen mode.
	 * <p>
	 * If the value is set to {@code true}, then the related UI elements must be
	 * hidden when fullscreen is activated, and shown again when leaving the
	 * fullscreen mode.
	 *
	 * @param enabled True to enable the extended fullscreen mode, false
	 *                otherwise.
	 */
	public void setExtendedFullscreen(boolean enabled) {
		this.extendedFullscreen.set(enabled);
	}

	/**
	 * Obtain the extended fullscreen mode property.
	 *
	 * @return the extended fullscreen mode property.
	 */
	public BooleanProperty extendedFullscreenProperty() {
		return extendedFullscreen;
	}

	/**
	 * Check whether to enable/disable advanced settings visible in the settings
	 * UI view.
	 *
	 * @return {@code true} if the advanced settings mode is enabled, otherwise {@code false}.
	 *
	 * @see #setAdvancedUIMode(Boolean)
	 */
	public Boolean getAdvancedUIMode() {
		return advancedUIMode.get();
	}

	/**
	 * Set whether to enable/disable advanced settings visible in the settings
	 * UI view.
	 * <p>
	 * If the value is set to {@code true}, then the advanced settings UI
	 * elements must be visible in the settings UI view, and hidden again when
	 * the advanced settings mode is disabled.
	 *
	 * @param enabled True to enable the advanced settings mode, false
	 *                otherwise.
	 */
	public void setAdvancedUIMode(Boolean enabled) {
		this.advancedUIMode.set(enabled);
	}

	/**
	 * Obtain the advanced settings mode property.
	 *
	 * @return the advanced settings mode property.
	 */
	public BooleanProperty advancedUIModeProperty() {
		return advancedUIMode;
	}

	/**
	 * Obtain the extended drawing area of a page.
	 *
	 * @return the new extended drawing area.
	 *
	 * @see #setExtendPageDimension(Dimension2D)
	 */
	public Dimension2D getExtendPageDimension() {
		return extendPageDimension.get();
	}

	/**
	 * Set the new extended drawing area of a page. The specified dimension
	 * defines how the page is scaled down in order to provide additional blank
	 * drawing area. The width and height of the specified dimension must be in
	 * the range of [0,1].
	 *
	 * @param dimension The new extended page dimension to set.
	 */
	public void setExtendPageDimension(Dimension2D dimension) {
		this.extendPageDimension.set(dimension);
	}

	/**
	 * Obtain the extended page dimension property.
	 *
	 * @return the extended page dimension property.
	 */
	public ObjectProperty<Dimension2D> extendPageDimensionProperty() {
		return extendPageDimension;
	}

	/**
	 * Returns the mapping of a filesystem path to a related context.
	 *
	 * @return The context to path mapping.
	 */
	public ObservableHashMap<String, String> getContextPaths() {
		return contextPaths;
	}



}

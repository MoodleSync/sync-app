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

package moodle.sync.core.util;


/**
 * The listener that is called when changes to the {@link moodle.sync.core.util.ObservableSet} occur.
 * 
 * @author Alex Andres
 *
 * @param <T>
 */
public interface SetChangeListener<T extends ObservableSet<?>> {

	/**
	 * Called whenever one or more elements in the Set have changed.
	 *
	 * @param set The changed Set.
	 */
	void setChanged(T set);
	
}

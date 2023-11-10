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

package moodle.sync.javafx.guice;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import moodle.sync.javafx.core.view.FxmlView;

public class FxmlViewMatcher extends AbstractMatcher<TypeLiteral<?>> {

    @Override
    public boolean matches(TypeLiteral<?> type) {
        if (type.getRawType().isAnnotationPresent(FxmlView.class)) {
            FxmlView viewAnnotation = type.getRawType().getAnnotation(FxmlView.class);
            String viewName = viewAnnotation.name();

            return !viewName.isEmpty() && !viewName.isBlank();
        }

        return false;
    }

}
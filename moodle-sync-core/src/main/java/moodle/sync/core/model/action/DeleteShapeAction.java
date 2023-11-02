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

package moodle.sync.core.model.action;

import moodle.sync.core.model.Page;
import moodle.sync.core.model.shape.Shape;
import moodle.sync.core.model.action.ShapeAction;



import java.util.List;

/**
 * Action that removes {@link Shape}s from a {@link Page} object.
 * 
 * @author Tobias
 * 
 */
public class DeleteShapeAction extends ShapeAction {

	/**
	 * Create a {@link DeleteShapeAction} with the specified page and adds the specified shape to the list of shapes.
	 *
	 * @param page The page.
	 * @param shape The shape.
	 */
	public DeleteShapeAction(Page page, Shape shape) {
		super(page);

		getShapes().add(shape);
	}

	/**
	 * Create a {@link DeleteShapeAction} with the specified page and adds the specified shapes to the list of shapes.
	 *
	 * @param page The page.
	 * @param shapes The shapes.
	 */
	public DeleteShapeAction(Page page, List<Shape> shapes) {
		super(page);

		getShapes().addAll(shapes);
	}

	@Override
	public void execute() {
		for (Shape s : getShapes()) {
			getPage().removeShape(s);
		}
	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public void undo() {
		getPage().addShapes(getShapes());
	}

}

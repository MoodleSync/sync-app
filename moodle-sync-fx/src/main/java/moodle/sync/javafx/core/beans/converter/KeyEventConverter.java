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

package moodle.sync.javafx.core.beans.converter;


import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;
import java.util.Map;

import moodle.sync.core.beans.Converter;


public class KeyEventConverter implements Converter<moodle.sync.core.input.KeyEvent, KeyEvent> {

    public static final KeyEventConverter INSTANCE = new KeyEventConverter();

    private static final Map<Integer, KeyCode> codeMap;

    static {
        KeyCode[] values = KeyCode.values();

        codeMap = new HashMap<>(values.length);

        for (KeyCode c : values) {
            codeMap.put(c.getCode(), c);
        }
    }


    @Override
    public KeyEvent to(moodle.sync.core.input.KeyEvent event) {
        moodle.sync.core.input.KeyEvent.EventType eventType = event.getEventType();
        int modifiers = event.getModifiers();

        EventType<KeyEvent> type = null;
        KeyCode code = codeMap.get(event.getKeyCode());

        boolean altGrDown = (modifiers & moodle.sync.core.input.KeyEvent.ALT_GRAPH_MASK) != 0;
        boolean shiftDown = (modifiers & moodle.sync.core.input.KeyEvent.SHIFT_MASK) != 0;
        boolean controlDown = (modifiers & moodle.sync.core.input.KeyEvent.CTRL_MASK) != 0 || altGrDown;
        boolean altDown = (modifiers & moodle.sync.core.input.KeyEvent.ALT_MASK) != 0 || altGrDown;
        boolean metaDown = false;

        if (eventType == moodle.sync.core.input.KeyEvent.EventType.TYPED) {
            type = KeyEvent.KEY_TYPED;
        }
        else if (eventType == moodle.sync.core.input.KeyEvent.EventType.PRESSED) {
            type = KeyEvent.KEY_PRESSED;
        }
        else if (eventType == moodle.sync.core.input.KeyEvent.EventType.RELEASED) {
            type = KeyEvent.KEY_RELEASED;
        }

        return new KeyEvent(type, null, null, code, shiftDown, controlDown, altDown, metaDown);
    }

    @Override
    public moodle.sync.core.input.KeyEvent from(KeyEvent event) {
        EventType<KeyEvent> type = event.getEventType();
        int code = event.getCode().getCode();
        int modifiers = 0;
        moodle.sync.core.input.KeyEvent.EventType eventType;

        if (event.isAltDown()) {
            modifiers |= moodle.sync.core.input.KeyEvent.ALT_MASK;
        }
        if (event.isAltDown() && event.getCode() == KeyCode.ALT_GRAPH) {
            modifiers |= moodle.sync.core.input.KeyEvent.ALT_GRAPH_MASK;
        }
        if (event.isControlDown()) {
            modifiers |= moodle.sync.core.input.KeyEvent.CTRL_MASK;
        }
        if (event.isShiftDown()) {
            modifiers |= moodle.sync.core.input.KeyEvent.SHIFT_MASK;
        }

        if (type == KeyEvent.KEY_TYPED) {
            eventType = moodle.sync.core.input.KeyEvent.EventType.TYPED;
        }
        else if (type == KeyEvent.KEY_PRESSED) {
            eventType = moodle.sync.core.input.KeyEvent.EventType.PRESSED;
        }
        else if (type == KeyEvent.KEY_RELEASED) {
            eventType = moodle.sync.core.input.KeyEvent.EventType.RELEASED;
        }
        else {
            eventType = moodle.sync.core.input.KeyEvent.EventType.TYPED;
        }

        return new moodle.sync.core.input.KeyEvent(code, modifiers, eventType);
    }

}
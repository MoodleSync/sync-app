package moodle.sync.input;

import moodle.sync.core.input.KeyCode;
import moodle.sync.core.input.KeyEvent;

public enum Shortcut {

	APP_CLOSE(KeyCode.Q, KeyEvent.CTRL_MASK),

	CLOSE_VIEW(KeyCode.ESCAPE);


	private final KeyEvent keyEvent;


	Shortcut(KeyCode code) {
		this.keyEvent = new KeyEvent(code.getCode());
	}

	Shortcut(KeyCode code, int modifiers) {
		this.keyEvent = new KeyEvent(code.getCode(), modifiers);
	}

	public KeyEvent getKeyEvent() {
		return keyEvent;
	}

	public boolean match(KeyEvent event) {
		return keyEvent.equals(event);
	}

}

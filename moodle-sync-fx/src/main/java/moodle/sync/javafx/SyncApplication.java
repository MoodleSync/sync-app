package moodle.sync.javafx;

import moodle.sync.core.app.ApplicationFactory;
import moodle.sync.javafx.app.JavaFxApplication;

public class SyncApplication extends JavaFxApplication {

	/**
	 * The entry point of the application. This method calls the static
	 * {@link #launch(String[])} method to fire up the application.
	 *
	 * @param args the main method's arguments.
	 */
	public static void main(String[] args) {
		SyncApplication.launch(args);
	}

	@Override
	public ApplicationFactory createApplicationFactory() {
		return new SyncFxFactory();
	}
}

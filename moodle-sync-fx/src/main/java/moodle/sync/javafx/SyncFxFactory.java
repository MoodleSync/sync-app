package moodle.sync.javafx;

import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.app.ApplicationFactory;
import moodle.sync.core.inject.GuiceInjector;
import moodle.sync.core.inject.Injector;
import moodle.sync.javafx.inject.ApplicationModule;
import moodle.sync.javafx.inject.ViewModule;
import moodle.sync.presenter.MainPresenter;

public class SyncFxFactory implements ApplicationFactory {

	private final Injector injector;


	public SyncFxFactory() {
		injector = new GuiceInjector(new ApplicationModule(), new ViewModule());
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return injector.getInstance(ApplicationContext.class);
	}

	@Override
	public moodle.sync.core.presenter.MainPresenter<?> getStartPresenter() {
		return injector.getInstance(MainPresenter.class);
	}
}

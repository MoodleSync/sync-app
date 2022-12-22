package moodle.sync.javafx;

import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.app.ApplicationFactory;
import org.lecturestudio.core.inject.GuiceInjector;
import org.lecturestudio.core.inject.Injector;

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
	public org.lecturestudio.core.presenter.MainPresenter<?> getStartPresenter() {
		return injector.getInstance(MainPresenter.class);
	}
}

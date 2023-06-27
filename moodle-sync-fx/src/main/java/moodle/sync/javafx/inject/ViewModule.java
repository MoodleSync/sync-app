package moodle.sync.javafx.inject;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import javax.inject.Provider;

import javafx.util.BuilderFactory;

import moodle.sync.javafx.view.*;
import moodle.sync.view.*;

import org.lecturestudio.core.inject.DIViewContextFactory;
import org.lecturestudio.core.util.AggregateBundle;
import org.lecturestudio.core.view.*;
import org.lecturestudio.javafx.guice.FxmlViewLoader;
import org.lecturestudio.javafx.guice.FxmlViewMatcher;
import org.lecturestudio.javafx.view.*;
import org.lecturestudio.javafx.view.builder.DIBuilderFactory;

public class ViewModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(BuilderFactory.class).to(DIBuilderFactory.class);
		bind(ViewContextFactory.class).to(DIViewContextFactory.class);

		bind(DirectoryChooserView.class).to(FxDirectoryChooserView.class);
		bind(FileChooserView.class).to(FxFileChooserView.class);
		bind(NewVersionView.class).to(FxNewVersionView.class);
		bind(NotificationView.class).to(FxNotificationView.class);
		bind(NotificationPopupView.class).to(FxNotificationPopupView.class);
		bind(NotificationPopupManager.class).to(FxNotificationPopupManager.class);
		bind(ProgressView.class).to(FxProgressView.class);
		bind(ProgressDialogView.class).to(FxProgressDialogView.class);

		bind(MainView.class).to(FxMainView.class);
		bind(SettingsView.class).to(FxSettingsView.class);
		bind(TrainerStartView.class).to(FxTrainerStartView.class);


		Provider<AggregateBundle> resourceProvider = getProvider(AggregateBundle.class);
		Provider<BuilderFactory> builderProvider = getProvider(BuilderFactory.class);

		bindListener(new FxmlViewMatcher(), new TypeListener() {

			@Override
			public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
				encounter.register(FxmlViewLoader.getInstance(resourceProvider, builderProvider));
			}

		});
	}

}

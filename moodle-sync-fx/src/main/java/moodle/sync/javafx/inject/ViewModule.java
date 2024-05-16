package moodle.sync.javafx.inject;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import javax.inject.Provider;

import javafx.util.BuilderFactory;

import moodle.sync.core.inject.DIViewContextFactory;
import moodle.sync.core.util.AggregateBundle;
import moodle.sync.core.view.*;
import moodle.sync.core.view.ProgressView;
import moodle.sync.javafx.guice.FxmlViewLoader;
import moodle.sync.javafx.guice.FxmlViewMatcher;
import moodle.sync.javafx.view.*;
import moodle.sync.view.*;
import moodle.sync.view.builder.DIBuilderFactory;

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
		bind(PanoptoSettingsView.class).to(FxPanoptoSettingsView.class);
		bind(FtpSettingsView.class).to(FxFtpSettingsView.class);

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

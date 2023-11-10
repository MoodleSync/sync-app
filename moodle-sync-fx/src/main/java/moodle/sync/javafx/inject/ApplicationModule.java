package moodle.sync.javafx.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.inject.Singleton;

import moodle.sync.core.app.AppDataLocator;
import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.app.LocaleProvider;
import moodle.sync.core.app.configuration.Configuration;
import moodle.sync.core.app.configuration.ConfigurationService;
import moodle.sync.core.app.configuration.JsonConfigurationService;
import moodle.sync.core.app.dictionary.Dictionary;
import moodle.sync.core.bus.ApplicationBus;
import moodle.sync.core.bus.AudioBus;
import moodle.sync.core.bus.EventBus;
import moodle.sync.core.util.DirUtils;
import moodle.sync.core.web.service.MoodleService;
import moodle.sync.core.util.AggregateBundle;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import moodle.sync.core.config.DefaultConfiguration;
import moodle.sync.core.config.MoodleSyncConfiguration;
import moodle.sync.core.context.MoodleSyncContext;

public class ApplicationModule extends AbstractModule {

	private final static Logger LOG = LogManager.getLogger(ApplicationModule.class);
	
	private static final AppDataLocator LOCATOR = new AppDataLocator("MoodleSync");
	
	private static final File CONFIG_FILE = new File(LOCATOR.toAppDataPath("config.json"));


	@Override
	protected void configure() {
		bind(ApplicationContext.class).to(MoodleSyncContext.class);

		Properties streamProps = new Properties();

		try {
			streamProps.load(getClass().getClassLoader()
					.getResourceAsStream("resources/moodle-api.properties"));

			Names.bindProperties(binder(), streamProps);
		}
		catch (IOException e) {
			LOG.error("Load stream properties failed", e);
		}
	}

	@Provides
	@Singleton
	ResourceBundle createResourceBundle(Configuration config) throws Exception {
		LocaleProvider localeProvider = new LocaleProvider();
		Locale locale = localeProvider.getBestSupported(config.getLocale());

		return new AggregateBundle(locale, "resources.i18n.core", "resources.i18n.dict");
	}

	@Provides
	@Singleton
	AggregateBundle createAggregateBundle(ResourceBundle resourceBundle) {
		return (AggregateBundle) resourceBundle;
	}

	@Provides
	@Singleton
	Dictionary provideDictionary(ResourceBundle resourceBundle) {
		return new Dictionary() {

			@Override
			public String get(String key) throws NullPointerException {
				return resourceBundle.getString(key);
			}

			@Override
			public boolean contains(String key) {
				return resourceBundle.containsKey(key);
			}
		};
	}

	@Provides
	@Singleton
	MoodleService createMoodleService(Configuration config){
		MoodleSyncConfiguration syncConfig = (MoodleSyncConfiguration) config;
		return new MoodleService(syncConfig.moodleUrlProperty());
	}

	@Provides
	@Singleton
	MoodleSyncContext createApplicationContext(Configuration config,
			Dictionary dict) throws Exception {
		EventBus eventBus = ApplicationBus.get();
		EventBus audioBus = AudioBus.get();

		return new MoodleSyncContext(LOCATOR, CONFIG_FILE, config, dict,
				eventBus, audioBus);
	}

	@Provides
	@Singleton
	ConfigurationService<MoodleSyncConfiguration> provideConfigurationService() {
		ConfigurationService<MoodleSyncConfiguration> configService = null;

		try {
			configService = new JsonConfigurationService<>();
		}
		catch (Exception e) {
			LOG.error("Create configuration service failed.", e);
		}

		return configService;
	}

	@Provides
	@Singleton
	Configuration provideConfiguration(
			ConfigurationService<MoodleSyncConfiguration> configService) {
		MoodleSyncConfiguration configuration = null;

		try {
			DirUtils.createIfNotExists(Paths.get(LOCATOR.getAppDataPath()));

			if (!CONFIG_FILE.exists()) {
				// Create configuration with default values.
				configuration = new DefaultConfiguration();

				configService.save(CONFIG_FILE, configuration);
			}
			else {
				configuration = configService.load(CONFIG_FILE,
						MoodleSyncConfiguration.class);
			}

			// Set system default locale.
			LocaleProvider localeProvider = new LocaleProvider();
			configuration.setLocale(localeProvider.getBestSupported(
					configuration.getLocale()));
		}
		catch (Exception e) {
			LOG.error("Create configuration failed", e);
		}

		return configuration;
	}


}

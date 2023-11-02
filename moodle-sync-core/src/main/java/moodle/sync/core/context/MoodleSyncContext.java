package moodle.sync.core.context;

import java.io.File;

import javax.inject.Inject;

import moodle.sync.core.app.AppDataLocator;
import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.app.configuration.Configuration;
import moodle.sync.core.app.configuration.ConfigurationService;
import moodle.sync.core.app.configuration.JsonConfigurationService;
import moodle.sync.core.app.dictionary.Dictionary;
import moodle.sync.core.bus.EventBus;

public class MoodleSyncContext extends ApplicationContext {

	private final File configFile;


	@Inject
	public MoodleSyncContext(AppDataLocator dataLocator, File configFile,
							 Configuration config, Dictionary dict, EventBus eventBus,
							 EventBus audioBus) {
		super(dataLocator, config, dict, eventBus, audioBus);

		this.configFile = configFile;
	}

	@Override
	public void saveConfiguration() throws Exception {
		ConfigurationService<Configuration> configService = new JsonConfigurationService<>();
		configService.save(configFile, getConfiguration());
	}
}

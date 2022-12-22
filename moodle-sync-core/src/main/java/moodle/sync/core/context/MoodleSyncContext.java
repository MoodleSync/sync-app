package moodle.sync.core.context;

import java.io.File;

import javax.inject.Inject;

import org.lecturestudio.core.app.AppDataLocator;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.core.app.configuration.Configuration;
import org.lecturestudio.core.app.configuration.ConfigurationService;
import org.lecturestudio.core.app.configuration.JsonConfigurationService;
import org.lecturestudio.core.app.dictionary.Dictionary;
import org.lecturestudio.core.bus.EventBus;

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

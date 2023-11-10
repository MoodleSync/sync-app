package moodle.sync.cli.log;

import moodle.sync.core.app.AppDataLocator;
import moodle.sync.core.log.Log4jXMLConfigurationFactory;
import moodle.sync.core.model.VersionInfo;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.Plugin;


@Plugin(name = "Log4jConfigurationFactory", category = "ConfigurationFactory")
@Order(10)
public class Log4jConfigurationFactory extends Log4jXMLConfigurationFactory {

	public Log4jConfigurationFactory() {
		AppDataLocator dataLocator = new AppDataLocator("MoodleSyncCli");

		System.setProperty("logAppVersion", VersionInfo.getAppVersion());
		System.setProperty("logFilePath", dataLocator.getAppDataPath());
	}

}

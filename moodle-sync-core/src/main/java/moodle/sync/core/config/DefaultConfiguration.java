package moodle.sync.core.config;

import moodle.sync.core.fileserver.FileServerType;
import moodle.sync.core.fileserver.LanguageSupport;

import java.util.Locale;

/**
 * This Class offers the possibility to generate a default configuration.
 */
public class DefaultConfiguration extends MoodleSyncConfiguration {

	public DefaultConfiguration() {
		setApplicationName("MoodleSync");
		setLocale(Locale.getDefault());
		setCheckNewVersion(true);
		setUIControlSize(9);
		setStartMaximized(false);
		setAdvancedUIMode(false);
		setSyncRootPath(System.getProperty("user.dir"));
		setMoodleUrl("https://localhost");
		setFormatsMoodle("pdf,png,pptx,docx");
		setRecentFileServerType(LanguageSupport.getDefaultFileserver(Locale.getDefault()));
		setFtpConfiguration(new FileserverFTPConfiguration());
		getFtpConfiguration().setFtpFormats("avi,mp4,mpg,wmv,mov");
		setPanoptoConfiguration(new FileserverPanoptoConfiguration());
		getPanoptoConfiguration().setPanoptoFormats("avi,mp4,mpg,wmv,mov");
		getFtpConfiguration().setFtpPort("21");
	}

}

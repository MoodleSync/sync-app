package moodle.sync.cli.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import moodle.sync.core.app.LocaleProvider;
import moodle.sync.core.app.dictionary.Dictionary;
import moodle.sync.core.beans.StringProperty;
import moodle.sync.core.util.AggregateBundle;
import moodle.sync.core.web.service.MoodleService;


import javax.inject.Singleton;
import java.util.Locale;
import java.util.ResourceBundle;

public class ApplicationModule extends AbstractModule {

	@Provides
	@Singleton
	ResourceBundle createResourceBundle() throws Exception {
		LocaleProvider localeProvider = new LocaleProvider();
		Locale locale = localeProvider.getBestSupported(Locale.getDefault());

		return new AggregateBundle(locale, "resources.i18n.core", "resources.i18n.dict", "resources.i18n.cli");
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
	MoodleService createMoodleService(){
		return new MoodleService(new StringProperty("http://localhost/"));
	}

}

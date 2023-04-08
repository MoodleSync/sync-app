package moodle.sync.view;

import org.lecturestudio.core.beans.BooleanProperty;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.beans.StringProperty;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.View;

import java.util.List;
import java.util.Locale;

/**
 * Interface defining the functions of the "settings-page".
 *
 * @author Daniel Schröter
 */
public interface SettingsView extends View {

    void setOnExit(Action action);

    void setLocale(ObjectProperty<Locale> locale);

    void setLocales(List<Locale> locales);

    void setMoodleField(StringProperty moodleURL);

    void setFormatsMoodle(StringProperty moodleformats);

    void setFormatsFileserver(StringProperty fileserverformats);

    void setFtpField(StringProperty ftpURL);

    void setFtpPort(StringProperty ftpPort);

    void setFtpUser(StringProperty ftpUser);

    void setFtpPassword(StringProperty ftpPassword);

    void setMoodleToken(StringProperty moodleToken);

    void setOnCheckToken(Action action);

    void setTokenValid(boolean valid);

    void setSyncRootPath(StringProperty path);

    void setSelectSyncRootPath(Action action);

    void setShowUnknownFormats(BooleanProperty unknownFormats);
}

package moodle.sync.view;

import java.util.List;
import java.util.Locale;

import moodle.sync.core.beans.BooleanProperty;
import moodle.sync.core.beans.ObjectProperty;
import moodle.sync.core.beans.StringProperty;
import moodle.sync.core.fileserver.FileServerClient;
import moodle.sync.core.fileserver.FileServerType;
import moodle.sync.core.view.Action;
import moodle.sync.core.view.View;

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

    void setFileserver(StringProperty client);

    void setFileservers(List<String> clients);

    void setMoodleToken(StringProperty moodleToken);

    void setOnCheckToken(Action action);

    void setTokenValid(boolean valid);

    void setSyncRootPath(StringProperty path);

    void setSelectSyncRootPath(Action action);

    void setShowUnknownFormats(BooleanProperty unknownFormats);

    void setPanopto(PanoptoSettingsView panoptoSettingsView);

    void setFtp(FtpSettingsView ftpSettingsView);

    void clearFileservers();
}

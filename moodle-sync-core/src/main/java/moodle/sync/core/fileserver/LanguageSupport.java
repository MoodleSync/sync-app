package moodle.sync.core.fileserver;

import java.util.Locale;

public class LanguageSupport {

    public static String getDefaultFileserver(Locale locale) {
        if(locale.equals(Locale.GERMAN) || locale.equals(Locale.GERMANY)) {
            return "Keiner";
        } else {
            return "None";
        }
    }
}

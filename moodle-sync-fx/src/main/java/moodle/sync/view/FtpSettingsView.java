package moodle.sync.view;

import moodle.sync.core.beans.StringProperty;
import moodle.sync.core.view.View;

public interface FtpSettingsView  extends View {

    void setFormatsFTP(StringProperty ftpFormats);

    void setFtpField(StringProperty ftpURL);

    void setFtpPort(StringProperty ftpPort);

    void setFtpUser(StringProperty ftpUser);

    void setFtpPassword(StringProperty ftpPassword);

}

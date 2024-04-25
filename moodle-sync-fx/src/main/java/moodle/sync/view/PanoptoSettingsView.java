package moodle.sync.view;

import moodle.sync.core.beans.StringProperty;
import moodle.sync.core.view.Action;
import moodle.sync.core.view.View;

public interface PanoptoSettingsView extends View {

    void setPanoptoField(StringProperty panoptoURL);

    void setPanoptoClient(StringProperty panoptoClient);

    void setPanoptoSecret(StringProperty panoptoSecret);

    void setOnCheckPanopto(Action action);

    void setPanoptoValid(boolean valid);

    void setPanoptoDefaultFolder(StringProperty defaultFolder);

    void setFormatsPanopto(StringProperty panoptoformats);

}

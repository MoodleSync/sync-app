package moodle.sync.util;

import javafx.geometry.Pos;

import moodle.sync.core.app.ApplicationContext;
import moodle.sync.javafx.core.util.FxUtils;

import org.controlsfx.control.Notifications;


/**
 * Class used to display a popup when a download is finished.
 */
public class PopupUtil {

    public static void popUpDownload(ApplicationContext context) {
        FxUtils.invoke(() -> {
            Notifications notifications = Notifications.create().title(context.getDictionary().get("start.download.finish.title")).text(context.getDictionary().get("start.download.finish.message")).position(Pos.BOTTOM_RIGHT);
            notifications.showInformation();
        });
    }
}

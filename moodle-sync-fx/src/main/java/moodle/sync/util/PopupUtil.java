package moodle.sync.util;

import javafx.geometry.Pos;
import org.controlsfx.control.Notifications;
import org.lecturestudio.core.app.ApplicationContext;
import org.lecturestudio.javafx.util.FxUtils;

public class PopupUtil {

    public static void popUpDownload(ApplicationContext context) {
        FxUtils.invoke(() -> {
            Notifications notifications = Notifications.create().title(context.getDictionary().get("start.download.finish.title")).text(context.getDictionary().get("start.download.finish.message")).position(Pos.BOTTOM_RIGHT);
            notifications.showInformation();
        });
    }
}

package moodle.sync.view;

import java.util.function.Predicate;

import org.lecturestudio.core.geometry.Rectangle2D;
import org.lecturestudio.core.input.KeyEvent;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.core.view.View;
import org.lecturestudio.core.view.ViewLayer;

/**
 * Interface defining the functions of the "main-window".
 */
public interface MainView extends View {

    Rectangle2D getBounds();

    void close();

    void hide();

    void removeView(View view, ViewLayer layer);

    void showView(View view, ViewLayer layer);

    void setFullscreen(boolean fullscreen);

    void setOnKeyEvent(Predicate<KeyEvent> action);

    void setOnShown(Action action);

    void setOnClose(Action action);

}

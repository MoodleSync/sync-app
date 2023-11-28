package moodle.sync.view;

import java.util.function.Predicate;

import moodle.sync.core.input.KeyEvent;
import moodle.sync.core.geometry.Rectangle2D;
import moodle.sync.core.view.Action;
import moodle.sync.core.view.View;
import moodle.sync.core.view.ViewLayer;

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

package moodle.sync.javafx.view;

import static java.util.Objects.nonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.function.Predicate;

import javax.inject.Inject;

import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.app.Theme;
import moodle.sync.core.app.configuration.Configuration;
import moodle.sync.core.geometry.Rectangle2D;
import javafx.scene.input.KeyEvent;
import moodle.sync.core.view.Action;
import moodle.sync.core.view.View;
import moodle.sync.core.view.ViewLayer;

import moodle.sync.javafx.core.beans.converter.KeyEventConverter;
import moodle.sync.javafx.core.util.FxUtils;
import moodle.sync.javafx.core.view.FxmlView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import moodle.sync.view.MainView;

/**
 * Class implementing the functions of the "main-window".
 */
@FxmlView(name = "main-window")
public class FxMainView extends StackPane implements MainView {

	private final ApplicationContext context;

	private final Deque<Node> viewStack;

	private Predicate<moodle.sync.core.input.KeyEvent> keyAction;

	private Action shownAction;

	private Action closeAction;

	@FXML
	private BorderPane contentPane;


	@Inject
	public FxMainView(ApplicationContext context) {
		super();

		this.context = context;
		this.viewStack = new ArrayDeque<>();
	}

	@Override
	public Rectangle2D getBounds() {
		Window window = getScene().getWindow();

		return new Rectangle2D(window.getX(), window.getY(), window.getWidth(),
				window.getHeight());
	}

	@Override
	public void close() {
		// Fire close request in order to shut down appropriately.
		Window window = getScene().getWindow();
		window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	@Override
	public void hide() {
		FxUtils.invoke(() -> {
			Window window = getScene().getWindow();
			window.hide();
		});
	}

	@Override
	public void removeView(View view, ViewLayer layer) {
		if (layer == ViewLayer.Window) {
			return;
		}

		checkNodeView(view);

		Node nodeView = (Node) view;

		removeNode(nodeView);
	}

	@Override
	public void showView(View view, ViewLayer layer) {
		if (layer == ViewLayer.Window) {
			return;
		}

		checkNodeView(view);

		Node nodeView = (Node) view;

		switch (layer) {
			case Content:
				showNode(nodeView, true);
				break;

			case Dialog:
			case Notification:
				showNodeOnTop(nodeView);
				break;
		}
	}

	@Override
	public void setFullscreen(boolean fullscreen) {
		FxUtils.invoke(() -> {
			Stage stage = (Stage) getScene().getWindow();
			stage.setFullScreen(fullscreen);
		});
	}

	@Override
	public void setOnKeyEvent(Predicate<moodle.sync.core.input.KeyEvent> action) {
		this.keyAction = action;
	}

	@Override
	public void setOnShown(Action action) {
		this.shownAction = action;
	}

	@Override
	public void setOnClose(Action action) {
		this.closeAction = action;
	}

	private void onKeyEvent(KeyEvent event) {
		if (event.getEventType() == KeyEvent.KEY_PRESSED) {
			moodle.sync.core.input.KeyEvent keyEvent = KeyEventConverter.INSTANCE.from(event);

			if (nonNull(keyAction) && keyAction.test(keyEvent)) {
				event.consume();
			}
		}
	}

	private void removeNode(Node nodeView) {
		FxUtils.invoke(() -> {
			boolean removed = getChildren().remove(nodeView);

			if (!removed) {
				showNode(nodeView, false);
			}
		});
	}

	private void showNode(Node nodeView, boolean show) {
		Node currentView = contentPane.getCenter();
		boolean isSame = currentView == nodeView;

		if (show) {
			if (!isSame) {
				viewStack.push(nodeView);

				FxUtils.invoke(() -> {
					contentPane.setCenter(nodeView);
				});
			}
		}
		else if (isSame) {
			Node lastView = viewStack.pop();

			if (!viewStack.isEmpty()) {
				lastView = viewStack.pop();
			}

			showNode(lastView, true);
		}
	}

	private void showNodeOnTop(Node nodeView) {
		FxUtils.invoke(() -> {
			getChildren().add(nodeView);
		});
	}

	@FXML
	private void initialize() {
		Configuration config = context.getConfiguration();

		// Set application wide font size.
		setStyle(String.format(Locale.US, "-fx-font-size: %.2fpt;", config.getUIControlSize()));

		addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyEvent);

		config.themeProperty().addListener((observable, oldTheme, newTheme) -> {
			// Load new theme.
			loadTheme(newTheme);
			// Unload old theme.
			unloadTheme(oldTheme);
		});

		// Init view-stack with default node.
		viewStack.push(contentPane.getCenter());

		sceneProperty().addListener(new ChangeListener<>() {

			@Override
			public void changed(ObservableValue<? extends Scene> observableValue,
					Scene oldScene, Scene newScene) {
				if (nonNull(newScene)) {
					sceneProperty().removeListener(this);

					onSceneSet(newScene);
				}
			}
		});
	}

	private void loadTheme(Theme theme) {
		if (nonNull(theme) && nonNull(theme.getFile())) {
			String themeUrl = getClass().getResource(theme.getFile()).toExternalForm();
			getScene().getStylesheets().add(themeUrl);
		}
	}

	private void unloadTheme(Theme theme) {
		if (nonNull(theme.getFile())) {
			String themeUrl = getClass().getResource(theme.getFile()).toExternalForm();
			getScene().getStylesheets().remove(themeUrl);
		}
	}

	private void onSceneSet(Scene scene) {
		scene.windowProperty().addListener(new ChangeListener<>() {

			@Override
			public void changed(ObservableValue<? extends Window> observable,
					Window oldWindow, Window newWindow) {
				if (nonNull(newWindow)) {
					scene.windowProperty().removeListener(this);

					onStageSet((Stage) newWindow);
				}
			}
		});
	}

	private void onStageSet(Stage stage) {
		// It's imperative to load fxml-defined stylesheets prior to the user-defined theme
		// stylesheet, so the theme can override initial styles.
		getStylesheets().forEach(file -> {
			loadTheme(new Theme("defined", file));
		});
		// Remove loaded stylesheets to avoid additional loading by the scene itself.
		getStylesheets().clear();

		loadTheme(context.getConfiguration().getTheme());

		stage.setOnShown(event -> {
			executeAction(shownAction);
		});
		stage.setOnCloseRequest(event -> {
			// Consume event. Don't close the window yet.
			event.consume();

			executeAction(closeAction);
		});
	}

	private void checkNodeView(View view) {
		if (!Node.class.isAssignableFrom(view.getClass())) {
			throw new IllegalArgumentException("View expected to be a JavaFX Node");
		}
	}
}

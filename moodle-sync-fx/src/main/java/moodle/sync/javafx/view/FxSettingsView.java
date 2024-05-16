package moodle.sync.javafx.view;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.converter.IntegerStringConverter;
import moodle.sync.core.fileserver.FileServerClient;
import moodle.sync.core.fileserver.FileServerType;
import moodle.sync.presenter.SettingsPresenter;
import moodle.sync.util.UserInputValidations;
import moodle.sync.view.FtpSettingsView;
import moodle.sync.view.PanoptoSettingsView;
import moodle.sync.view.SettingsView;
import moodle.sync.core.beans.BooleanProperty;
import moodle.sync.core.beans.ObjectProperty;
import moodle.sync.core.beans.StringProperty;
import moodle.sync.core.view.Action;
import moodle.sync.javafx.core.beans.LectBooleanProperty;
import moodle.sync.javafx.core.beans.LectObjectProperty;
import moodle.sync.javafx.core.beans.LectStringProperty;
import moodle.sync.javafx.core.util.FxUtils;
import moodle.sync.javafx.core.view.FxView;
import moodle.sync.javafx.core.view.FxmlView;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

/**
 * Class implementing the functions of the "settings-page".
 *
 * @author Daniel Schröter
 */
@FxmlView(name = "main-settings", presenter = SettingsPresenter.class)
public class FxSettingsView extends VBox implements SettingsView, FxView {

    @FXML
    private VBox generalVbox;

    @FXML
    private Button closesettingsButton;
    @FXML
    private ComboBox<Locale> languageCombo;

    @FXML
    private TextField tokenField;

    @FXML
    private Button checkToken;

    @FXML
    private TextField syncRootPath;

    @FXML
    private TextField moodleField;

    @FXML
    private ComboBox fileserverCombo;

    @FXML
    private TextArea formatsMoodle;

    @FXML
    private Button syncRootPathButton;

    @FXML
    private CheckBox showUnknownFormats;

    @FXML
    private StackPane ftpSettingsPane;

    @FXML
    private StackPane panoptoSettingsPane;

    @FXML
    private Pane fileserverContainer;

    public FxSettingsView() {
        super();
    }

    /**
     * Exiting the "settings-page".
     *
     * @param action Users action.
     */
    @Override
    public void setOnExit(Action action) {
        FxUtils.bindAction(closesettingsButton, action);
    }

    /**
     * Setting the chosen locale property.
     *
     * @param locale chosen language.
     */
    @Override
    public void setLocale(ObjectProperty<Locale> locale) {
        languageCombo.valueProperty().bindBidirectional(new LectObjectProperty<>(locale));
    }

    /**
     * Sets the possible values of the languageCombo.
     *
     * @param locales possible languages.
     */
    @Override
    public void setLocales(List<Locale> locales) {
        FxUtils.invoke(() -> languageCombo.getItems().setAll(locales));
    }

    /**
     * Method used to display the Moodle URL. Includes a verifier.
     *
     * @param moodleURL Inserted Moodle URL.
     */
    @Override
    public void setMoodleField(StringProperty moodleURL) {
        moodleField.textProperty().bindBidirectional(new LectStringProperty(moodleURL));
        moodleField.textProperty().addListener(event -> {
            moodleField.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("error"),
                    !moodleField.getText().matches("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
            );
        });
    }

    /**
     * Input of the Moodle-token.
     *
     * @param moodleToken User input.
     */
    @Override
    public void setMoodleToken(StringProperty moodleToken) {
        tokenField.textProperty().bindBidirectional(new LectStringProperty(moodleToken));
        tokenField.textProperty().addListener(event -> {
            tokenField.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("error"),
                    (tokenField.getText().isEmpty())
            );
        });
    }

    /**
     * Method invokes a checking-operation for the inserted token.
     *
     * @param action start checking-operation.
     */
    @Override
    public void setOnCheckToken(Action action) {
        FxUtils.bindAction(checkToken, action);
    }

    /**
     * Marks the token-text-field if valid.
     *
     * @param valid boolean param.
     */
    @Override
    public void setTokenValid(boolean valid){
        tokenField.pseudoClassStateChanged(
                PseudoClass.getPseudoClass("error"),
                (!valid)
        );
        tokenField.pseudoClassStateChanged(
                PseudoClass.getPseudoClass("valid"),
                (valid)
        );
    }


    /**
     * Setting the chosen fileserver type property.
     *
     * @param type chosen fileserver type.
     */
    @Override
    public void setFileserver(StringProperty type) {
        FxUtils.invoke(() -> fileserverCombo.valueProperty().bindBidirectional(new LectObjectProperty<>(type)));
    }

    /**
     * Sets the possible values of the fileserver type.
     *
     * @param types possible fileserver types.
     */
    @Override
    public void setFileservers(List<String> types) {
        FxUtils.invoke(() -> fileserverCombo.getItems().setAll(types));
    }

    @Override
    public void setPanopto(PanoptoSettingsView panoptoSettingsView) {
        if (Node.class.isAssignableFrom(panoptoSettingsView.getClass())) {
            FxUtils.invoke(() -> fileserverContainer.getChildren().add((Node) panoptoSettingsView));
        }
    }

    @Override
    public void setFtp(FtpSettingsView ftpSettingsView) {
        if (Node.class.isAssignableFrom(ftpSettingsView.getClass())) {
            FxUtils.invoke(() -> fileserverContainer.getChildren().add((Node) ftpSettingsView));
        }
    }

    @Override
    public void clearFileservers() {
        FxUtils.invoke(() -> fileserverContainer.getChildren().clear());
    }

    /**
     * Input of formats used to upload to the Moodle-Platform.
     *
     * @param moodleformats User input.
     */
    @Override
    public void setFormatsMoodle(StringProperty moodleformats) {
        formatsMoodle.textProperty().bindBidirectional(new LectStringProperty(moodleformats));
    }

    /**
     * Input of the Root-Directory.
     *
     * @param path User input.
     */
    @Override
    public void setSyncRootPath(StringProperty path) {
        syncRootPath.textProperty().bindBidirectional(new LectStringProperty(path));
        syncRootPath.textProperty().addListener(event -> {
            boolean isDirectory;
            try{
                isDirectory = Files.isDirectory(Paths.get(syncRootPath.getText()));
            } catch (Exception e) {
                e.printStackTrace();
                isDirectory = false;
            }
            syncRootPath.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("error"),
                    !syncRootPath.getText().isEmpty() &&
                            !isDirectory
                    );

        });
    }

    /**
     * Opens the explorer.
     *
     * @param action User needs to click a button.
     */
    @Override
    public void setSelectSyncRootPath(Action action) {
        FxUtils.bindAction(syncRootPathButton, action);
    }

    /**
     * User can set if files with unknownFormats should be shown in the "sync-table".
     *
     * @param unknownFormats User Input CheckBox.
     */
    @Override
    public void setShowUnknownFormats(BooleanProperty unknownFormats) {
        showUnknownFormats.selectedProperty().bindBidirectional(new LectBooleanProperty(unknownFormats));
    }
}


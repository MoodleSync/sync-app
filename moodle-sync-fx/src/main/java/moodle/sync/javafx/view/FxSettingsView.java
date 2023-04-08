package moodle.sync.javafx.view;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import moodle.sync.presenter.SettingsPresenter;
import moodle.sync.util.UserInputValidations;
import moodle.sync.view.SettingsView;
import org.lecturestudio.core.beans.BooleanProperty;
import org.lecturestudio.core.beans.ObjectProperty;
import org.lecturestudio.core.beans.StringProperty;
import org.lecturestudio.core.view.Action;
import org.lecturestudio.javafx.beans.LectBooleanProperty;
import org.lecturestudio.javafx.beans.LectObjectProperty;
import org.lecturestudio.javafx.beans.LectStringProperty;
import org.lecturestudio.javafx.util.FxUtils;
import org.lecturestudio.javafx.view.FxView;
import org.lecturestudio.javafx.view.FxmlView;

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
    private TextField ftpField;

    @FXML
    private TextField ftpUser;

    @FXML
    private TextField ftpPassword;

    @FXML
    private TextField moodleField;

    @FXML
    private TextArea formatsMoodle;

    @FXML
    private TextField ftpPort;

    @FXML
    private TextArea formatsFileserver;

    @FXML
    private Button syncRootPathButton;

    @FXML
    private CheckBox showUnknownFormats;

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


    @Override
    public void setLocale(ObjectProperty<Locale> locale) {
        languageCombo.valueProperty().bindBidirectional(new LectObjectProperty<>(locale));
    }

    @Override
    public void setLocales(List<Locale> locales) {
        FxUtils.invoke(() -> languageCombo.getItems().setAll(locales));
    }

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

    @Override
    public void setOnCheckToken(Action action) {
        FxUtils.bindAction(checkToken, action);
    }

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
        //if(valid) {
            //tokenField.setStyle("-fx-border-width: 4px; -fx-text-box-border: #3CB222; -fx-focus-color: #3CB222");
        //} else {
            //tokenField.getStyleClass().add("error");
            //tokenField.setStyle("-fx-border-width: 4px; -fx-text-box-border: #B22222; -fx-focus-color: #B22222");
        //}
    }

    /**
     * Input and inputvalidation of the fileserver url.
     *
     * @param ftpURL User input.
     */
    @Override
    public void setFtpField(StringProperty ftpURL) {
        ftpField.textProperty().bindBidirectional(new LectStringProperty(ftpURL));
        ftpField.textProperty().addListener(event -> {
            ftpField.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("error"),
                    (!ftpField.getText().isEmpty() &&
                            !ftpField.getText().matches("^(((https?|ftp)://)|(ftp\\.))[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))
            );
        });
    }

    /**
     * Input and inputvalidation of the used port.
     *
     * @param ftpport User input.
     */
    @Override
    public void setFtpPort(StringProperty ftpport) {
        ftpPort.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, UserInputValidations.numberValidationFormatter));
        ftpPort.textProperty().bindBidirectional(new LectStringProperty(ftpport));
    }

    /**
     * Input of the fileserver username.
     *
     * @param ftpuser User input.
     */
    @Override
    public void setFtpUser(StringProperty ftpuser) {
        ftpUser.textProperty().bindBidirectional(new LectStringProperty(ftpuser));
    }

    /**
     * Input of the fileserver password.
     *
     * @param ftppassword User input.
     */
    @Override
    public void setFtpPassword(StringProperty ftppassword) {
        ftpPassword.textProperty().bindBidirectional(new LectStringProperty(ftppassword));
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
     * Input of formats used to upload to the fileserver.
     *
     * @param fileserverformats User input.
     */
    @Override
    public void setFormatsFileserver(StringProperty fileserverformats) {
        formatsFileserver.textProperty().bindBidirectional(new LectStringProperty(fileserverformats));
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
            syncRootPath.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("error"),
                    !syncRootPath.getText().isEmpty() &&
                            !Files.isDirectory(Paths.get(syncRootPath.getText()))
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


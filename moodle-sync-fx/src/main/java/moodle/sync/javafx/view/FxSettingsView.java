package moodle.sync.javafx.view;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import moodle.sync.core.fileserver.FileServerClient;
import moodle.sync.core.fileserver.FileServerType;
import moodle.sync.presenter.SettingsPresenter;
import moodle.sync.util.UserInputValidations;
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
    private GridPane fileserverSettings;

    @FXML
    private RowConstraints fileserverDefaultFolderColumn;

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
    private TextField panoptoField;

    @FXML
    private TextField ftpUser;

    @FXML
    private TextField panoptoClient;

    @FXML
    private TextField ftpPassword;

    @FXML
    private TextField panoptoSecret;

    @FXML
    private Button checkPanopto;

    @FXML
    private TextField moodleField;

    @FXML
    private ComboBox fileserverCombo;

    @FXML
    private TextArea formatsMoodle;

    @FXML
    private TextField ftpPort;

    @FXML
    private TextField fileserverDefaultFolder;

    @FXML
    private TextArea formatsFTP;

    @FXML
    private TextArea formatsPanopto;

    @FXML
    private Button syncRootPathButton;

    @FXML
    private CheckBox showUnknownFormats;

    @FXML
    private Label ftpPortLabel;

    @FXML
    private Label fileserverDefaultFolderLabel;

    @FXML
    private Label fileserverDefaultFolderExplain;

    @FXML
    RowConstraints fileserverDefaultFolderColumnExplain;

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
     * Method invokes a checking-operation for the inserted token.
     *
     * @param action start checking-operation.
     */
    @Override
    public void setOnCheckPanopto(Action action) {
        FxUtils.bindAction(checkPanopto, action);
    }

    /**
     * Marks the token-text-field if valid.
     *
     * @param valid boolean param.
     */
    @Override
    public void setPanoptoValid(boolean valid){
        panoptoSecret.pseudoClassStateChanged(
                PseudoClass.getPseudoClass("error"),
                (!valid)
        );
        panoptoSecret.pseudoClassStateChanged(
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
        System.out.println();
    }

    @Override
    public void setPanopto() {
        FxUtils.invoke(() -> {
            fileserverSettings.setVisible(true);
            fileserverSettings.setManaged(true);
            ftpPortLabel.setManaged(false);
            ftpPortLabel.setVisible(false);
            ftpPort.setManaged(false);
            ftpPort.setVisible(false);
            fileserverDefaultFolder.setManaged(true);
            fileserverDefaultFolder.setVisible(true);
            fileserverDefaultFolderLabel.setVisible(true);
            fileserverDefaultFolderExplain.setVisible(true);
            fileserverDefaultFolderLabel.setManaged(true);
            fileserverDefaultFolderExplain.setManaged(true);
            fileserverDefaultFolderColumn.setMaxHeight(30.0);
            fileserverDefaultFolderColumnExplain.setMaxHeight(30.0);

            ftpField.setVisible(false);
            ftpField.setManaged(false);
            panoptoField.setVisible(true);
            panoptoField.setManaged(true);

            ftpUser.setVisible(false);
            ftpUser.setManaged(false);
            panoptoClient.setVisible(true);
            panoptoClient.setManaged(true);

            ftpPassword.setVisible(false);
            ftpPassword.setManaged(false);
            panoptoSecret.setVisible(true);
            panoptoSecret.setManaged(true);

            formatsFTP.setVisible(false);
            formatsFTP.setManaged(false);
            formatsPanopto.setVisible(true);
            formatsPanopto.setManaged(true);
        });
    }

    @Override
    public void setFtp() {
        FxUtils.invoke(() -> {
            fileserverSettings.setVisible(true);
            fileserverSettings.setManaged(true);
            ftpPortLabel.setManaged(true);
            ftpPortLabel.setVisible(true);
            ftpPortLabel.setPrefWidth(60.0);
            ftpPort.setManaged(true);
            ftpPort.setVisible(true);
            fileserverDefaultFolder.setManaged(false);
            fileserverDefaultFolder.setVisible(false);
            fileserverDefaultFolderLabel.setVisible(false);
            fileserverDefaultFolderExplain.setVisible(false);
            fileserverDefaultFolderLabel.setManaged(false);
            fileserverDefaultFolderExplain.setManaged(false);
            fileserverDefaultFolderColumn.setMaxHeight(0.0);
            fileserverDefaultFolderColumnExplain.setMaxHeight(0.0);

            ftpField.setVisible(true);
            ftpField.setManaged(true);
            panoptoField.setVisible(false);
            panoptoField.setManaged(false);

            ftpUser.setVisible(true);
            ftpUser.setManaged(true);
            panoptoClient.setVisible(false);
            panoptoClient.setManaged(false);

            ftpPassword.setVisible(true);
            ftpPassword.setManaged(true);
            panoptoSecret.setVisible(false);
            panoptoSecret.setManaged(false);

            formatsFTP.setVisible(true);
            formatsFTP.setManaged(true);
            formatsPanopto.setVisible(false);
            formatsPanopto.setManaged(false);
        });
    }

    @Override
    public void setNoFileserver() {
        FxUtils.invoke(() -> {
            fileserverSettings.setVisible(false);
            fileserverSettings.setManaged(false);
        });
    }
    /**
     * Input and input-validation of the fileserver url.
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

    @Override
    public void setPanoptoField(StringProperty panoptoURL) {
        panoptoField.textProperty().bindBidirectional(new LectStringProperty(panoptoURL));
        panoptoField.textProperty().addListener(event -> {
            panoptoField.pseudoClassStateChanged(
                    PseudoClass.getPseudoClass("error"),
                    (!panoptoField.getText().isEmpty() &&
                            !panoptoField.getText().matches("^((https?)://)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))
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

    @Override
    public void setPanoptoClient(StringProperty panoptoclient) {
        panoptoClient.textProperty().bindBidirectional(new LectStringProperty(panoptoclient));
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

    @Override
    public void setPanoptoSecret(StringProperty panoptosecret) {
        panoptoSecret.textProperty().bindBidirectional(new LectStringProperty(panoptosecret));
    }

    @Override
    public void setFileserverDefaultFolder(StringProperty defaultFolder) {
        fileserverDefaultFolder.textProperty().bindBidirectional(new LectStringProperty(defaultFolder));
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

    @Override
    public void setFormatsFTP(StringProperty ftpformats) {
        formatsFTP.textProperty().bindBidirectional(new LectStringProperty(ftpformats));
    }

    @Override
    public void setFormatsPanopto(StringProperty panoptoformats) {
        formatsPanopto.textProperty().bindBidirectional(new LectStringProperty(panoptoformats));
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


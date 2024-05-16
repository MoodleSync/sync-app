package moodle.sync.javafx.view;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.StackPane;
import javafx.util.converter.IntegerStringConverter;
import moodle.sync.core.beans.StringProperty;
import moodle.sync.javafx.core.beans.LectStringProperty;
import moodle.sync.javafx.core.view.FxView;
import moodle.sync.javafx.core.view.FxmlView;
import moodle.sync.presenter.FtpSettingsPresenter;
import moodle.sync.util.UserInputValidations;
import moodle.sync.view.FtpSettingsView;


@FxmlView(name = "ftp-settings", presenter = FtpSettingsPresenter.class)
public class FxFtpSettingsView extends StackPane implements FtpSettingsView, FxView {

    @FXML
    private TextField ftpField;

    @FXML
    private TextField ftpUser;

    @FXML
    private TextField ftpPassword;

    @FXML
    private TextField ftpPort;

    @FXML
    private TextArea formatsFTP;

    public FxFtpSettingsView() {
        super();
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

    /**
     * Input and inputvalidation of the used port.
     *
     * @param ftpport User input.
     */
    @Override
    public void setFtpPort(StringProperty ftpport) {
        ftpPort.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0,
                UserInputValidations.numberValidationFormatter));
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

    @Override
    public void setFormatsFTP(StringProperty ftpformats) {
        formatsFTP.textProperty().bindBidirectional(new LectStringProperty(ftpformats));
    }
}

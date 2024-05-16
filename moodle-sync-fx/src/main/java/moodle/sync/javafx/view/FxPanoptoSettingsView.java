package moodle.sync.javafx.view;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import moodle.sync.core.beans.StringProperty;
import moodle.sync.core.view.Action;
import moodle.sync.javafx.core.beans.LectStringProperty;
import moodle.sync.javafx.core.util.FxUtils;
import moodle.sync.javafx.core.view.FxView;
import moodle.sync.javafx.core.view.FxmlView;
import moodle.sync.presenter.PanoptoSettingsPresenter;
import moodle.sync.view.PanoptoSettingsView;

@FxmlView(name = "panopto-settings", presenter = PanoptoSettingsPresenter.class)
public class FxPanoptoSettingsView extends StackPane implements PanoptoSettingsView, FxView {

    @FXML
    private TextField panoptoField;

    @FXML
    private TextField panoptoClient;

    @FXML
    private TextField panoptoSecret;

    @FXML
    private Button checkPanopto;

    @FXML
    private TextArea formatsPanopto;

    @FXML
    private TextField panoptoDefaultFolder;

    @FXML
    private Label panoptoDefaultFolderLabel;

    @FXML
    private Label panoptoDefaultFolderExplain;

    @FXML
    RowConstraints panoptoDefaultFolderColumnExplain;

    @FXML
    private RowConstraints panoptoDefaultFolderColumn;

    public FxPanoptoSettingsView() {
        super();
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

    @Override
    public void setPanoptoClient(StringProperty panoptoClientId) {
        panoptoClient.textProperty().bindBidirectional(new LectStringProperty(panoptoClientId));
    }

    @Override
    public void setPanoptoSecret(StringProperty panoptosecret) {
        panoptoSecret.textProperty().bindBidirectional(new LectStringProperty(panoptosecret));
    }

    @Override
    public void setPanoptoDefaultFolder(StringProperty defaultFolder) {
        panoptoDefaultFolder.textProperty().bindBidirectional(new LectStringProperty(defaultFolder));
    }

    @Override
    public void setFormatsPanopto(StringProperty panoptoformats) {
        formatsPanopto.textProperty().bindBidirectional(new LectStringProperty(panoptoformats));
    }

}

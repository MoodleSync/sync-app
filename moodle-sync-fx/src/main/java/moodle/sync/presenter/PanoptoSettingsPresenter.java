package moodle.sync.presenter;

import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.config.FileserverPanoptoConfiguration;
import moodle.sync.core.presenter.Presenter;
import moodle.sync.core.web.model.TokenProvider;
import moodle.sync.core.web.panopto.PanoptoService;
import moodle.sync.view.PanoptoSettingsView;

import javax.inject.Inject;


public class PanoptoSettingsPresenter  extends Presenter<PanoptoSettingsView> {

    private  FileserverPanoptoConfiguration config;

    @Inject
    public PanoptoSettingsPresenter(ApplicationContext context, PanoptoSettingsView view) {
        super(context, view);
    }

    @Override
    public void initialize() throws Exception {

            view.setOnCheckPanopto(this::checkPanopto);

            view.setPanoptoField(config.panoptoServerProperty());
            view.setPanoptoClient(config.panoptoClientIdProperty());
            view.setPanoptoSecret(config.panoptoSecretProperty());
            view.setFormatsPanopto(config.panoptoFormatsProperty());
            view.setPanoptoDefaultFolder(config.panoptoDefaultFolderProperty());

    }

    public void setPanoptoConfig(FileserverPanoptoConfiguration fileserverPanoptoConfiguration) {
        this.config = fileserverPanoptoConfiguration;
    }

    private void checkPanopto() {
        try {
            PanoptoService panoptoService = new PanoptoService(config.getPanoptoServer(),
                    new TokenProvider(config.getPanoptoClientId()
                            , config.getPanoptoSecret()));
            panoptoService.getSearchFolder("test");
            view.setPanoptoValid(true);
        } catch (Exception e) {
            view.setPanoptoValid(false);
        }
    }
}

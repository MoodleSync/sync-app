package moodle.sync.javafx.custom;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import moodle.sync.javafx.model.SyncTableElement;

/**
 * Class used for determining the state of a CheckBox inside the "sync-page"-table.
 *
 * @author Daniel Schröter
 */
public class AvailabilityCellValueFactory implements Callback<TableColumn.CellDataFeatures<SyncTableElement,Boolean>, ObservableValue<Boolean>> {
    @Override
    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<SyncTableElement, Boolean> param)
    {
        SyncTableElement elem = param.getValue();
        //selectedProperty should be used to determine the state.
        param.getValue().visibleProperty();
        SimpleBooleanProperty booleanProp= (SimpleBooleanProperty) elem.visibleProperty();
        booleanProp.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                                Boolean newValue) {
                elem.setVisible(newValue);
            }
        });
        return booleanProp;
    }
}

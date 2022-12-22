package moodle.sync.javafx.custom;

import javafx.scene.control.ListCell;
import moodle.sync.core.model.json.Section;


import static java.util.Objects.isNull;

/**
 * Class used to display the name of a Section inside a ListCell.
 *
 * @author Daniel Schröter
 */
public class SectionListCell extends ListCell<Section> {

    @Override
    protected void updateItem(Section item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);

        if (isNull(item) || empty) {
            setText("");
        } else {
            setText(item.getName());
        }
    }

}

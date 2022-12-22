package moodle.sync.util;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

/**
 * Class implementing a method used for input validation.
 *
 * @authod Daniel Schröter
 */
public class UserInputValidations {

    /**
     * Providing operation to check whether an input is a number. If not, the input is not printed.
     *
     * returns if the input is a number, the number otherwise an empty string.
     */
    public static UnaryOperator<TextFormatter.Change> numberValidationFormatter = change -> {
        //If change is not a number, change input to an empty string.
        if (!change.getControlNewText().matches("\\d+")) {
            change.setText("");
        }
        return change;
    };

}

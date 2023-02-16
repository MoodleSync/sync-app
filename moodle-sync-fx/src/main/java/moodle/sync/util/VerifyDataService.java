package moodle.sync.util;

/**
 * Class used to implement Methods to verify data.
 */
public final class VerifyDataService {

    /**
     * Method used to verify that a String is not empty.
     */
    public static boolean validateString(String string){
        if(string == null || string.isEmpty() || string.isBlank()) {
            return false;
        } else {
            return true;
        }
    }
}

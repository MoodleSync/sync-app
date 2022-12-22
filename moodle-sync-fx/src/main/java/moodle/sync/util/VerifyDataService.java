package moodle.sync.util;

/**
 * Class used to implement Methods to verify data.
 */
public final class VerifyDataService {

    public static boolean validateString(String string){
        if(string == null || string.isEmpty() || string.isBlank()) {
            return false;
        } else {
            return true;
        }
    }
}

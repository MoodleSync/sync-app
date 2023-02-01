package moodle.sync.core.util;

/**
 * Enumeration containing several entities describing actions to do with UploadData.
 *
 * @author Daniel Schröter
 */
public enum MoodleAction {

    MoodleUpload("Upload file to moodle"),
    MoodleSynchronize("Update file on moodle"),
    FTPUpload("Upload file to fileserver"),
    FTPSynchronize("Update file on fileserver"),
    FTPLink("Link file to moodle"),
    FolderUpload("Create new folder and add files"),
    FolderSynchronize("Add files to existing folder"),
    NotLocalFile("File not locally saved"),
    ExistingFile("File is up to date"),
    DatatypeNotKnown("Data-Format not specified"),
    ExistingSection("Exisiting section"),
    UploadSection("Create a new section");

    public final String message;

    MoodleAction(String message) {
        this.message = message;
    }

}
package moodle.sync.core.fileserver;

import java.util.List;

/**
 * Interface declaring all needed methods for fileserver support.
 *
 * @author Daniel Schröter
 */
public interface FileServerClient {

    String getName();
    //Retrieve list of FileServerFiles from dedicated directory
    List<FileServerFile> getFiles(String pathname) throws Exception;

//    String uploadFile(syncTableElement item, String pathname);

    //Disconnect from fileserver
    void disconnect();

    //Connect to fileserver
    void connect();

}

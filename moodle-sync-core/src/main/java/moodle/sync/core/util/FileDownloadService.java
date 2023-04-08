package moodle.sync.core.util;

import moodle.sync.core.web.service.MoodleDownloadService;

import java.io.File;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

public final class FileDownloadService {

    public static void getFile(String url, String token, String path, String name) throws Exception {
        MoodleDownloadService moodleDownloadService = new MoodleDownloadService(url);
        try (InputStream download = moodleDownloadService.getDownload(token)) {
            if (download.available() != 0) {
                File targetFile = new File(path + "/" + name);
                java.nio.file.Files.copy(download, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    /** Usage:
     * try{
     *             FileDownloadService.getFile("https://localhost/webservice/pluginfile" +
     *                     ".php/21/mod_resource/content/0/The%20Relational%20Model.pdf?forcedownload=1", token,
     *                     "C:/Users" +
     *                     "/danie/OneDrive/Desktop", "Testdatei.pdf");
     *         } catch (Exception e){
     *             logException(e, "Sync failed");
     *         }
     * }
     */

}

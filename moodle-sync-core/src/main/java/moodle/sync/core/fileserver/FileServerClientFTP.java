package moodle.sync.core.fileserver;

import moodle.sync.core.config.MoodleSyncConfiguration;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import javax.ws.rs.Path;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing the FileServerClient-interface using the ftp-protocol.
 *
 * @author Daniel Schröter
 */
public class FileServerClientFTP implements FileServerClient {

    //Used FTPClient for communication.
    private final FTPClient ftpClient;

    //Configuration providing information about url etc.
    private final MoodleSyncConfiguration config;


    public FileServerClientFTP(MoodleSyncConfiguration config) {
        ftpClient = new FTPClient();
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        this.config = config;
    }

    /**
     * Establishes the connection with a fileserver.
     */
    @Override
    public void connect() {
        try {
            ftpClient.connect(config.getFtpConfiguration().getFtpServer(),
                    Integer.parseInt(config.getFtpConfiguration().getFtpPort()));
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new IOException("Exception in connecting to FTP Server");
            }
            ftpClient.login(config.getFtpConfiguration().getFtpUser(), config.getFtpConfiguration().getFtpPassword());
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Terminates the connection with a fileserver.
     */
    @Override
    public void disconnect() {
        try {
            ftpClient.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to gather information about uploaded files.
     *
     * @param pathname Path to the directory at the ftpserver.
     * @return a list containing elements of FileServerFile.
     */
    @Override
    public List<FileServerFile> getFiles(String pathname) throws Exception {
        List<FileServerFile> files = new ArrayList<>();
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles(pathname);
            for (FTPFile item : ftpFiles) {
                files.add(new FileServerFile(item.getName(), item.getTimestamp().getTimeInMillis()));
            }
        }
        catch (Exception e) {
            throw new Exception();
        }
        return files;
    }

    @Override
    public String getName() {
        //Todo With Language-Support
        return "FTP-Server";
    }

    /**
     * Method to upload a file to a ftpserver.
     *
     * @param item     UploadElement, containing the local path to the file.
     * @param pathname Dedicated directory at the ftpserver.
     * @return the url of the uploaded file.
     */
//    @Override
//    public String uploadFile(syncTableElement item, String pathname) {
//        //Evtl noch pathname einbringen
//        String url = null;
//        try {
//            InputStream file = Files.newInputStream(Paths.get(item.getExistingFile()));
//            ftpClient.storeFile("/"  /*+ config.getRecentSection().getName() + "/" */ + item.getExistingFileName(), file);
//            //ToDo add functionality Url
//            url = config.getFileserver() + "/" /*+  config.getRecentSection().getName() + "/" */ + item.getExistingFileName();
//            file.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return url;
//    }

}

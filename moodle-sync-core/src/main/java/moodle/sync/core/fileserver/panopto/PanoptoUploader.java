package moodle.sync.core.fileserver.panopto;

import moodle.sync.core.fileserver.panopto.util.XMLWriter;
import moodle.sync.core.model.json.PanoptoFolder;
import moodle.sync.core.model.json.PanoptoSession;
import moodle.sync.core.model.json.PanoptoSessionComplete;
import moodle.sync.core.web.model.TokenProvider;
import moodle.sync.core.web.panopto.PanoptoService;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PanoptoUploader {

    private static S3Client s3;

    public static String uploadVideo (PanoptoService panoptoService, String uri, String folderId, Path path,
                                      String title,
                                      String description) throws PanoptoException {
        try {
            PanoptoSession session = panoptoService.setBlankSession(new PanoptoFolder(folderId));

            String[] uploadTarget = session.getUploadTarget().split("/");

            String bucketName = uploadTarget[4];
            String filename = String.valueOf(path.getFileName());
            String key1 = uploadTarget[5] + "/" + filename;
            String key2 = uploadTarget[5] + "/upload_manifest_generated.xml";

            URI myURI = new URI(uri + "/Panopto");
            System.setProperty("aws.accessKeyId", "dummy");
            System.setProperty("aws.secretAccessKey", "dummy");

            uploadSingleFile(bucketName, key1, myURI, path);

            uploadSingleFile(bucketName, key2, myURI, XMLWriter.CreateUpload_manifest(title, description, getZoneDateTime(), filename));

            PanoptoSessionComplete sessionComplete = new PanoptoSessionComplete(session.getID(), session.getUploadTarget(), session.getFolderId(), "1", session.getSessionId());
            panoptoService.setFinishSession(sessionComplete);
            return session.getID();
        }
        catch (Exception e) {
            throw new PanoptoException();
        }
    }

    private static void uploadSingleFile(String bucketName, String key, URI myURI, Path path) throws Exception{
        s3 = S3Client.builder().region(Region.EU_CENTRAL_1).endpointOverride(myURI).build();

        CreateMultipartUploadRequest createRequest =
                CreateMultipartUploadRequest.builder().bucket(bucketName).key(key).build();

        CreateMultipartUploadResponse createResponse = s3.createMultipartUpload(createRequest);
        String uploadId = createResponse.uploadId();

        //If file is larger than 25 MB, the panopto server may fail, so the file is split up in several parts.

        List<CompletedPart> completedParts = new ArrayList<>();
        int partNumber = 1;
        ByteBuffer buffer = ByteBuffer.allocate(10 * 1024 * 1024);

        RandomAccessFile file = new RandomAccessFile(path.toFile(), "r");
        long fileSize = file.length();
        long position = 0;

        while (position < fileSize) {

            file.seek(position);
            int bytesRead = file.getChannel().read(buffer);

            buffer.flip();
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .contentLength((long) bytesRead)
                    .build();


            UploadPartResponse response = s3.uploadPart(uploadPartRequest, RequestBody.fromByteBuffer(buffer));

            completedParts.add(CompletedPart.builder()
                    .partNumber(partNumber)
                    .eTag(response.eTag())
                    .build());

            buffer.clear();
            position += bytesRead;
            partNumber++;
        }

        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                CompleteMultipartUploadRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .uploadId(uploadId)
                        .multipartUpload(completedMultipartUpload)
                        .build();

        s3.completeMultipartUpload(completeMultipartUploadRequest);
    }

    private static void uploadSingleFile(String bucketName, String key, URI myURI, byte[] file) {
        s3 = S3Client.builder().region(Region.EU_CENTRAL_1).endpointOverride(myURI).build();

        CreateMultipartUploadRequest createRequest =
                CreateMultipartUploadRequest.builder().bucket(bucketName).key(key).build();

        CreateMultipartUploadResponse createResponse = s3.createMultipartUpload(createRequest);
        String uploadId = createResponse.uploadId();

        UploadPartRequest uploadPartRequest1 = UploadPartRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .partNumber(1).build();

        String etag1 = s3.uploadPart(uploadPartRequest1, RequestBody.fromBytes(file)).eTag();


        CompletedPart part1 = CompletedPart.builder().partNumber(1).eTag(etag1).build();

        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(part1)
                .build();

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                CompleteMultipartUploadRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .uploadId(uploadId)
                        .multipartUpload(completedMultipartUpload)
                        .build();

        s3.completeMultipartUpload(completeMultipartUploadRequest);
    }

    private static String getZoneDateTime() {
        /*SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");

        StringBuffer buff = new StringBuffer();
        Date date = new Date();
        buff.append(format1.format(date));
        buff.append('T');
        buff.append(format2.format(date));

        Calendar calendar = Calendar.getInstance();*/

        //int offset = calendar.get(calendar.ZONE_OFFSET)
                /// (1000 * 60);
        //TODO look into this
        /*int offset = 0;
        if (offset < 0) {
            buff.append('-');
            offset *= -1;
        }
        else {
            buff.append('+');
        }
        String s1 = String.valueOf(offset / 60);
        for (int i = s1.length(); i < 2; i++) {
            buff.append('0');
        }
        buff.append(s1);
        buff.append(':');

        String s2 = String.valueOf(offset % 60);

        for (int i = s2.length(); i < 2; i++) {
            buff.append('0');
        }
        buff.append(s2);
        System.out.println(buff.toString());
        System.out.println(OffsetDateTime.now( ZoneOffset.UTC ));
        //return buff.toString();*/
        return OffsetDateTime.now( ZoneOffset.UTC ).toString();
    }
}

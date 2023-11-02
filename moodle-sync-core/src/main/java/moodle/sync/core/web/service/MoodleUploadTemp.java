package moodle.sync.core.web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import moodle.sync.core.model.json.MoodleUpload;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.File;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * This class implements an alternative way to execute http-requests with the help of the OkHttpClient and is used for file upload.
 *
 * @author Daniel Schöter
 */
@NoArgsConstructor
public class MoodleUploadTemp {

    /**
     * With the help of this method, http-requests to upload a file to Moodle are constructed and executed.
     *
     * @param name      name of the file to upload
     * @param pathname  path of the file
     * @param moodleUrl url of the Moodle platform
     * @param token     Moodle-token
     * @return MoodleUpload: Object which contains the filename and an itemid which is needed to identify the file
     */

    public MoodleUpload upload(String name, String pathname, String moodleUrl, String token) {
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            //Usage of https
            if (moodleUrl.startsWith("https")) {
                X509TrustManager trustManager;
                SSLSocketFactory sslSocketFactory;
                try {
                    trustManager = createTrustManager();
                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, new TrustManager[]{trustManager}, new java.security.SecureRandom());
                    sslSocketFactory = sslContext.getSocketFactory();
                } catch (GeneralSecurityException e) {
                    throw new RuntimeException(e);
                }
                builder.sslSocketFactory(sslSocketFactory, trustManager);
                builder.hostnameVerifier((hostname, sslSession) -> hostname
                        .equalsIgnoreCase(sslSession.getPeerHost()));

            }
            //Execution of the http-request
            OkHttpClient client = builder.build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(name, pathname,
                            RequestBody.create(MediaType.parse("application/octet-stream"),
                                    new File(pathname)))
                    .build();
            Request request = new Request.Builder()
                    .url(moodleUrl + "/webservice/upload.php?token=" + token)
                    .method("POST", body)
                    .build();
            System.out.println("--------------------------------------" + request.toString());
            ResponseBody response = client.newCall(request).execute().body();
            String bodystring = response.string();
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("--------------------------------------" + bodystring);
            List<MoodleUpload> entity = objectMapper.readValue(bodystring, new TypeReference<List<MoodleUpload>>() {
            });
            System.out.println(entity);
            return entity.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public MoodleUpload upload(String name, String pathname, String moodleUrl, String token, Long itemid) {
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            //Usage of https
            if (moodleUrl.startsWith("https")) {
                X509TrustManager trustManager;
                SSLSocketFactory sslSocketFactory;
                try {
                    trustManager = createTrustManager();
                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, new TrustManager[]{trustManager}, new java.security.SecureRandom());
                    sslSocketFactory = sslContext.getSocketFactory();
                } catch (GeneralSecurityException e) {
                    throw new RuntimeException(e);
                }
                builder.sslSocketFactory(sslSocketFactory, trustManager);
                builder.hostnameVerifier((hostname, sslSession) -> hostname
                        .equalsIgnoreCase(sslSession.getPeerHost()));

            }
            //Execution of the http-request
            OkHttpClient client = builder.build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(name, pathname,
                            RequestBody.create(MediaType.parse("application/octet-stream"),
                                    new File(pathname)))
                    .build();
            Request request = new Request.Builder()
                    .url(moodleUrl + "/webservice/upload.php?token=" + token + "&itemid=" + itemid)
                    .method("POST", body)
                    .build();
            ResponseBody response = client.newCall(request).execute().body();
            String bodystring = response.string();
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("--------------------------------------" + bodystring);
            List<MoodleUpload> entity = objectMapper.readValue(bodystring, new TypeReference<List<MoodleUpload>>() {
            });
            System.out.println(entity);
            return entity.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Method user for generating a needed X509TrustManager for https-communication
     *
     * @return X509TrustManager
     */
    private static X509TrustManager createTrustManager() {
        X509TrustManager tm;
        try {
            tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tm;
    }
}

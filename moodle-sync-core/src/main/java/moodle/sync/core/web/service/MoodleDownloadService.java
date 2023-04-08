package moodle.sync.core.web.service;

import moodle.sync.core.web.client.MoodleDownloadClient;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class MoodleDownloadService {

    private MoodleDownloadClient moodleClient;

    /**
     * Creates a new MoodleService.
     *
     * @param apiUrl The url of the Moodle-platform.
     */
    public MoodleDownloadService(String apiUrl) {
        setApiUrl(apiUrl);
    }

    /**
     * Method which instantiates a MoodleClient.
     *
     * @param apiUrl The url of the Moodle-platform.
     */
    public void setApiUrl(String apiUrl) {
        //Parameter checks.
        if (apiUrl == null || apiUrl.isEmpty() || apiUrl.isBlank()) {
            return;
        }
        RestClientBuilder builder = RestClientBuilder.newBuilder();
        builder.baseUri(URI.create(apiUrl));
        //Usage of https.
        if (apiUrl.startsWith("https")) {
            builder.sslContext(createSSLContext());
            builder.hostnameVerifier((hostname, sslSession) -> hostname.equalsIgnoreCase(sslSession.getPeerHost()));
        }
        //MoodleClient is instantiated by classes of the MicroProfile Rest Client.
        moodleClient = builder.build(MoodleDownloadClient.class);
    }

    public InputStream getDownload(String token) {
        return moodleClient.getDownload(token);
    }

    private static SSLContext createSSLContext() {
        SSLContext sslContext;

        try {
            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new TrustManager[]{tm}, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sslContext;
    }
}

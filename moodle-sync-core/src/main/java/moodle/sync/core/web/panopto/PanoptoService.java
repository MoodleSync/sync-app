package moodle.sync.core.web.panopto;

import moodle.sync.core.fileserver.panopto.PanoptoException;
import moodle.sync.core.model.json.*;
import moodle.sync.core.web.filter.AuthorizationFilter;
import moodle.sync.core.web.model.TokenProvider;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class PanoptoService {

    private PanoptoClient panoptoClient;

    private TokenProvider tokenProvider;
    /**
     * Creates a new MoodleService.
     *
     * @param apiUrl The url of the Moodle-platform.
     */
    public PanoptoService(String apiUrl, TokenProvider tokenProvider) {
        setApiUrl(apiUrl, tokenProvider);
    }

    /**
     * Method which instantiates a MoodleClient.
     *
     * @param apiUrl The url of the Moodle-platform.
     */
    public void setApiUrl(String apiUrl, TokenProvider tokenProvider) {
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
        panoptoClient = builder
                .property(TokenProvider.class.getName(), tokenProvider)
                .register(AuthorizationFilter.class)
                .build(PanoptoClient.class);
    }

    public PanoptoSession setBlankSession(PanoptoFolder folder) {
        return panoptoClient.createBlankSession(folder);
    }

    public PanoptoSessionComplete setFinishSession(PanoptoSessionComplete sessionComplete) {
        return panoptoClient.finishSession(sessionComplete.getID(), sessionComplete);
    }

    public PanoptoSessionComplete getStatusSession(String sessionId) {
        return panoptoClient.statusSession(sessionId);
    }

    public PanoptoResults getSearchFolder(String searchQuery) throws Exception {
        return panoptoClient.searchFolder(searchQuery);
    }

    public PanoptoFolderContent getFolderContents(PanoptoFolder folder) {
        return panoptoClient.folderContent(folder.getFolderId(), "CreatedDate", "Desc");
    }

    public void setTokenProvider(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
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

package moodle.sync.core.fileserver.panopto.util;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import moodle.sync.core.app.ApplicationContext;
import moodle.sync.core.config.MoodleSyncConfiguration;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PanoptoAuthorizer {

    private final MoodleSyncConfiguration config;
    //TODO make this a variable from Config
    private static final String TOKEN_SERVER_URL = "https://tu-darmstadt.cloud.panopto.eu/Panopto/oauth2/connect/token";
    private static final String AUTHORIZATION_SERVER_URL = "https://tu-darmstadt.cloud.panopto.eu/Panopto/oauth2/connect/authorize";

    /** Port in the "Callback URL". */
    private static final int PORT = 9127;

    /** Domain name in the "Callback URL". */
    private static final String DOMAIN = "localhost";

    /**
     * Directory to store user credentials.
     */
    private static File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".store/googlesample");

    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
     * globally shared instance across your application.
     */
    public static FileDataStoreFactory DATA_STORE_FACTORY;

    private static DataStore<StoredCredential> credentialDataStore;
    /**
     * OAuth 2 scope.
     */
    private static final List<String> SCOPE =
            Arrays.asList("openid", "api" , "offline_access");
    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Global instance of the JSON factory.
     */
    static final JsonFactory JSON_FACTORY = new GsonFactory();

    @Inject
    public PanoptoAuthorizer(ApplicationContext context){
        this.config = (MoodleSyncConfiguration) context.getConfiguration();
    }

    /**
     * Method used to authorize a user via OAuth2.
     *
     * @return Credentials
     * @throws IOException
     */
    public static Credential fullAuthorize(String apiKey, String apiSecret) throws IOException {
        new File(System.getProperty("user.home"), ".store/googlesample/StoredCredential").delete();
        DATA_STORE_DIR = new File(System.getProperty("user.home"), ".store/googlesample");
        DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);


        // set up authorization code flow
        AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                HTTP_TRANSPORT, JSON_FACTORY, new GenericUrl(TOKEN_SERVER_URL),
                new ClientParametersAuthentication(apiKey, apiSecret),
                apiKey, AUTHORIZATION_SERVER_URL)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setScopes(SCOPE)
                .build();

        // authorize
        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder().setHost(DOMAIN).setPort(PORT).build();
        Credential credentials = null;

        try {
            credentials = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return credentials;
    }

    /**
     * Method trying to authorize via an existend refresh-token.
     *
     * @return Credentials
     * @throws IOException
     */
    public static Credential authorize(String apiKey, String apiSecret) throws IOException {
        Credential reCred = null;
        try{
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);

            StoredCredential cred = (StoredCredential) DATA_STORE_FACTORY.getDataStore("StoredCredential").get(
                    "user");

            try {
                TokenResponse response = new RefreshTokenRequest(HTTP_TRANSPORT, JSON_FACTORY, new GenericUrl(TOKEN_SERVER_URL), cred.getRefreshToken()).setClientAuthentication(new ClientParametersAuthentication(apiKey, apiSecret)).setScopes(SCOPE).setGrantType("refresh_token").execute();

                credentialDataStore = StoredCredential.getDefaultDataStore(DATA_STORE_FACTORY);

                Credential credential = newCredential(apiKey, apiSecret, "user").setFromTokenResponse(response);

                if (credentialDataStore != null) {
                    credentialDataStore.set("user", new StoredCredential(credential));
                }


                return credential;
            }
            catch (Exception e) {
                e.printStackTrace();
                throw  new Exception();
            }
        } catch (Exception e) {
            reCred = fullAuthorize(apiKey, apiSecret);
        }
        return reCred;
    }

    /**
     * Method used to create new Credentials based on a response.
     *
     * @param userId
     * @return Credential
     */
    private static Credential newCredential(String apiKey, String apiSecret,String userId) {
        try {
            Credential reCred = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).setTransport(HTTP_TRANSPORT).setJsonFactory(JSON_FACTORY).setTokenServerUrl(new GenericUrl(TOKEN_SERVER_URL)).setClientAuthentication(new ClientParametersAuthentication(apiKey, apiSecret)).build();
            return reCred;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

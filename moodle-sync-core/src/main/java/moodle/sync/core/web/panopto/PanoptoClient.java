package moodle.sync.core.web.panopto;

import com.google.api.client.auth.oauth2.Credential;

import moodle.sync.core.model.json.*;
import moodle.sync.core.web.filter.LoggingFilter;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static moodle.sync.core.fileserver.panopto.util.PanoptoAuthorizer.authorize;


@Path("/Panopto/")
//@ClientHeaderParam(name = "Authorization", value = "{getAuthorizationHeader}")
@RegisterProviders({@RegisterProvider(LoggingFilter.class), @RegisterProvider(JsonConfigProvider.class),})
public interface PanoptoClient {

    //default String getAuthorizationHeader() throws Exception {
    //    final Credential credential = authorize("5ba41c3b-c0f5-4fec-91f0-b09e00aa8acf",
    //        "xj5zi5MTqL5nwwezud8eyN5AWkx2WpU/N21Vwuzf8T4=");
    //    return "Bearer " + credential.getAccessToken();
    //}

    @POST
    @Consumes("application/json")
    @Path("PublicAPI/REST/sessionUpload")
    PanoptoSession createBlankSession(PanoptoFolder folder);

    @GET
    @Consumes("application/json")
    @Path("PublicAPI/REST/sessionUpload/{id}")
    PanoptoSessionComplete statusSession(@PathParam("id") String id);

    @PUT
    @Consumes("application/json")
    @Path("PublicAPI/REST/sessionUpload/{id}")
    PanoptoSessionComplete finishSession(@PathParam("id") String id, PanoptoSessionComplete sessionComplete);

    @GET
    @Path("api/v1/folders/search")
    @Produces(MediaType.APPLICATION_JSON)
    PanoptoResults searchFolder(@QueryParam("searchQuery") String searchQuery);

    @GET
    @Path("api/v1/folders/{id}/sessions")
    PanoptoFolderContent folderContent(@PathParam("id") String id, @QueryParam("sortField") String sortField, @QueryParam(
            "sortOrder") String sortOrder);
}

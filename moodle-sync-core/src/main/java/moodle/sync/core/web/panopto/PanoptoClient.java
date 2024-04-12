package moodle.sync.core.web.panopto;

import com.google.api.client.auth.oauth2.Credential;

import moodle.sync.core.model.json.*;
import moodle.sync.core.web.filter.LoggingFilter;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;



@Path("/Panopto/")
@RegisterProviders({@RegisterProvider(LoggingFilter.class), @RegisterProvider(JsonConfigProvider.class),})
public interface PanoptoClient {

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

package moodle.sync.core.web.client;

import moodle.sync.core.model.json.JsonConfigProvider;
import moodle.sync.core.web.filter.LoggingFilter;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;


@Path("")
@RegisterProviders({@RegisterProvider(LoggingFilter.class), @RegisterProvider(JsonConfigProvider.class),})
public interface MoodleDownloadClient {

    @GET
    @Path("")
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    InputStream getDownload(@QueryParam("token") String token);
}

package moodle.sync.core.web.client;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import moodle.sync.core.model.json.*;
import moodle.sync.core.web.filter.LoggingFilter;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;

import java.util.List;

/**
 * Interface used to define https-calls executed and implemented by classes of the package MicroProfile Rest Client.
 *
 * @author Daniel Schröter
 */
@Path("/webservice/rest/server.php")
@RegisterProviders({@RegisterProvider(LoggingFilter.class), @RegisterProvider(JsonConfigProvider.class),})
public interface MoodleClient {

    /**
     * Obtain a users subscribed Moodle-courses.
     *
     * @param moodlewsrestformat Used dataformat.
     * @param token              The Moodle-token.
     * @param function           The called Web Service API function.
     * @param userid             The user´s id.
     * @return a list of moodle-courses.
     */
    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    List<Course> getCourses(@QueryParam("moodlewsrestformat") String moodlewsrestformat,
                            @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function,
                            @QueryParam("userid") int userid);

    /**
     * Method used to provide a webservice info, including information about the user.
     *
     * @param moodlewsrestformat Used dataformat.
     * @param token              The Moodle-token.
     * @param function           The called Web Service API function.
     * @return an object containing the userid.
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    SiteInfo getSiteInfo(@QueryParam("moodlewsrestformat") String moodlewsrestformat,
                         @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function);

    /**
     * Receive a moodle-courses content.
     *
     * @param moodlewsrestformat Used dataformat.
     * @param token              The Moodle-token.
     * @param function           The called Web Service API function.
     * @param courseid           The Moodle-courses id.
     * @return a list of course-sections containing course-modules.
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    List<Section> getCourseContent(@QueryParam("moodlewsrestformat") String moodlewsrestformat,
                                   @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function,
                                   @QueryParam("courseid") int courseid);

    /**
     * Method used to move a course-module to a specific position.
     *
     * @param moodlewsrestformat Used dataformat.
     * @param token              The Moodle-token.
     * @param function           The called Web Service API function.
     * @param cmid               The course-modules id.
     * @param sectionid          The course-sections id.
     * @param beforemod          The course-module id of the course-module at the supposed position. If beforemod ist
     *                           null, the course-module will be moved to the bottom of the course-section.
     */
    @POST
    @Path("")
    void setMoveModule(@QueryParam("moodlewsrestformat") String moodlewsrestformat,
                       @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function,
                       @QueryParam("cmid") int cmid, @QueryParam("sectionid") int sectionid,
                       @QueryParam("beforemod") Integer beforemod);

    /**
     * Create a course-module of type "url".
     *
     * @param moodlewsrestformat Used dataformat.
     * @param token              The Moodle-token.
     * @param function           The called Web Service API function.
     * @param courseid           The Moodle-courses id.
     * @param sectionnum         The moodle-sections number in the moodle-course.
     * @param urlname            The displayname of the course-module.
     * @param url                The course-modules content.
     * @param beforemod          The course-module id of the course-module at the supposed position. If beforemod ist
     *                           null, the course-module will be moved to the bottom of the course-section.
     */
    @POST
    @Path("")
    void setUrl(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token,
                @QueryParam("wsfunction") String function, @QueryParam("courseid") int courseid,
                @QueryParam("sectionnum") int sectionnum, @QueryParam("urlname") String urlname,
                @QueryParam("url") String url, @QueryParam("time") Long time, @QueryParam("visible") boolean visible,
                @QueryParam("beforemod") Integer beforemod);

    /**
     * Create a course-module of type "resource".
     *
     * @param moodlewsrestformat Used dataformat.
     * @param token              The Moodle-token.
     * @param function           The called Web Service API function.
     * @param courseid           The Moodle-courses id.
     * @param sectionnum         The moodle-sections number in the moodle-course.
     * @param itemid             The id of the prior uploaded file, which should be presented by the course-module.
     * @param displayname        The displayname of the course-module.
     * @param beforemod          The course-module id of the course-module at the supposed position. If beforemod ist
     *                           null, the course-module will be moved to the bottom of the course-section.
     */
    @POST
    @Path("")
    void setResource(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token
            , @QueryParam("wsfunction") String function, @QueryParam("courseid") int courseid,
                     @QueryParam("sectionnum") int sectionnum, @QueryParam("itemid") long itemid, @QueryParam("time") Long time,
                     @QueryParam("visible") boolean visible, @QueryParam("displayname") String displayname,
                     @QueryParam("beforemod") Integer beforemod);

    /**
     * Create a course-module of type "folder".
     *
     * @param moodlewsrestformat Used dataformat.
     * @param token              The Moodle-token.
     * @param function           The called Web Service API function.
     * @param courseid           The Moodle-courses id.
     * @param sectionnum         The moodle-sections number in the moodle-course.
     * @param itemid             The id of the prior uploaded files, which should be presented by the course-module.
     * @param displayname        The displayname of the course-module.
     * @param beforemod          The course-module id of the course-module at the supposed position. If beforemod ist
     *                           null, the course-module will be moved to the bottom of the course-section.
     */
    @POST
    @Path("")
    void setFolder(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token,
                   @QueryParam("wsfunction") String function, @QueryParam("courseid") int courseid,
                   @QueryParam("sectionnum") int sectionnum, @QueryParam("itemid") long itemid,
                   @QueryParam("displayname") String displayname, @QueryParam("time") Long time,
                   @QueryParam("visible") boolean visible, @QueryParam("beforemod") Integer beforemod);

    /**
     * Add a file or several files to an existing folder.
     *
     * @param moodlewsrestformat Used dataformat.
     * @param token              The Moodle-token.
     * @param function           The called Web Service API function.
     * @param courseid           The Moodle-courses id.
     * @param itemid             The id of the prior uploaded files, which should be presented by the course-module.
     * @param contextid          The contextid of the folder the files should be added to.
     */
    @POST
    @Path("")
    void addFilesToFolder(@QueryParam("moodlewsrestformat") String moodlewsrestformat,
                          @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function,
                          @QueryParam("courseid") int courseid, @QueryParam("itemid") long itemid,
                          @QueryParam("contextid") int contextid);

    /**
     * Obtains the course-content of a specific course-section.
     *
     * @param moodlewsrestformat Used dataformat.
     * @param token              The Moodle-token.
     * @param function           The called Web Service API function.
     * @param courseid           The Moodle-courses id.
     * @param s                  Needed parameter for creating a JSON-Object.
     * @param sectionid          The course-sections id.
     * @return a list containg one section.
     */
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    List<Section> getCourseContentSection(@QueryParam("moodlewsrestformat") String moodlewsrestformat,
                                          @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function,
                                          @QueryParam("courseid") int courseid, @QueryParam("options[0][name]") String s,
                                          @QueryParam("options[0][value]") int sectionid);

    /**
     * Method used to remove a course-module.
     *
     * @param moodlewsrestformat Used dataformat.
     * @param token              The Moodle-token.
     * @param function           The called Web Service API function.
     * @param cmid               The course-modules id.
     */
    @POST
    @Path("")
    void removeResource(@QueryParam("moodlewsrestformat") String moodlewsrestformat,
                        @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function,
                        @QueryParam("cmids[0]") int cmid);

    /**
     * Method used to create a new course-section.
     *
     * @param moodlewsrestformat Used dataformat.
     * @param token              The Moodle-token.
     * @param function           The called Web Service API function.
     * @param courseid           The Moodle-courses id.
     * @param sectionname        The name of the section.
     * @param sectionnum         The moodle-sections number in the moodle-course.
     */
    @GET
    @Path("")
    void setSection(@QueryParam("moodlewsrestformat") String moodlewsrestformat, @QueryParam("wstoken") String token,
                    @QueryParam("wsfunction") String function, @QueryParam("courseid") int courseid,
                    @QueryParam("sectionname") String sectionname, @QueryParam("sectionnum") int sectionnum);

    @GET
    @Path("")
    Permissions getPermissions(@QueryParam("moodlewsrestformat") String moodlewsrestformat,
                               @QueryParam("wstoken") String token, @QueryParam("wsfunction") String function, @QueryParam(
                                "courseids[0]") int courseid);
}

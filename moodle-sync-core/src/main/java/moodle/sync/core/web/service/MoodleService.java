package moodle.sync.core.web.service;

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import moodle.sync.core.beans.StringProperty;
import moodle.sync.core.model.json.Course;
import moodle.sync.core.model.json.Section;
import moodle.sync.core.model.json.SiteInfo;
import moodle.sync.core.web.client.MoodleClient;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Class which contains methods to instantiate the interface MoodleClient and to call methods defined in this interface.
 *
 * @author Daniel Schröter
 */
public class MoodleService {

    private MoodleClient moodleClient;

    /**
     * Creates a new MoodleService.
     *
     * @param apiUrl The url of the Moodle-platform.
     */
    public MoodleService(StringProperty apiUrl) {
        setApiUrl(apiUrl.get());
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
        moodleClient = builder.build(MoodleClient.class);
    }

    /**
     * Method used to get a users userid.
     *
     * @param token The Moodle-token.
     * @return the userid as an int.
     */
    public int getUserId(String token) throws Exception {
        SiteInfo info = moodleClient.getSiteInfo("json", token, "core_webservice_get_site_info");
        return info.getUserid();
    }

    /**
     * Method used to provide a users subscribed Moodle-courses.
     *
     * @param token  The Moodle-token.
     * @param userid A user's id.
     * @return list of Moodle-Courses.
     */
    public List<Course> getEnrolledCourses(String token, int userid) {
        return moodleClient.getCourses("json", token, "core_enrol_get_users_courses", userid);
    }

    /**
     * Obtain a specific Moodle-courses content.
     *
     * @param token    The Moodle-token.
     * @param courseid A Moodle-courses id.
     * @return a list of course-sections, which contains the course-modules.
     */
    public List<Section> getCourseContent(String token, int courseid) {
        return moodleClient.getCourseContent("json", token, "core_course_get_contents", courseid);
    }

    /**
     * Obtain the content of a specific Moodle-section.
     *
     * @param token     The Moodle-token.
     * @param courseid  A Moodle-courses id.
     * @param sectionid The course-sections id.
     * @return the course-section, which contains the course-modules.
     */
    public List<Section> getCourseContentSection(String token, int courseid, int sectionid) {
        return moodleClient.getCourseContentSection("json", token, "core_course_get_contents", courseid, "sectionid",
                sectionid);
    }

    /**
     * Move a course-module to a specific position in a Moodle-course.
     *
     * @param token     The Moodle-token.
     * @param cmid      The course-modules id.
     * @param sectionid The course-sections id.
     * @param beforemod The course-module id of the course-module at the supposed position. If beforemod ist null,
     *                  the course-module will be moved to the bottom of the course-section.
     */
    public void setMoveModule(String token, int cmid, int sectionid, int beforemod) {
        if (beforemod == -1) {
            moodleClient.setMoveModule("json", token, "local_course_move_module_to_specific_position", cmid,
                    sectionid, null);
        } else {
            moodleClient.setMoveModule("json", token, "local_course_move_module_to_specific_position", cmid,
                    sectionid, beforemod);
        }
    }

    /**
     * Create a course-module of the type "url".
     *
     * @param token    The Moodle-token.
     * @param courseid A Moodle-courses id.
     * @param section  The moodle-sections number in the moodle-course.
     * @param urlname  The displayname of the course-module.
     * @param url      The course-modules content.
     */
    public void setUrl(String token, int courseid, int section, String urlname, String url, Long time,
                       Boolean visible, int beforemod) {
        if (beforemod == -1) {
            moodleClient.setUrl("json", token, "local_course_add_new_course_module_url", courseid, section, urlname,
                    url, time, visible, null);
        } else {
            moodleClient.setUrl("json", token, "local_course_add_new_course_module_url", courseid, section, urlname,
                    url, time, visible, beforemod);
        }
    }


    /**
     * Create a course-module of the type "resource" at a specific position inside a course-section.
     *
     * @param token     The Moodle-token.
     * @param courseid  A Moodle-courses id.
     * @param section   The moodle-sections number in the moodle-course.
     * @param name      The displayname of the course-module.
     * @param itemid    The id of the prior uploaded file, which should be presented by the course-module.
     * @param beforemod The course-module id of the course-module at the supposed position. If beforemod ist null,
     *                  the course-module will be moved to the bottom of the course-section.
     */
    public void setResource(String token, int courseid, int section, Long itemid, Long time, Boolean visible,
                            String name, int beforemod) {
        if (beforemod == -1) {
            moodleClient.setResource("json", token, "local_course_add_new_course_module_resource", courseid, section,
                    itemid, time, visible, name, null);
        } else {
            moodleClient.setResource("json", token, "local_course_add_new_course_module_resource", courseid, section,
                    itemid, time, visible, name, beforemod);
        }
    }

    /**
     * Method used for deleting an exisiting course-module
     *
     * @param token The Moodle-token.
     * @param cmid  The course-modules id.
     */

    public void removeResource(String token, int cmid) {
        moodleClient.removeResource("json", token, "core_course_delete_modules", cmid);
    }


    /**
     * Create a course-module of the type "resource" at a specific position inside a course-section.
     *
     * @param token     The Moodle-token.
     * @param courseid  A Moodle-courses id.
     * @param section   The moodle-sections number in the moodle-course.
     * @param itemid    The id of the prior uploaded files, which should be presented by the course-module.
     * @param name      The displayname of the course-module.
     * @param beforemod The course-module id of the course-module at the supposed position. If beforemod ist null,
     *                  the course-module will be moved to the bottom of the course-section.
     */
    public void setFolder(String token, int courseid, int section, Long itemid, String name, Long time, boolean visible,
                          int beforemod) {
        if (beforemod == -1) {
            moodleClient.setFolder("json", token, "local_course_add_new_course_module_directory", courseid, section,
                    itemid, name, time, visible, null);
        } else {
            moodleClient.setFolder("json", token, "local_course_add_new_course_module_directory", courseid, section,
                    itemid, name, time, visible, beforemod);
        }
    }

    /**
     * Add a file or several files to an existing folder. Note: to upload several files, it is necessary that all
     * files are uploaded one after another with the same itemid.
     *
     * @param token     The Moodle-token.
     * @param courseid  A Moodle-courses id.
     * @param itemid    The id of the prior uploaded file(s), which should be presented by the course-module.
     * @param contextid The contextid of the folder the files should be added to.
     */
    public void addFilesToFolder(String token, int courseid, Long itemid, int contextid) {
        moodleClient.addFilesToFolder("json", token, "local_course_add_files_to_directory", courseid, itemid,
                contextid);
    }

    /**
     * Method used to create a new course section.
     *
     * @param token       The Moodle-token.
     * @param courseid    A Moodle-courses id.
     * @param sectionname The name of the new section.
     * @param sectionnum  The moodle-sections number in the moodle-course.
     */
    public void setSection(String token, int courseid, String sectionname, int sectionnum) {
        moodleClient.setSection("json", token, "local_course_add_new_section", courseid, sectionname, sectionnum);
    }

    public Boolean getPermissions(String token, int courseid) throws Exception {
        return moodleClient.getPermissions("json", token, "core_course_get_user_administration_options", courseid).getCourses().get(0).getOptions().get(0).getAvailable();
    }


    /**
     * Method user for generating a needed SSLContext for https-communication
     *
     * @return SSLContext
     */
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

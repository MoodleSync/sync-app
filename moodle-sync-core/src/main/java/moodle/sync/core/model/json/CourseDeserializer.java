package moodle.sync.core.model.json;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.JsonObject;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import java.lang.reflect.Type;

/**
 * Class which helps deserialize and debug a course
 */
public class CourseDeserializer implements JsonbDeserializer<Course> {
    private static final Logger LOG = LogManager.getLogger(CourseDeserializer.class);

    @Override
    public Course deserialize(JsonParser parser,
                              DeserializationContext deserializationContext, Type type) {
        JsonObject jsonObj = parser.getObject();

        System.out.println("-----------");
        System.out.println(jsonObj.toString());
        LOG.debug(jsonObj.toString());

        Integer id = null;
        String shortname = null;
        String displayname = null;
        String idnumber = null;
        Integer visible = null;
        Integer enddate = null;

        if (jsonObj.containsKey("id")) {
            id = jsonObj.getInt("id");
        }

        if (jsonObj.containsKey("shortname")) {
            shortname = jsonObj.getString("shortname");
        }

        if (jsonObj.containsKey("displayname")) {
            displayname = jsonObj.getString("displayname");
        }

        if (jsonObj.containsKey("idnumber")) {
            idnumber = jsonObj.getString("idnumber");
        }

        if (jsonObj.containsKey("visible")) {
            visible = jsonObj.getInt("visible");
        }

        if (jsonObj.containsKey("enddate")) {
            try{
                String temp = jsonObj.getJsonNumber("enddate").toString();
                System.out.println("Enddate: " + temp);
                LOG.debug("Enddate: " + temp);
                enddate = Integer.parseInt(temp);
            }catch (Throwable e){
                enddate = 0;
                System.out.println("Failed to parse enddate");
                LOG.error("Failed to parse enddate");
            }
        } else{
            enddate = 0;
        }

        Course course = new Course();
        course.setId(id);
        course.setShortname(shortname);
        course.setDisplayname(displayname);
        course.setIdnumber(idnumber);
        course.setVisible(visible);
        course.setEnddate(enddate);

        System.out.println("-----------");
        System.out.println(course);
        return course;
    }
}

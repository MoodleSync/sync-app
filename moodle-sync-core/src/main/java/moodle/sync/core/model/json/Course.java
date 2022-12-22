package moodle.sync.core.model.json;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

/**
 * Class representing a Moodle-Course.
 *
 * @author Daniel Schröter
 */
public class Course {
    private Integer id;
    private String shortname;
    private String displayname;
    private String idnumber ;
    private Integer visible;
    private Integer enddate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return shortname;
    }
}

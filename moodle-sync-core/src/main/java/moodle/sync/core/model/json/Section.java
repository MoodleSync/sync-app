package moodle.sync.core.model.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

/**
 * Class representing a course-section.
 *
 * @author Daniel Schröter
 */
public class Section {
    private Integer id;
    private String name;
    private Integer visible;
    private String summary;
    private Integer summaryformat;
    private Integer section;
    private Integer hiddenbynumsections;
    private Boolean uservisible;
    private List<Module> modules;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}

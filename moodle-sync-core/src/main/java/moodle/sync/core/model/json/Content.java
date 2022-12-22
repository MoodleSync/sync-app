package moodle.sync.core.model.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

/**
 * Class representing a course-modules content.
 *
 * @author Daniel Schröter
 */
public class Content {
    private String filename;
    private Long timemodified;
}
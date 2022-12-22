package moodle.sync.core.model.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

/**
 * Class representing the response-object of a file upload.
 *
 * @author Daniel Schröter
 */
public class MoodleUpload {
    private String filename;
    private Long itemid;
}

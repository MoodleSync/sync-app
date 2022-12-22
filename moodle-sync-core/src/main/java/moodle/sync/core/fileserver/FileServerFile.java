package moodle.sync.core.fileserver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

/**
 * Class representing a file uploaded to a fileserver.
 *
 * @author Daniel Schröter
 */
public class FileServerFile {

    private String filename;

    private Long lastTimeModified;

}

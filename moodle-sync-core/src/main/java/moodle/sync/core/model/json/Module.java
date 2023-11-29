package moodle.sync.core.model.json;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

/**
 * Class representing a course-module.
 *
 * @author Daniel Schröter
 */
public class Module {
    private Integer id;
    private String url;
    private String name;
    private Integer instance;
    private Integer contextid;
    private String description;
    private Integer visible;
    private Boolean uservisible;
    private String modname;
    private String availability;
    private List<Content> contents;


    public Module(List<Content> contents){
        this.contents = contents;
    }
}

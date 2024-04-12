package moodle.sync.core.model.json;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PanoptoContent {

    private String Name;

    private String Description;

    private String StartTime;

    private PanoptoContentUrls Urls;
}

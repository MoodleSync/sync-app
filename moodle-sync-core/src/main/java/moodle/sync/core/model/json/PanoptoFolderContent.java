package moodle.sync.core.model.json;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PanoptoFolderContent {

    private List<PanoptoContent> Results;

}

package moodle.sync.core.model.json;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PanoptoSessionComplete {

    private String ID;

    private String UploadTarget;

    private String FolderId;

    private String State;

    private String SessionId;

}

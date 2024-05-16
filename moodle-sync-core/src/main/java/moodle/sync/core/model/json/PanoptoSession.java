package moodle.sync.core.model.json;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PanoptoSession {

    private String ID;

    private String UploadTarget;

    private String FolderId;

    private String SessionId;

}


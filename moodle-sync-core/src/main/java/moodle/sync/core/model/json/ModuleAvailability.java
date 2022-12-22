package moodle.sync.core.model.json;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ModuleAvailability {

    @JsonRawValue
    private TimeDateCondition[] c;
    @JsonRawValue
    private Boolean[] showc;

    public TimeDateCondition getTimeDateCondition(){
        return c != null ? c[0] : null;
    }

    public Boolean getConditionVisibility(){
        return showc != null ? showc[0] : null;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class TimeDateCondition {
        String type;
        String d; //Duration
        Long t;
    }

}

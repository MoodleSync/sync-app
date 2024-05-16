package moodle.sync.core.model.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import moodle.sync.core.config.MoodleSyncConfiguration;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PanoptoCourse {

    private String Description;

    private String ParentFolder;

    private PanoptoUrls Urls;

    private String Id;

    private String Name;

    public boolean equals(PanoptoCourse o) {
        return Objects.equals(this.Description, o.Description) &&
                Objects.equals(this.ParentFolder, o.ParentFolder) &&
                Objects.equals(this.Urls, o.Urls) &&
                Objects.equals(this.Id, o.Id) &&
                Objects.equals(this.Name, o.Name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Description, ParentFolder, Urls, Id, Name);
    }
}

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
 * Class representing the necessary parameters of the http-response to the api-call: getSiteInfo.
 *
 * @author Daniel Schröter
 */
public class SiteInfo {
    private int userid;
}

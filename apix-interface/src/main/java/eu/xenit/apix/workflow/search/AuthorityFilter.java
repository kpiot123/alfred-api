package eu.xenit.apix.workflow.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorityFilter implements IQueryFilter {

    public static final String TYPE = "AuthorityFilter";
    private String property;
    private String value;

    @JsonCreator
    public AuthorityFilter(@JsonProperty("value") String value, @JsonProperty("property") String property,
            @JsonProperty("type") String type) {
        this.value = value;
        this.property = property;
    }

    public String getValue() {
        return this.value;
    }

    public String getProperty() {
        return this.property;
    }

    public String getType() {
        return "AuthorityFilter";
    }
}

package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record IssueUpdatePayload(@JsonProperty("status_id") Long statusId, String notes,
                                 @JsonProperty("custom_fields") List<CustomFieldInput> customFields) {
    public IssueUpdatePayload(Long statusId, String notes) {
        this(statusId, notes, null);
    }
}
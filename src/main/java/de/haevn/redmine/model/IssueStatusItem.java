package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IssueStatusItem(long id, String name, @JsonProperty("is_closed") boolean isClosed,
                              @JsonProperty("is_default") Boolean isDefault) {
}

package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EnumerationItem(long id, String name, @JsonProperty("is_default") boolean isDefault, boolean active) {
}

package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomFieldInput(long id, Object value) {
}
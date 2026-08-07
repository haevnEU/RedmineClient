package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IssueUpdatePayload(@JsonProperty("status_id") Long statusId, String notes) {
}
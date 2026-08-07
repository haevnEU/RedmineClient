package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TimeEntryPayload(@JsonProperty("issue_id") Long issueId, @JsonProperty("hours") double hours,
                               @JsonProperty("activity_id") Long activityId, @JsonProperty("comments") String comments,
                               @JsonProperty("spent_on") String spentOn) {
}
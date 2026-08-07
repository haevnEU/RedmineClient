package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateTimeEntryRequest(@JsonProperty("time_entry") TimeEntryPayload timeEntry) {
}

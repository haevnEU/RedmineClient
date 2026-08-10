package de.haevn.redmine.api;

import de.haevn.redmine.model.RedmineInfoResponses;

public enum InfoType {
    STATUS("/issue_statuses.json", RedmineInfoResponses.IssueStatusesResponse.class), PRIORITY(
        "/enumerations/issue_priorities.json", RedmineInfoResponses.PrioritiesResponse.class), ACTIVITY(
        "/enumerations/time_entry_activities.json", RedmineInfoResponses.ActivitiesResponse.class);

    public final String endpoint;
    public final Class<?> responseWrapperType;

    InfoType(final String endpoint, final Class<?> responseWrapperType) {
        this.endpoint = endpoint;
        this.responseWrapperType = responseWrapperType;
    }
}

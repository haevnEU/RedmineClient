package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RedmineInfoResponses {

    public record InfoResponse(long id, String name, Boolean isDefault) {
    }


    public record IssueStatusesResponse(@JsonProperty("issue_statuses") List<IssueStatusItem> issueStatuses) {
    }


    public record PrioritiesResponse(@JsonProperty("issue_priorities") List<EnumerationItem> issuePriorities) {
    }


    public record ActivitiesResponse(@JsonProperty("time_entry_activities") List<EnumerationItem> timeEntryActivities) {
    }
}

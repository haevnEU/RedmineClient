package de.haevn.redmine.api;

import de.haevn.redmine.internal.client.DefaultRedmineClient;
import de.haevn.redmine.model.Checkbox;
import de.haevn.redmine.model.Issue;
import de.haevn.redmine.model.RedmineInfoResponses;
import java.util.List;
import java.util.Optional;

public interface RedmineClient {

    static RedmineClient create(final String baseUrl, final String apiKey) {
        return new DefaultRedmineClient(baseUrl, apiKey);
    }

    List<Issue> getIssues(final String projectIdentifier, final QueryParams... params) throws RedmineException;

    List<Issue> getMyAssignedIssues(final QueryParams... params) throws RedmineException;

    Optional<Issue> getIssueById(final long id, final QueryParams... params) throws RedmineException;

    List<Checkbox> getCheckboxes(final long ticketId) throws RedmineException;

    void addComment(final long ticketId, final String comment) throws RedmineException;

    void addChecklistItem(final long ticketId, final String description) throws RedmineException;

    void tickCheckbox(final long ticketId, final long checkboxId, final boolean state) throws RedmineException;

    void moveToStatus(final long ticketId, final long statusId) throws RedmineException;

    void moveToStatus(final long ticketId, final long statusId, final String comment) throws RedmineException;

    void logTime(final long ticketId, final int hours, final int minutes, final String comment, final long activityId)
        throws RedmineException;

    void logTime(final long ticketId, final int hours, final int minutes, final String comment, final long activityId,
        final String spentOn) throws RedmineException;

    void setCustomField(final long ticketId, final String value, final long customFieldId) throws RedmineException;

    void setCustomField(final long ticketId, final List<String> value, final long customFieldId)
        throws RedmineException;

    List<RedmineInfoResponses.InfoResponse> getInfo(final InfoType infoType) throws RedmineException;
}
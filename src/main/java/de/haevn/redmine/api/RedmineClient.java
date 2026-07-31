package de.haevn.redmine.api;
import de.haevn.redmine.internal.client.DefaultRedmineClient;
import de.haevn.redmine.model.Issue;
import java.util.List;
import java.util.Optional;

public interface RedmineClient {

    List<Issue> getIssues(final String projectIdentifier, final QueryParams ... params) throws RedmineException;

    List<Issue> getMyAssignedIssues(final QueryParams ... params) throws RedmineException;

    Optional<Issue> getIssueById(final long id, final QueryParams ... params) throws RedmineException;

    static RedmineClient create(final String baseUrl, final String apiKey) {
        return new DefaultRedmineClient(baseUrl, apiKey);
    }
}
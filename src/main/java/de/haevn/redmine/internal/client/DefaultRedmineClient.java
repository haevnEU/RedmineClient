package de.haevn.redmine.internal.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.haevn.redmine.api.QueryParams;
import de.haevn.redmine.api.RedmineClient;
import de.haevn.redmine.api.RedmineException;
import de.haevn.redmine.internal.dto.IssueListResponse;
import de.haevn.redmine.internal.dto.SingleIssueResponse;
import de.haevn.redmine.internal.utils.RedmineSerializer;
import de.haevn.redmine.model.Issue;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class DefaultRedmineClient implements RedmineClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String apiKey;

    public DefaultRedmineClient(final String baseUrl, final String apiKey) {
        this(baseUrl, apiKey, HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build());
    }

    public DefaultRedmineClient(final String baseUrl, final String apiKey, final HttpClient httpClient) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.objectMapper = RedmineSerializer.create();
    }

    @Override
    public List<Issue> getIssues(final String projectIdentifier, final QueryParams... params) throws RedmineException {
        final String path = String.format("/issues.json?project_id=%s&include=%s", projectIdentifier,
            QueryParams.toQueryString(params));
        return executeGetRequest(path, IssueListResponse.class).issues();
    }

    @Override
    public List<Issue> getMyAssignedIssues(final QueryParams... params) throws RedmineException {
        final String path =
            String.format("/issues.json?assigned_to_id=me&include=%s", QueryParams.toQueryString(params));
        return executeGetRequest(path, IssueListResponse.class).issues();
    }

    @Override
    public Optional<Issue> getIssueById(final long id, final QueryParams... params) throws RedmineException {
        final String path = String.format("/issues/%d.json?include=%s", id, QueryParams.toQueryString(params));
        try {
            final SingleIssueResponse response = executeGetRequest(path, SingleIssueResponse.class);
            return Optional.ofNullable(response.issue());
        } catch (RedmineException e) {
            if (e.getStatusCode() == 404) {
                return Optional.empty();
            }
            throw e;
        }
    }

    private <T> T executeGetRequest(final String path, final Class<T> targetType) throws RedmineException {
        final URI uri = URI.create(baseUrl + path);

        final HttpRequest request =
            HttpRequest.newBuilder().uri(uri).header("X-Redmine-API-Key", apiKey).header("Accept", "application/json")
                .GET().build();

        try {
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), targetType);
            } else {
                throw new RedmineException(
                    "Redmine API Request fehlgeschlagen. Status: " + response.statusCode() + " Body: "
                        + response.body(), response.statusCode());
            }
        } catch (Exception e) {
            if (e instanceof RedmineException RedmineException) {
                throw RedmineException;
            }
            throw new RedmineException("Fehler bei der Kommunikation mit Redmine", e);
        }
    }
}

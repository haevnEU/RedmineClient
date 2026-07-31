package de.haevn.redmine.internal.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.haevn.redmine.api.QueryParams;
import de.haevn.redmine.api.RedmineClient;
import de.haevn.redmine.api.RedmineException;
import de.haevn.redmine.internal.dto.CheckboxListResponse;
import de.haevn.redmine.internal.dto.IssueListResponse;
import de.haevn.redmine.internal.dto.SingleIssueResponse;
import de.haevn.redmine.internal.utils.RedmineSerializer;
import de.haevn.redmine.model.Checkbox;
import de.haevn.redmine.model.Issue;
import java.io.IOException;
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
        return executeGetRequest(path, IssueListResponse.class).orElse(new IssueListResponse(List.of())).issues();
    }

    @Override
    public List<Issue> getMyAssignedIssues(final QueryParams... params) throws RedmineException {
        final String path =
            String.format("/issues.json?assigned_to_id=me&include=%s", QueryParams.toQueryString(params));
        return executeGetRequest(path, IssueListResponse.class).orElse(new IssueListResponse(List.of())).issues();
    }

    @Override
    public Optional<Issue> getIssueById(final long id, final QueryParams... params) throws RedmineException {
        final String path = String.format("/issues/%d.json?include=%s", id, QueryParams.toQueryString(params));
        try {
            final Optional<SingleIssueResponse> response = executeGetRequest(path, SingleIssueResponse.class);
            return response.map(SingleIssueResponse::issue);
        } catch (RedmineException e) {
            if (e.getStatusCode() == 404) {
                return Optional.empty();
            }
            throw e;
        }
    }

    @Override
    public List<Checkbox> getCheckboxes(final long id) throws RedmineException {
        final String path = String.format("/issues/%s/checklist.json?", id);
        final CheckboxListResponse response =
            executeGetRequest(path, CheckboxListResponse.class).orElse(new CheckboxListResponse(List.of()));
        return response.checklists();
    }

    @Override
    public void addComment(long ticketId, final String comment) throws RedmineException {

    }

    @Override
    public void addChecklistItem(long ticketId, final String description) throws RedmineException {

    }

    @Override
    public void tickCheckbox(final long ticketId, final long checkboxId, final boolean state) throws RedmineException {

    }

    @Override
    public void moveToStatus(final long ticketId, final long statusId) {

    }

    private <T, R> Optional<R> executeRequest(final String path, final RequestMethod method, final Optional<T> body,
        final Class<R> responseType) throws RedmineException {
        try {
            final URI uri = URI.create(baseUrl + path);
            final HttpRequest.BodyPublisher bodyPublisher;

            final var requestBuilder = HttpRequest.newBuilder().uri(uri).header("X-Redmine-API-Key", apiKey)
                .header("Accept", "application/json");
            if (body.isPresent()) {
                bodyPublisher = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body.get()));
                requestBuilder.header("Content-Type", "application/json");
            } else {
                bodyPublisher = HttpRequest.BodyPublishers.noBody();
            }

            requestBuilder.method(method.name(), bodyPublisher);

            final HttpResponse<String> response =
                httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body() == null ?
                    Optional.empty() :
                    Optional.of(objectMapper.readValue(response.body(), responseType));
            } else {
                throw new RedmineException(
                    "Redmine API Request fehlgeschlagen. Status: " + response.statusCode() + " Body: "
                        + response.body(), response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof RedmineException RedmineException) {
                throw RedmineException;
            }
            throw new RedmineException("Fehler bei der Kommunikation mit Redmine", e);
        }
    }

    private <T> boolean executeDeleteRequest(final String path, final Optional<T> body)
        throws JsonProcessingException, RedmineException {
        throw new RuntimeException("Not implemented yet");
    }

    private <T> boolean executePostRequest(final String path, final Optional<T> body)
        throws JsonProcessingException, RedmineException {
        throw new RuntimeException("Not implemented yet");
    }

    private <T> boolean executePutRequest(final String path, final Optional<T> body)
        throws JsonProcessingException, RedmineException {
        throw new RuntimeException("Not implemented yet");
    }

    private <T> boolean executePatchRequest(final String path, final Optional<T> body)
        throws JsonProcessingException, RedmineException {
        throw new RuntimeException("Not implemented yet");
    }

    private <T> Optional<T> executeGetRequest(final String path, final Class<T> targetType) throws RedmineException {
        return executeRequest(path, RequestMethod.GET, Optional.empty(), targetType);
    }

    private enum RequestMethod {
        GET, POST, PUT, DELETE, PATCH
    }
}

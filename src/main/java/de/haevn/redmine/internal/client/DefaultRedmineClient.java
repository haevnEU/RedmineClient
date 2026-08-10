package de.haevn.redmine.internal.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.haevn.redmine.api.InfoType;
import de.haevn.redmine.api.QueryParams;
import de.haevn.redmine.api.RedmineClient;
import de.haevn.redmine.api.RedmineException;
import de.haevn.redmine.internal.dto.CheckboxListResponse;
import de.haevn.redmine.internal.dto.IssueListResponse;
import de.haevn.redmine.internal.dto.SingleIssueResponse;
import de.haevn.redmine.internal.utils.RedmineSerializer;
import de.haevn.redmine.model.Checkbox;
import de.haevn.redmine.model.CreateTimeEntryRequest;
import de.haevn.redmine.model.CustomFieldInput;
import de.haevn.redmine.model.Issue;
import de.haevn.redmine.model.IssueUpdatePayload;
import de.haevn.redmine.model.RedmineInfoResponses;
import de.haevn.redmine.model.TimeEntryPayload;
import de.haevn.redmine.model.UpdateIssueRequest;
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
        updateIssue(ticketId, null, comment, null);
    }

    @Override
    public void addChecklistItem(long ticketId, final String description) throws RedmineException {

    }

    @Override
    public void tickCheckbox(final long ticketId, final long checkboxId, final boolean state) throws RedmineException {

    }

    @Override
    public void moveToStatus(final long ticketId, final long statusId) throws RedmineException {
        updateIssue(ticketId, statusId, null, null);
    }

    @Override
    public void moveToStatus(final long ticketId, final long statusId, final String comment) throws RedmineException {
        updateIssue(ticketId, statusId, comment, null);
    }

    @Override
    public void logTime(final long ticketId, final int hours, final int minutes, final String comment,
        final long activityId) throws RedmineException {
        logTime(ticketId, hours, minutes, comment, activityId, null);
    }

    @Override
    public void logTime(final long ticketId, final int hours, final int minutes, final String comment,
        final long activityId, final String spentOn) throws RedmineException {
        if (spentOn != null && !DateUtils.isValidIsoDate(spentOn)) {
            throw new RedmineException("Invalid date format for spentOn. Expected format: YYYY-MM-DD", 400);
        }
        if (minutes < 0 || minutes >= 60 || hours < 0) {
            throw new IllegalArgumentException("Invalid hours or minutes values");
        }
        final double time = hours + (minutes / 60.0);

        final String path = "/time_entries.json";

        final var entry = new TimeEntryPayload(ticketId, time, activityId, comment, spentOn);
        final var payload = new CreateTimeEntryRequest(entry);
        executeRequest(path, RequestMethod.POST, Optional.of(payload), Void.class);
    }

    @Override
    public void setCustomField(final long ticketId, final String value, final long customFieldId)
        throws RedmineException {
        updateIssue(ticketId, null, null, List.of(new CustomFieldInput(customFieldId, value)));
    }

    @Override
    public void setCustomField(final long ticketId, final List<String> value, final long customFieldId)
        throws RedmineException {
        updateIssue(ticketId, null, null, List.of(new CustomFieldInput(customFieldId, value)));
    }

    public List<RedmineInfoResponses.InfoResponse> getInfo(final InfoType infoType) throws RedmineException {
        final var responseOpt =
            executeRequest(infoType.endpoint, RequestMethod.GET, Optional.empty(), infoType.responseWrapperType);

        if (responseOpt.isEmpty()) {
            return List.of();
        }

        final Object response = responseOpt.get();

        return switch (infoType) {
            case STATUS -> {
                final var statusesWrapper = (RedmineInfoResponses.IssueStatusesResponse) response;
                yield statusesWrapper.issueStatuses().stream()
                    .map(item -> new RedmineInfoResponses.InfoResponse(item.id(), item.name(), item.isDefault()))
                    .toList();
            }
            case PRIORITY -> {
                final var prioritiesWrapper = (RedmineInfoResponses.PrioritiesResponse) response;
                yield prioritiesWrapper.issuePriorities().stream()
                    .map(item -> new RedmineInfoResponses.InfoResponse(item.id(), item.name(), item.isDefault()))
                    .toList();
            }
            case ACTIVITY -> {
                final var activitiesWrapper = (RedmineInfoResponses.ActivitiesResponse) response;
                yield activitiesWrapper.timeEntryActivities().stream()
                    .map(item -> new RedmineInfoResponses.InfoResponse(item.id(), item.name(), item.isDefault()))
                    .toList();
            }
        };
    }

    private void updateIssue(final long ticketId, final Long statusId, final String comment,
        final List<CustomFieldInput> customFields) throws RedmineException {
        final String path = String.format("/issues/%d.json", ticketId);
        final var payload = new UpdateIssueRequest(new IssueUpdatePayload(statusId, comment, customFields));
        executeRequest(path, RequestMethod.PUT, Optional.of(payload), Void.class);
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
                final String responseBody = response.body();
                if (responseBody == null || responseBody.isBlank() || responseType == Void.class) {
                    return Optional.empty();
                }
                return Optional.of(objectMapper.readValue(responseBody, responseType));
            } else {
                throw new RedmineException(
                    "Redmine API request failed. Status: " + response.statusCode() + " Body: " + response.body(),
                    response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof RedmineException redmineException) {
                throw redmineException;
            }
            throw new RedmineException("An error occurred while communicating with Redmine", e);
        }
    }

    private <T> boolean executeDeleteRequest(final String path, final Optional<T> body)
        throws JsonProcessingException, RedmineException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private <T> boolean executePostRequest(final String path, final Optional<T> body)
        throws JsonProcessingException, RedmineException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private <T> boolean executePutRequest(final String path, final Optional<T> body)
        throws JsonProcessingException, RedmineException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private <T> boolean executePatchRequest(final String path, final Optional<T> body)
        throws JsonProcessingException, RedmineException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private <T> Optional<T> executeGetRequest(final String path, final Class<T> targetType) throws RedmineException {
        return executeRequest(path, RequestMethod.GET, Optional.empty(), targetType);
    }

    private enum RequestMethod {
        GET, POST, PUT, DELETE, PATCH
    }
}
package de.haevn.redmine.internal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.haevn.redmine.model.Issue;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IssueListResponse(List<Issue> issues) {}

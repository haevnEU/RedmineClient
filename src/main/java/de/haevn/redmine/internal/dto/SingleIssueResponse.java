package de.haevn.redmine.internal.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.haevn.redmine.model.Issue;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SingleIssueResponse(Issue issue) {}

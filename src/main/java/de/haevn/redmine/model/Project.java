package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Project(
    long id,
    String name,
    String identifier,
    String description,
    Instant createdAt,
    Instant updatedAt,
    List<Issue> tickets
) {}
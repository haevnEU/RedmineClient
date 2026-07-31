package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Relation(
    long id,
    long issueId,
    long issueToId,
    String relationType,
    String delay
) {}
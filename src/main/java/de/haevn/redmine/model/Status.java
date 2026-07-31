package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Status(
    long id,
    String name,
    boolean isClosed
) {}
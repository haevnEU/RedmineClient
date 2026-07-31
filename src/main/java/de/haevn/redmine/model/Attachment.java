package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Attachment(
    long id,
    String filename,
    long filesize,
    String contentType,
    String description,
    String contentUrl,
    String thumbnailUrl,
    Author author,
    Instant createdOn
) {}
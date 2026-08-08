package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Issue(
    Long id,
    Project project,
    Tracker tracker,
    Status status,
    Priority priority,
    Author author,
    Author assignedTo,
    String subject,
    String description,

    // Reine Datumsangaben (YYYY-MM-DD) -> LocalDate verwenden:
    LocalDate startDate,
    LocalDate dueDate,

    Integer doneRatio,
    Boolean isPrivate,
    Double estimatedHours,
    Double spentHours,
    Double totalSpentHours,

    // Zeitstempel mit Uhrzeit -> Instant beibehalten:
    Instant createdOn,
    Instant updatedOn,
    Instant closedOn,

    List<Attachment> attachments,
    List<Journal> journals,
    List<Relation> relations,
    List<CustomField> customFields
) {}
package de.haevn.redmine.model;

import java.time.Instant;

public record Checkbox(long id, long issueId, String subject, boolean isDone, int position, boolean isSection,
                       Instant createdAt, Instant updatedAt) {
}

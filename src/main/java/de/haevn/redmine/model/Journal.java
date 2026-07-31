package de.haevn.redmine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Journal(Long id, Author user, String notes, Boolean privateNotes, Instant createdOn,
                      List<JournalDetail> details) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JournalDetail(String property, String name, String oldValue, String newValue) {
    }
}

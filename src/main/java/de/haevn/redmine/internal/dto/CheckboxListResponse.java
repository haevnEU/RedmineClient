package de.haevn.redmine.internal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.haevn.redmine.model.Checkbox;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckboxListResponse(List<Checkbox> checklists) {
}

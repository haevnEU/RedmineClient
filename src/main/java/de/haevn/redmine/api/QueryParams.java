package de.haevn.redmine.api;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum QueryParams {

    JOURNALS("journals"),
    ATTACHMENTS("attachments"),
    RELATIONS("relations"),
    CHILDREN("children"),
    SUBTASKS("subtasks");
    public final String parameterName;

    QueryParams(final String parameterName) {
        this.parameterName = parameterName;
    }

    public static String toQueryString(final QueryParams[] params) {
        return Stream.of(params).map(QueryParams::getParameterName).collect(Collectors.joining(","));
    }

    public String getParameterName() {
        return parameterName;
    }
}

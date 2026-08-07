package de.haevn.redmine.internal.client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

final class DateUtils {
    private static final DateTimeFormatter ISO_DATE_FORMATTER =
        DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT);

    private DateUtils() {
        // An instantiation is not allowed
    }

    public static boolean isValidIsoDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return false;
        }
        try {
            LocalDate.parse(dateStr, ISO_DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException ignored) {
            return false;
        }
    }
}

package de.haevn.redmine.api;

public class RedmineException extends Exception {

    private final int statusCode;

    public RedmineException(final String message, final int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public RedmineException(final String message, final Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
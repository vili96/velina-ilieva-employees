package org.example.exception;

public class DateParseException extends RuntimeException {
    public DateParseException(String message) {
        super(message);
    }
}

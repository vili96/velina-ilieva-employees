package org.example.exception;

public final class CsvValidationException extends RuntimeException {
    public CsvValidationException(String message) {
        super(message);
    }
}

package com.theliems.lokigame.infrastructure.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends AppException {
    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
    }

    public ValidationException(String message, Object... args) {
        super(message, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", args);
    }
}

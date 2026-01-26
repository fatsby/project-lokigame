package com.theliems.lokigame.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String errorCode;
    private final Object[] args;

    public AppException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.args = null;
    }

    public AppException(String message, HttpStatus httpStatus, String errorCode, Object... args) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.args = args;
    }

    public AppException(String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.args = null;
    }
}

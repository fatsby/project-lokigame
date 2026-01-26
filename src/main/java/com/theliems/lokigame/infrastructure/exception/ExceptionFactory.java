package com.theliems.lokigame.infrastructure.exception;

import org.springframework.stereotype.Component;

@Component
public class ExceptionFactory {

    public ResourceNotFoundException resourceNotFound(String resourceName, Object identifier) {
        return new ResourceNotFoundException(resourceName, identifier);
    }

    public ValidationException validationError(String message) {
        return new ValidationException(message);
    }

    public ValidationException validationError(String message, Object... args) {
        return new ValidationException(message, args);
    }

    public UnauthorizedException unauthorized(String message) {
        return new UnauthorizedException(message);
    }

    public ForbiddenException forbidden(String message) {
        return new ForbiddenException(message);
    }

    public AppException badRequest(String message) {
        return new AppException(message, org.springframework.http.HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public AppException internalError(String message) {
        return new AppException(message, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }
}

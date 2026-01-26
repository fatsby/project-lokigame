package com.theliems.lokigame.infrastructure.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(
                String.format("%s with identifier '%s' not found", resourceName, identifier),
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                resourceName,
                identifier
        );
    }
}

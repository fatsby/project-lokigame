package com.theliems.lokigame.infrastructure.exception.exceptions;

import java.util.List;

import com.theliems.lokigame.infrastructure.exception.errorCategories.ValidationError;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ValidationException extends AppException {

    List<FieldValidationError> fieldErrors;

    public ValidationException(List<FieldValidationError> fieldErrors) {
        super(ValidationError.INVALID_FIELDS);
        this.fieldErrors = fieldErrors;
    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class FieldValidationError {
        String field;
        String errorCode;
        String message;
    }
}

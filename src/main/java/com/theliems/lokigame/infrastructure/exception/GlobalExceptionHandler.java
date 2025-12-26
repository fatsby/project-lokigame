package com.theliems.lokigame.infrastructure.exception;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.theliems.lokigame.infrastructure.exception.handlers.SqlExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.theliems.lokigame.infrastructure.exception.errorCategories.SystemError;
import com.theliems.lokigame.infrastructure.exception.errorCategories.ValidationError;
import com.theliems.lokigame.infrastructure.exception.exceptions.AppException;
import com.theliems.lokigame.infrastructure.exception.exceptions.ValidationException;
import com.theliems.lokigame.infrastructure.exception.handlers.AppExceptionHandler;
import com.theliems.lokigame.infrastructure.exception.handlers.ExceptionHandlerInterface;
import com.theliems.lokigame.infrastructure.exception.handlers.ValidationExceptionHandler;
import com.theliems.lokigame.model.dto.api.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
        List<ExceptionHandlerInterface<? extends Exception>> exceptionHandlers;

    @Autowired
    public GlobalExceptionHandler(List<ExceptionHandlerInterface<? extends Exception>> exceptionHandlers) {
        this.exceptionHandlers = exceptionHandlers;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception exception) {

        for (ExceptionHandlerInterface<? extends Exception> handler : exceptionHandlers) {
            if (handler.canHandle(exception)) {
                return ((ExceptionHandlerInterface<Exception>) handler).handle(exception);
            }
        }

        return handleGenericException(exception);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiResponse<?>> handleSqlException(SQLException exception) {
        return new SqlExceptionHandler().handle(exception);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolation(
            DataIntegrityViolationException exception) {

        // Extract root cause (usually SQLException)
        Throwable rootCause = exception.getRootCause();
        if (rootCause instanceof SQLException) {
            return new SqlExceptionHandler().handle((SQLException) rootCause);
        }

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(SystemError.SQL_CONSTRAINT_VIOLATION.getCode())
                .message("Data integrity violation: " + exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ValidationException.FieldValidationError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        ValidationException validationException = new ValidationException(fieldErrors);
        return new ValidationExceptionHandler().handle(validationException);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException exception) {
        AppException appException = new AppException(SystemError.UNAUTHORIZED);
        return new AppExceptionHandler().handle(appException);
    }

    private ValidationException.FieldValidationError mapFieldError(FieldError fieldError) {
        String errorMessage = fieldError.getDefaultMessage();
        String fieldName = fieldError.getField();
        String capitalizedFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        String errorKey;
        String[] params = new String[0];

        if (errorMessage != null && errorMessage.contains(":")) {
            String[] parts = errorMessage.split(":");
            errorKey = parts[0];
            if (parts.length > 1) {
                params = Arrays.copyOfRange(parts, 1, parts.length);
            }
        } else {
            errorKey = errorMessage;
        }

        ErrorCode errorCode = findErrorCodeByKey(errorKey, params);
        String finalMessage = errorCode.getMessage().replace("This field", capitalizedFieldName);

        return new ValidationException.FieldValidationError(fieldName, errorKey, finalMessage);
    }

    private ErrorCode findErrorCodeByKey(String errorKey, String... params) {
        try {
            ValidationError validationError = ValidationError.valueOf(errorKey);
            String formattedMessage = validationError.getFormattedMessage((Object[]) params);

            return new ErrorCode(validationError.getCode(), formattedMessage, validationError.getStatusCode());
        } catch (IllegalArgumentException e) {
            // If the key is not in ValidationError, we might want to return a generic error or the original message
            // Adaptation: The original code used ValidationError.KEY_INVALID. Let's stick to that if possible,
            // or if the errorKey is just a plain message (not a key), we might want to handle it.
            // For now, I'll stick to the logic provided.
            return new ErrorCode(ValidationError.KEY_INVALID.getCode(),
                    ValidationError.KEY_INVALID.getMessage(),
                    ValidationError.KEY_INVALID.getStatusCode());
        }
    }

    private ResponseEntity<ApiResponse<?>> handleGenericException(Exception exception) {
        exception.printStackTrace(); // Good for debugging locally
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(500)
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}

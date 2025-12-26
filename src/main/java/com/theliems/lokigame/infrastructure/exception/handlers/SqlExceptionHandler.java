package com.theliems.lokigame.infrastructure.exception.handlers;

import com.theliems.lokigame.infrastructure.exception.ErrorCode;
import com.theliems.lokigame.infrastructure.exception.errorCategories.SystemError;
import com.theliems.lokigame.model.dto.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class SqlExceptionHandler implements ExceptionHandlerInterface<SQLException> {

    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof SQLException ||
                exception.getCause() instanceof SQLException;
    }

    @Override
    public ResponseEntity<ApiResponse<?>> handle(SQLException exception) {
        String message = exception.getMessage();
        ErrorCode errorCode;

        if (message.contains("cannot insert the value NULL") ||
                message.contains("column does not allow nulls")) {
            String fieldName = extractFieldName(message);
            errorCode = new ErrorCode(
                    SystemError.NULL_VALUE_NOT_ALLOWED.getCode(),
                    String.format(SystemError.NULL_VALUE_NOT_ALLOWED.getMessage(), fieldName),
                    SystemError.NULL_VALUE_NOT_ALLOWED.getStatusCode()
            );
        }
        else if (message.contains("FOREIGN KEY constraint") ||
                message.contains("PRIMARY KEY constraint") ||
                message.contains("UNIQUE constraint")) {
            errorCode = new ErrorCode(
                    SystemError.SQL_CONSTRAINT_VIOLATION.getCode(),
                    String.format(SystemError.SQL_CONSTRAINT_VIOLATION.getMessage(),
                            extractConstraintInfo(message)),
                    SystemError.SQL_CONSTRAINT_VIOLATION.getStatusCode()
            );
        }
        else {
            errorCode = new ErrorCode(
                    SystemError.SQL_CONSTRAINT_VIOLATION.getCode(),
                    "Database operation failed: " + message,
                    SystemError.SQL_CONSTRAINT_VIOLATION.getStatusCode()
            );
        }

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    private String extractFieldName(String errorMessage) {
        try {
            int startIndex = errorMessage.indexOf("column '") + 8;
            int endIndex = errorMessage.indexOf("'", startIndex);
            if (startIndex > 7 && endIndex > startIndex) {
                return errorMessage.substring(startIndex, endIndex);
            }
        } catch (Exception e) {
        }
        return "required field";
    }

    private String extractConstraintInfo(String errorMessage) {
        if (errorMessage.contains("FOREIGN KEY")) {
            return "Foreign key constraint violated";
        } else if (errorMessage.contains("PRIMARY KEY")) {
            return "Primary key constraint violated";
        } else if (errorMessage.contains("UNIQUE")) {
            return "Unique constraint violated";
        }
        return "Constraint violated";
    }
}

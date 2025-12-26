package com.theliems.lokigame.infrastructure.exception.handlers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.theliems.lokigame.infrastructure.exception.ErrorCode;
import com.theliems.lokigame.infrastructure.exception.exceptions.AppException;
import com.theliems.lokigame.model.dto.api.ApiResponse;

@Component
public class AppExceptionHandler implements ExceptionHandlerInterface<AppException> {
    
    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof AppException;
    }
    
    @Override
    public ResponseEntity<ApiResponse<?>> handle(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        
        ApiResponse.ApiResponseBuilder<Object> responseBuilder = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage());
        
        if (!exception.getContext().isEmpty()) {
            responseBuilder.result(exception.getContext());
        }
        
        return ResponseEntity.status(errorCode.getStatusCode()).body(responseBuilder.build());
    }
}

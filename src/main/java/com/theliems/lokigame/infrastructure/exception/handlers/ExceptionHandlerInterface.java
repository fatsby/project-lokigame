package com.theliems.lokigame.infrastructure.exception.handlers;
import org.springframework.http.ResponseEntity;

import com.theliems.lokigame.model.dto.api.ApiResponse;

public interface ExceptionHandlerInterface<T extends Exception> {
    boolean canHandle(Exception exception);

    ResponseEntity<ApiResponse<?>> handle(T exception);
}

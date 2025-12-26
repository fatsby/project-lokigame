package com.theliems.lokigame.infrastructure.exception;
import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ErrorCode {
    int code;
    String message;
    HttpStatusCode statusCode;
}

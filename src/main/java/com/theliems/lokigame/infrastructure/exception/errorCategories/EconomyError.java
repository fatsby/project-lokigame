package com.theliems.lokigame.infrastructure.exception.errorCategories;

import com.theliems.lokigame.infrastructure.exception.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public enum EconomyError implements ErrorCodeInterface {
    // 4000 - 4999
    INSUFFICIENT_FUNDS(4000, "Player does not have enough currency.", HttpStatus.BAD_REQUEST),
    NEGATIVE_AMOUNT(4001, "Currency transaction amount must be positive.", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;
}

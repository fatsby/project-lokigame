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
public enum PlayerError implements ErrorCodeInterface {
    // 1000 - 1999
    PLAYER_NOT_FOUND(1000, "Player not found in database.", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;
}

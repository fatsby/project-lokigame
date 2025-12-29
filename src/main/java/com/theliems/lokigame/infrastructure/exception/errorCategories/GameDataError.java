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
public enum GameDataError implements ErrorCodeInterface {
    // > 2000
    WORLD_DATA_LIST_EMPTY(1000, "Worlds Data List is empty or null.", HttpStatus.INTERNAL_SERVER_ERROR),
    VISUAL_CONTAINER_EMPTY(1001, "Visual Container is empty or null.", HttpStatus.INTERNAL_SERVER_ERROR),
    HERO_CLASS_REGISTRY_EMPTY(1002, "Hero Classes is empty or null.", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatusCode statusCode;
}

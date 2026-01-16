package com.theliems.lokigame.infrastructure.exception.errorCategories;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.theliems.lokigame.infrastructure.exception.ErrorCodeInterface;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public enum HeroError implements ErrorCodeInterface {
    // 5000 - 5999
    HERO_NOT_FOUND(5000, "Hero not found.", HttpStatus.NOT_FOUND),
    HERO_NOT_OWNED(5001, "Player does not own this hero.", HttpStatus.FORBIDDEN),
    INVALID_HERO_CREATION(5002, "Cannot create hero with missing required fields.", HttpStatus.BAD_REQUEST),
    INVALID_HERO_DEFINITION(5003, "Cannot find HeroDefinition of hero_id", HttpStatus.BAD_REQUEST),
    HERO_SELF_SACRIFICE(5004, "Cannot self sacrifice the target hero.", HttpStatus.BAD_REQUEST),
    HERO_SACRIFICE_INVALID(5005, "Sacrifice request invalid.", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;
}

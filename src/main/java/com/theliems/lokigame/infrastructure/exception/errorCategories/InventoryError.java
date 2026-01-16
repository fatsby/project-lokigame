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
public enum InventoryError implements ErrorCodeInterface {
    // 3000 - 3999
    ITEM_NOT_FOUND(3000, "Inventory item not found.", HttpStatus.NOT_FOUND),
    ITEM_NOT_OWNED(3001, "Player does not own this item.", HttpStatus.FORBIDDEN),
    INVALID_ITEM_CREATION(3002, "Cannot create item with missing required fields.", HttpStatus.BAD_REQUEST),
    ITEM_DEFINITION_NOT_FOUND(3003, "Cannot find ItemDefinition of item_id", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;
}

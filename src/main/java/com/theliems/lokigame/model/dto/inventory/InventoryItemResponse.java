package com.theliems.lokigame.model.dto.inventory;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.theliems.lokigame.model.enums.Rarity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Base DTO for inventory items.
 * Uses Jackson polymorphic serialization for subclass-specific responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "itemCategory")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EquipmentItemResponse.class, name = "EQUIPMENT")
})
public class InventoryItemResponse {
    private UUID id;
    private UUID ownerId;
    private Rarity rarity;
    private String displayName;
    private Map<String, Object> metadata;
    private String itemCategory;
}

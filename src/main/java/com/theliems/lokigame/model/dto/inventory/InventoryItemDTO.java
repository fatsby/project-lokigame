package com.theliems.lokigame.model.dto.inventory;

import com.theliems.lokigame.model.enums.ItemTier;
import com.theliems.lokigame.model.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * DTO for representing InventoryItem details to the frontend.
 * Used in HeroResponseDTO to show full equipment details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemDTO {
    private UUID id;
    private String itemId;
    private ItemType type;
    private ItemTier tier;
    private Map<String, Object> metadata;
}

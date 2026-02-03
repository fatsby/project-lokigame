package com.theliems.lokigame.controller.inventory;

import com.theliems.lokigame.mapper.InventoryItemMapper;
import com.theliems.lokigame.model.dto.inventory.InventoryItemResponse;
import com.theliems.lokigame.repository.inventory.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for inventory items.
 * Inventory items are created procedurally (via HeroRollService,
 * DungeonService, etc.)
 * so this controller is primarily for read operations.
 */
@RestController
@RequestMapping("/api/inventory-item")
@RequiredArgsConstructor
public class InventoryItemController {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemMapper inventoryItemMapper;

    @GetMapping
    public ResponseEntity<List<InventoryItemResponse>> getAll() {
        return ResponseEntity.ok(inventoryItemRepository.findAll().stream()
                .map(inventoryItemMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> getById(@PathVariable UUID id) {
        return inventoryItemRepository.findById(id)
                .map(inventoryItemMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (inventoryItemRepository.existsById(id)) {
            inventoryItemRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<InventoryItemResponse>> getByPlayerId(@PathVariable UUID playerId) {
        return ResponseEntity.ok(inventoryItemRepository.findByOwner_PlayerId(playerId).stream()
                .map(inventoryItemMapper::toDto)
                .collect(Collectors.toList()));
    }
}

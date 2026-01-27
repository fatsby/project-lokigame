package com.theliems.lokigame.controller.inventory;

import com.theliems.lokigame.mapper.InventoryItemMapper;
import com.theliems.lokigame.model.dto.inventory.InventoryItemRequest;
import com.theliems.lokigame.model.dto.inventory.InventoryItemResponse;
import com.theliems.lokigame.model.entity.inventory.InventoryItem;
import com.theliems.lokigame.repository.inventory.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @PostMapping
    public ResponseEntity<InventoryItemResponse> create(@RequestBody InventoryItemRequest request) {
        InventoryItem entity = inventoryItemMapper.toEntity(request);
        entity = inventoryItemRepository.save(entity);
        return ResponseEntity.ok(inventoryItemMapper.toDto(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> update(@PathVariable UUID id,
            @RequestBody InventoryItemRequest request) {
        return inventoryItemRepository.findById(id)
                .map(entity -> {
                    inventoryItemMapper.updateEntityFromDto(request, entity);
                    entity = inventoryItemRepository.save(entity);
                    return ResponseEntity.ok(inventoryItemMapper.toDto(entity));
                })
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

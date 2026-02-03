package com.theliems.lokigame.controller.hero;

import com.theliems.lokigame.mapper.WorldMapper;
import com.theliems.lokigame.model.dto.hero.WorldRequest;
import com.theliems.lokigame.model.dto.hero.WorldResponse;
import com.theliems.lokigame.model.entity.hero.World;
import com.theliems.lokigame.repository.hero.WorldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/world")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class WorldController {

    private final WorldRepository worldRepository;
    private final WorldMapper worldMapper;

    @GetMapping
    public ResponseEntity<List<WorldResponse>> getAll() {
        return ResponseEntity.ok(worldRepository.findAll().stream()
                .map(worldMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorldResponse> getById(@PathVariable UUID id) {
        return worldRepository.findById(id)
                .map(worldMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<WorldResponse> create(@RequestBody WorldRequest request) {
        World entity = worldMapper.toEntity(request);
        entity = worldRepository.save(entity);
        return ResponseEntity.ok(worldMapper.toDto(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorldResponse> update(@PathVariable UUID id, @RequestBody WorldRequest request) {
        return worldRepository.findById(id)
                .map(entity -> {
                    worldMapper.updateEntityFromDto(request, entity);
                    entity = worldRepository.save(entity);
                    return ResponseEntity.ok(worldMapper.toDto(entity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (worldRepository.existsById(id)) {
            worldRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

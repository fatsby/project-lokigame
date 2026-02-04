package com.theliems.lokigame.controller.dungeon;

import com.theliems.lokigame.mapper.DungeonMapper;
import com.theliems.lokigame.model.dto.dungeon.DungeonRequest;
import com.theliems.lokigame.model.dto.dungeon.DungeonResponse;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import com.theliems.lokigame.repository.dungeon.DungeonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dungeon")
@RequiredArgsConstructor
public class DungeonController {

        private final DungeonRepository dungeonRepository;
        private final DungeonMapper dungeonMapper;

        @GetMapping
        public ResponseEntity<List<DungeonResponse>> getAll() {
                return ResponseEntity.ok(dungeonRepository.findAll().stream()
                                .map(dungeonMapper::toDto)
                                .collect(Collectors.toList()));
        }

        @GetMapping("/{id}")
        public ResponseEntity<DungeonResponse> getById(@PathVariable UUID id) {
                return dungeonRepository.findById(id)
                                .map(dungeonMapper::toDto)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<DungeonResponse> create(@RequestBody DungeonRequest request) {
                Dungeon entity = dungeonMapper.toEntity(request);
                entity = dungeonRepository.save(entity);
                return ResponseEntity.ok(dungeonMapper.toDto(entity));
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<DungeonResponse> update(@PathVariable UUID id, @RequestBody DungeonRequest request) {
                return dungeonRepository.findById(id)
                                .map(entity -> {
                                        dungeonMapper.updateEntityFromDto(request, entity);
                                        entity = dungeonRepository.save(entity);
                                        return ResponseEntity.ok(dungeonMapper.toDto(entity));
                                })
                                .orElse(ResponseEntity.notFound().build());
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> delete(@PathVariable UUID id) {
                if (dungeonRepository.existsById(id)) {
                        dungeonRepository.deleteById(id);
                        return ResponseEntity.noContent().build();
                }
                return ResponseEntity.notFound().build();
        }

}

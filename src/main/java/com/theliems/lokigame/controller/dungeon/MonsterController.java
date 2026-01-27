package com.theliems.lokigame.controller.dungeon;

import com.theliems.lokigame.mapper.MonsterMapper;
import com.theliems.lokigame.model.dto.dungeon.MonsterRequest;
import com.theliems.lokigame.model.dto.dungeon.MonsterResponse;
import com.theliems.lokigame.model.entity.dungeon.Monster;
import com.theliems.lokigame.repository.dungeon.MonsterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/monster")
@RequiredArgsConstructor
public class MonsterController {

    private final MonsterRepository monsterRepository;
    private final MonsterMapper monsterMapper;

    @GetMapping
    public ResponseEntity<List<MonsterResponse>> getAll() {
        return ResponseEntity.ok(monsterRepository.findAll().stream()
                .map(monsterMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonsterResponse> getById(@PathVariable UUID id) {
        return monsterRepository.findById(id)
                .map(monsterMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MonsterResponse> create(@RequestBody MonsterRequest request) {
        Monster entity = monsterMapper.toEntity(request);
        entity = monsterRepository.save(entity);
        return ResponseEntity.ok(monsterMapper.toDto(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MonsterResponse> update(@PathVariable UUID id, @RequestBody MonsterRequest request) {
        return monsterRepository.findById(id)
                .map(entity -> {
                    monsterMapper.updateEntityFromDto(request, entity);
                    entity = monsterRepository.save(entity);
                    return ResponseEntity.ok(monsterMapper.toDto(entity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (monsterRepository.existsById(id)) {
            monsterRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

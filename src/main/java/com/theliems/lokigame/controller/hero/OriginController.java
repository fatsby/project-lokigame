package com.theliems.lokigame.controller.hero;

import com.theliems.lokigame.mapper.OriginMapper;
import com.theliems.lokigame.model.dto.hero.OriginRequest;
import com.theliems.lokigame.model.dto.hero.OriginResponse;
import com.theliems.lokigame.model.entity.hero.Origin;
import com.theliems.lokigame.repository.hero.OriginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/origin")
@RequiredArgsConstructor
public class OriginController {

    private final OriginRepository originRepository;
    private final OriginMapper originMapper;

    @GetMapping
    public ResponseEntity<List<OriginResponse>> getAll() {
        return ResponseEntity.ok(originRepository.findAll().stream()
                .map(originMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OriginResponse> getById(@PathVariable UUID id) {
        return originRepository.findById(id)
                .map(originMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OriginResponse> create(@RequestBody OriginRequest request) {
        Origin entity = originMapper.toEntity(request);
        entity = originRepository.save(entity);
        return ResponseEntity.ok(originMapper.toDto(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OriginResponse> update(@PathVariable UUID id, @RequestBody OriginRequest request) {
        return originRepository.findById(id)
                .map(entity -> {
                    originMapper.updateEntityFromDto(request, entity);
                    entity = originRepository.save(entity);
                    return ResponseEntity.ok(originMapper.toDto(entity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (originRepository.existsById(id)) {
            originRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

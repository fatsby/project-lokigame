package com.theliems.lokigame.controller.hero;

import com.theliems.lokigame.mapper.HeroClassMapper;
import com.theliems.lokigame.model.dto.hero.HeroClassRequest;
import com.theliems.lokigame.model.dto.hero.HeroClassResponse;
import com.theliems.lokigame.model.entity.hero.HeroClass;
import com.theliems.lokigame.repository.hero.HeroClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hero-class")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class HeroClassController {

    private final HeroClassRepository heroClassRepository;
    private final HeroClassMapper heroClassMapper;

    @GetMapping
    public ResponseEntity<List<HeroClassResponse>> getAll() {
        return ResponseEntity.ok(heroClassRepository.findAll().stream()
                .map(heroClassMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HeroClassResponse> getById(@PathVariable UUID id) {
        return heroClassRepository.findById(id)
                .map(heroClassMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<HeroClassResponse> create(@RequestBody HeroClassRequest request) {
        HeroClass entity = heroClassMapper.toEntity(request);
        entity = heroClassRepository.save(entity);
        return ResponseEntity.ok(heroClassMapper.toDto(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HeroClassResponse> update(@PathVariable UUID id, @RequestBody HeroClassRequest request) {
        return heroClassRepository.findById(id)
                .map(entity -> {
                    heroClassMapper.updateEntityFromDto(request, entity);
                    entity = heroClassRepository.save(entity);
                    return ResponseEntity.ok(heroClassMapper.toDto(entity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (heroClassRepository.existsById(id)) {
            heroClassRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

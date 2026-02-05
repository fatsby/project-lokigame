package com.theliems.lokigame.controller.dungeon;

import com.theliems.lokigame.model.dto.dungeon.MonsterTemplateResponse;
import com.theliems.lokigame.model.entity.dungeon.MonsterTemplate;
import com.theliems.lokigame.repository.dungeon.MonsterTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for managing MonsterTemplates.
 * Admin-only access for viewing monster templates used in procedural
 * generation.
 */
@RestController
@RequestMapping("/api/monster-template")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MonsterTemplateController {

    private final MonsterTemplateRepository monsterTemplateRepository;

    /**
     * Get all monster templates.
     */
    @GetMapping
    public ResponseEntity<List<MonsterTemplateResponse>> getAll() {
        List<MonsterTemplateResponse> templates = monsterTemplateRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(templates);
    }

    /**
     * Get a monster template by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MonsterTemplateResponse> getById(@PathVariable UUID id) {
        return monsterTemplateRepository.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private MonsterTemplateResponse toDto(MonsterTemplate template) {
        return MonsterTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .baseStats(template.getBaseStats())
                .statGrowthPerLevel(template.getStatGrowthPerLevel())
                .build();
    }
}

package com.theliems.lokigame.controller.player;

import com.theliems.lokigame.mapper.PlayerMapper;
import com.theliems.lokigame.model.dto.player.PlayerRequest;
import com.theliems.lokigame.model.dto.player.PlayerResponse;

import com.theliems.lokigame.repository.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAll() {
        return ResponseEntity.ok(playerRepository.findAll().stream()
                .map(playerMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getById(@PathVariable UUID id) {
        return playerRepository.findById(id)
                .map(playerMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerResponse> update(@PathVariable UUID id, @RequestBody PlayerRequest request) {
        return playerRepository.findById(id)
                .map(entity -> {
                    playerMapper.updateEntityFromDto(request, entity);
                    entity = playerRepository.save(entity);
                    return ResponseEntity.ok(playerMapper.toDto(entity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

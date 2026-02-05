package com.theliems.lokigame.controller.dungeon;

import com.theliems.lokigame.mapper.DungeonMapper;
import com.theliems.lokigame.model.dto.dungeon.DungeonResponse;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import com.theliems.lokigame.service.dungeon.DungeonService;
import com.theliems.lokigame.service.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for dungeon preview/generation.
 * Dungeons are now procedurally generated, not stored as static entities.
 */
@RestController
@RequestMapping("/api/dungeon")
@RequiredArgsConstructor
public class DungeonController {

        private final DungeonService dungeonService;
        private final DungeonMapper dungeonMapper;
        private final PlayerService playerService;

        /**
         * Preview a procedurally generated dungeon without starting a run.
         * 
         * @param worldId The world ID
         * @param level   The dungeon level
         * @return Generated dungeon preview
         */
        @GetMapping("/preview")
        public ResponseEntity<DungeonResponse> previewDungeon(
                        @RequestParam UUID worldId,
                        @RequestParam Integer level) {
                UUID playerId = playerService.getCurrentPlayer().getPlayerId();
                Dungeon dungeon = dungeonService.getOrGenerateDungeon(playerId, level, worldId);
                return ResponseEntity.ok(dungeonMapper.toDto(dungeon));
        }
}

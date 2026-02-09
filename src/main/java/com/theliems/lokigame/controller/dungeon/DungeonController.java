package com.theliems.lokigame.controller.dungeon;

import com.theliems.lokigame.mapper.DungeonMapper;
import com.theliems.lokigame.mapper.DungeonSeedMapper;
import com.theliems.lokigame.model.dto.dungeon.DungeonResponse;
import com.theliems.lokigame.model.dto.dungeon.DungeonSeedResponse;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import com.theliems.lokigame.model.entity.dungeon.DungeonSeed;
import com.theliems.lokigame.model.entity.hero.World;
import com.theliems.lokigame.service.dungeon.DungeonSeedService;
import com.theliems.lokigame.service.dungeon.DungeonService;
import com.theliems.lokigame.service.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
        private final DungeonSeedService dungeonSeedService;
        private final DungeonMapper dungeonMapper;
        private final DungeonSeedMapper dungeonSeedMapper;
        private final PlayerService playerService;

        /**
         * Preview a procedurally generated dungeon without starting a run.
         * 
         * @param level The dungeon level
         * @return Generated dungeon preview
         */
        @GetMapping("/preview")
        public ResponseEntity<DungeonResponse> previewDungeon(
                        @RequestParam Integer level) {
                UUID playerId = playerService.getCurrentPlayer().getPlayerId();
                Dungeon dungeon = dungeonService.getOrGenerateDungeon(playerId, level);
                return ResponseEntity.ok(dungeonMapper.toDto(dungeon));
        }

        /**
         * Get all dungeon seeds for the currently authenticated player.
         * 
         * @return List of all dungeon seeds with embedded world info
         */
        @GetMapping("/seeds")
        public ResponseEntity<List<DungeonSeedResponse>> getAllPlayerSeeds() {
                UUID playerId = playerService.getCurrentPlayer().getPlayerId();
                Map<DungeonSeed, World> seedsWithWorlds = dungeonSeedService.getAllSeedsWithWorlds(playerId);

                List<DungeonSeedResponse> response = seedsWithWorlds.entrySet().stream()
                                .map(entry -> dungeonSeedMapper.toDto(entry.getKey(), entry.getValue()))
                                .toList();

                return ResponseEntity.ok(response);
        }
}

package com.theliems.lokigame.controller.battle;

import com.theliems.lokigame.model.dto.battle.BattleSimulateRequest;
import com.theliems.lokigame.model.dto.battle.BattleSimulateResponse;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import com.theliems.lokigame.service.battle.BattleService;
import com.theliems.lokigame.service.dungeon.DungeonService;
import com.theliems.lokigame.service.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for battle simulation.
 * Uses procedurally generated dungeons.
 */
@RestController
@RequestMapping("/api/battle")
@RequiredArgsConstructor
public class BattleController {

        private final BattleService battleService;
        private final DungeonService dungeonService;
        private final PlayerService playerService;

        /**
         * Simulate a battle against a procedurally generated dungeon.
         * Admin-only endpoint for testing battle simulation.
         */
        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping("/simulate")
        public ResponseEntity<BattleSimulateResponse> simulateBattle(@RequestBody BattleSimulateRequest request) {
                UUID playerId = playerService.getCurrentPlayer().getPlayerId();

                // Generate dungeon for simulation
                // Generate dungeon for simulation
                Dungeon dungeon = dungeonService.getOrGenerateDungeon(
                                playerId,
                                request.getDungeonLevel());

                BattleSimulateResponse response = battleService.simulateBattle(
                                request.getHeroIds(),
                                dungeon);
                return ResponseEntity.ok(response);
        }
}

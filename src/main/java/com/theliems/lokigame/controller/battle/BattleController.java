package com.theliems.lokigame.controller.battle;

import com.theliems.lokigame.model.dto.battle.BattleSimulateRequest;
import com.theliems.lokigame.model.dto.battle.BattleSimulateResponse;
import com.theliems.lokigame.service.battle.BattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battle")
@RequiredArgsConstructor
public class BattleController {

        private final BattleService battleService;

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping("/simulate")
        public ResponseEntity<BattleSimulateResponse> simulateBattle(@RequestBody BattleSimulateRequest request) {
                BattleSimulateResponse response = battleService.simulateBattle(
                                request.getHeroIds(),
                                request.getDungeonId());
                return ResponseEntity.ok(response);
        }
}

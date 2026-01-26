package com.theliems.lokigame.controller.battle;

import com.theliems.lokigame.engine.BattleEngine;
import com.theliems.lokigame.model.dto.battle.BattleSimulateRequest;
import com.theliems.lokigame.model.dto.battle.BattleSimulateResponse;
import com.theliems.lokigame.service.battle.BattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/battle")
@RequiredArgsConstructor
public class BattleController {

    private final BattleService battleService;

    @PostMapping("/simulate")
    public ResponseEntity<BattleSimulateResponse> simulateBattle(@RequestBody BattleSimulateRequest request) {
        BattleEngine.BattleResult result = battleService.simulateBattle(
                request.getHeroIds(),
                request.getDungeonId()
        );

        List<BattleSimulateResponse.BattleLogEntry> logs = result.getLogs().stream()
                .map(log -> BattleSimulateResponse.BattleLogEntry.builder()
                        .turn(log.getTurn())
                        .message(log.getMessage())
                        .build())
                .toList();

        BattleSimulateResponse response = BattleSimulateResponse.builder()
                .winner(result.getWinner())
                .turns(result.getTurns())
                .logs(logs)
                .build();

        return ResponseEntity.ok(response);
    }
}

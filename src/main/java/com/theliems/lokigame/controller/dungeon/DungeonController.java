package com.theliems.lokigame.controller.dungeon;

import com.theliems.lokigame.model.dto.dungeon.DungeonRunRequest;
import com.theliems.lokigame.model.dto.dungeon.DungeonRunResponse;
import com.theliems.lokigame.service.dungeon.DungeonService;
import com.theliems.lokigame.service.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dungeon")
@RequiredArgsConstructor
public class DungeonController {

    private final DungeonService dungeonService;
    private final PlayerService playerService;

    @PostMapping("/run")
    public ResponseEntity<DungeonRunResponse> runDungeon(@RequestBody DungeonRunRequest request) {
        UUID playerId = playerService.getCurrentPlayer().getPlayerId();
        DungeonService.DungeonRunResult result = dungeonService.runDungeon(
                playerId,
                request.getDungeonId()
        );

        List<DungeonRunResponse.RewardResponse> rewards = result.getRewards().stream()
                .map(reward -> DungeonRunResponse.RewardResponse.builder()
                        .type(reward.getType())
                        .amount(reward.getAmount())
                        .build())
                .collect(Collectors.toList());

        DungeonRunResponse response = DungeonRunResponse.builder()
                .dungeonId(result.getDungeonId())
                .rewards(rewards)
                .build();

        return ResponseEntity.ok(response);
    }
}

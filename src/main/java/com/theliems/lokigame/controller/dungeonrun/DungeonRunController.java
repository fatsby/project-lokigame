package com.theliems.lokigame.controller.dungeonrun;

import com.theliems.lokigame.model.dto.dungeon.DungeonRunRequest;
import com.theliems.lokigame.model.dto.dungeon.DungeonRunResponse;
import com.theliems.lokigame.service.dungeon.DungeonRunFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for executing dungeon runs.
 * Uses procedural dungeon generation based on level and world.
 */
@RestController
@RequestMapping("/api/dungeon-run")
@RequiredArgsConstructor
public class DungeonRunController {

    private final DungeonRunFacade dungeonRunFacade;

    /**
     * Execute a dungeon run with procedurally generated dungeon.
     * 
     * @param request Contains heroIds, dungeonLevel, and worldId
     * @return DungeonRunResponse with battle results and rewards
     */
    @PostMapping
    public ResponseEntity<DungeonRunResponse> executeDungeonRun(@Valid @RequestBody DungeonRunRequest request) {
        DungeonRunResponse response = dungeonRunFacade.executeDungeonRun(
                request.getHeroIds(),
                request.getDungeonLevel());
        return ResponseEntity.ok(response);
    }
}

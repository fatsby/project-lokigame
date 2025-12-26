package com.theliems.lokigame.controller.player;

import com.theliems.lokigame.model.dto.api.ApiResponse;
import com.theliems.lokigame.model.dto.player.PlayerResponseDTO;
import com.theliems.lokigame.service.player.PlayerServiceInterface;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PlayerController {
    PlayerServiceInterface playerService;
    private final RestClient.Builder builder;

    @GetMapping
    public ResponseEntity<ApiResponse<PlayerResponseDTO>> getMe() {
        return ResponseEntity.ok(ApiResponse.<PlayerResponseDTO>builder()
                .message("Successfully retrieved player's data")
                .result(playerService.getMe())
                .build()
        );
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> getAdmin() {
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Success")
                .result("Hello Admin")
                .build()
        );
    }
}

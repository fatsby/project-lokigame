package com.theliems.lokigame.model.dto.player;

import com.theliems.lokigame.model.entity.player.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponse {
    private UUID playerId;
    private String email;
    private String username;
    private Role role;
    private Long currency;
    private Long gold;
}

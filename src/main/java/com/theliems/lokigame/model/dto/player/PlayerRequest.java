package com.theliems.lokigame.model.dto.player;

import com.theliems.lokigame.model.entity.player.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRequest {
    private String email;
    private String username;
    private String password; // Optional, only for updates/creation
    private Role role;
}

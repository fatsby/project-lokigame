package com.theliems.lokigame.model.dto.player;

import com.theliems.lokigame.model.entity.player.Role;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PlayerResponseDTO {
    UUID id;
    String email;
    String username;
    Role role;
    Long currency;
    private boolean active;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}

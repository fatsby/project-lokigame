package com.theliems.lokigame.model.dto.auth;

import com.theliems.lokigame.model.dto.leveling.OfflineProgressionResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;

    /**
     * Offline progression result (XP gained while offline).
     * Only populated on login, null on register.
     */
    private OfflineProgressionResult offlineProgression;
}

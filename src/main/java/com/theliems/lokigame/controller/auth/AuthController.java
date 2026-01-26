package com.theliems.lokigame.controller.auth;

import com.theliems.lokigame.model.dto.auth.AuthResponse;
import com.theliems.lokigame.model.dto.auth.LoginRequest;
import com.theliems.lokigame.model.dto.auth.RegisterRequest;
import com.theliems.lokigame.model.dto.auth.TokenPair;
import com.theliems.lokigame.model.dto.auth.TokenRefreshRequest;
import com.theliems.lokigame.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenPair tokens = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(tokens);
    }
}

package com.theliems.lokigame.controller.authentication;

import com.theliems.lokigame.model.dto.auth.LoginRequest;
import com.theliems.lokigame.model.dto.auth.RegisterRequest;
import com.theliems.lokigame.model.dto.auth.TokenPair;
import com.theliems.lokigame.model.dto.auth.TokenRefreshRequest;
import com.theliems.lokigame.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        TokenPair tokenPair = authService.login(loginRequest);
        return ResponseEntity.ok(tokenPair);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenPair tokenPair = authService.refreshToken(request);
        return ResponseEntity.ok(tokenPair);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {

    }
}

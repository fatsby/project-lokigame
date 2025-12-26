package com.theliems.lokigame.controller;

import com.theliems.lokigame.infrastructure.security.JwtService;
import com.theliems.lokigame.infrastructure.security.UserDetailsImpl;
import com.theliems.lokigame.model.dto.auth.AuthResponse;
import com.theliems.lokigame.model.dto.auth.LoginRequest;
import com.theliems.lokigame.model.dto.auth.RegisterRequest;
import com.theliems.lokigame.model.dto.auth.TokenRefreshRequest;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.entity.player.Role;
import com.theliems.lokigame.model.entity.system.RefreshToken;
import com.theliems.lokigame.repository.PlayerRepository;
import com.theliems.lokigame.service.auth.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final PlayerRepository playerRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateTokenFromUsername(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new AuthResponse(jwt,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        if (playerRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        if (playerRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Create new user's account
        Player player = new Player();
        player.setUsername(signUpRequest.getUsername());
        player.setEmail(signUpRequest.getEmail());
        player.setPasswordHash(encoder.encode(signUpRequest.getPassword()));
        
        Set<String> strRoles = signUpRequest.getRole();
        Role role = Role.USER;

        if (strRoles != null && strRoles.contains("admin")) {
            role = Role.ADMIN;
        } else if (strRoles != null && strRoles.contains("mod")) {
            role = Role.MODERATOR;
        }

        player.setRole(role);
        playerRepository.save(player);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getPlayer)
                .map(player -> {
                    String token = jwtService.generateTokenFromUsername(player.getUsername());
                    return ResponseEntity.ok(new AuthResponse(token, requestRefreshToken, player.getId(), player.getUsername(), player.getEmail(), List.of(player.getRole().name())));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        refreshTokenService.deleteByUserId(userDetails.getId());
        return ResponseEntity.ok("Log out successful!");
    }
}

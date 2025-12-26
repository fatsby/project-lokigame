package com.theliems.lokigame.service.auth;

import com.theliems.lokigame.model.dto.auth.LoginRequest;
import com.theliems.lokigame.model.dto.auth.RegisterRequest;
import com.theliems.lokigame.model.dto.auth.TokenPair;
import com.theliems.lokigame.model.dto.auth.TokenRefreshRequest;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.entity.player.Role;
import com.theliems.lokigame.repository.player.PlayerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    PlayerRepository playerRepository;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtService jwtService;
    UserDetailsService userDetailsService;

    @Transactional
    public void registerUser(RegisterRequest registerRequest){
        // Mail & Username validation
        if (playerRepository.existsByUsername(registerRequest.getUsername()) || playerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Username or Email already exists");
        }

        // Build user
        Player player = Player.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.ROLE_USER)
                .currency(0L)
                .build();

        // Save user
        playerRepository.save(player);
    }

    public TokenPair login(LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate Token Pair
        return jwtService.generateTokenPair(authentication);
    }

    public TokenPair refreshToken(TokenRefreshRequest request) {

        String refreshToken = request.getRefreshToken();
        // check if it is valid refresh token
        if(!jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String user = jwtService.extractUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user);

        if (userDetails == null) {
            throw new IllegalArgumentException("User not found");
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        String accessToken = jwtService.generateAccessToken(authentication);
        return new TokenPair(accessToken, refreshToken);
    }
}

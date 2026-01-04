package com.theliems.lokigame.controller.hero;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.infrastructure.exception.errorCategories.PlayerError;
import com.theliems.lokigame.mapper.hero.HeroMapper;
import com.theliems.lokigame.model.dto.api.ApiResponse;
import com.theliems.lokigame.model.dto.hero.HeroResponseDTO;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.repository.player.PlayerRepository;
import com.theliems.lokigame.service.hero.HeroService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hero")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HeroController {

    HeroService heroService;
    HeroMapper heroMapper;
    PlayerRepository playerRepository;
    ExceptionFactory exceptionFactory;

    /**
     * Summon a new hero for the authenticated player.
     * Requires JWT authentication.
     */
    @PostMapping("/summon")
    public ResponseEntity<ApiResponse<HeroResponseDTO>> summonHero() {
        // Get authenticated player
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> exceptionFactory.createNotFoundException(
                        "Player", "username", username, PlayerError.PLAYER_NOT_FOUND));

        // Summon hero
        Hero hero = heroService.summonHero(player.getId());
        HeroResponseDTO dto = heroMapper.toDTO(hero);

        return ResponseEntity.ok(ApiResponse.<HeroResponseDTO>builder()
                .message("Hero summoned successfully")
                .result(dto)
                .build());
    }
}

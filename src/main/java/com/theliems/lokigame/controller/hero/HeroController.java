package com.theliems.lokigame.controller.hero;

import com.theliems.lokigame.mapper.hero.HeroMapper;
import com.theliems.lokigame.model.dto.api.ApiResponse;
import com.theliems.lokigame.model.dto.hero.HeroResponseDTO;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.service.hero.HeroService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hero")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HeroController {

    HeroService heroService;

    /**
     * Summon a new hero for the authenticated player.
     * Requires JWT authentication.
     */
    @PostMapping("/summon")
    public ResponseEntity<ApiResponse<HeroResponseDTO>> summonHero() {
        HeroResponseDTO hero = heroService.summonHeroForCurrentPlayer();

        return ResponseEntity.ok(ApiResponse.<HeroResponseDTO>builder()
                .message("Hero summoned successfully")
                .result(hero)
                .build());
    }

    /**
     * Get all heroes owned by the authenticated player.
     * Requires JWT authentication.
     */
    @GetMapping("/my-heroes")
    public ResponseEntity<ApiResponse<List<HeroResponseDTO>>> getMyHeroes() {
        List<HeroResponseDTO> heroes = heroService.getHeroesForCurrentPlayer();

        return ResponseEntity.ok(ApiResponse.<List<HeroResponseDTO>>builder()
                .message("Heroes retrieved successfully")
                .result(heroes)
                .build());
    }
}

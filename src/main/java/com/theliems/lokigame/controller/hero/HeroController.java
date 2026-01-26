package com.theliems.lokigame.controller.hero;

import com.theliems.lokigame.model.dto.hero.HeroResponse;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.model.entity.hero.HeroStats;
import com.theliems.lokigame.service.hero.HeroRollService;
import com.theliems.lokigame.service.hero.HeroService;
import com.theliems.lokigame.service.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hero")
@RequiredArgsConstructor
public class HeroController {

    private final HeroRollService heroRollService;
    private final HeroService heroService;
    private final PlayerService playerService;

    @PostMapping("/roll")
    public ResponseEntity<HeroResponse> rollHero() {
        // Get current authenticated player
        var player = playerService.getCurrentPlayer();
        
        Hero hero = heroRollService.rollHero(player);
        return ResponseEntity.ok(mapToResponse(hero));
    }

    @GetMapping("/my-heroes")
    public ResponseEntity<List<HeroResponse>> getMyHeroes() {
        UUID playerId = playerService.getCurrentPlayer().getPlayerId();
        List<Hero> heroes = heroService.getPlayerHeroes(playerId);
        List<HeroResponse> responses = heroes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private HeroResponse mapToResponse(Hero hero) {
        List<HeroResponse.HeroStatResponse> stats = hero.getStats().stream()
                .map(stat -> HeroResponse.HeroStatResponse.builder()
                        .statType(stat.getStatType().name())
                        .baseValue(stat.getBaseValue())
                        .finalValue(stat.getFinalValue())
                        .build())
                .collect(Collectors.toList());

        return HeroResponse.builder()
                .heroId(hero.getHeroId())
                .firstName(hero.getFirstName())
                .lastName(hero.getLastName())
                .heroClassName(hero.getHeroClass() != null ? hero.getHeroClass().getName() : null)
                .originName(hero.getOrigin() != null ? hero.getOrigin().getName() : null)
                .worldName(hero.getOriginWorld() != null ? hero.getOriginWorld().getName() : null)
                .level(hero.getLevel())
                .star(hero.getStar())
                .stats(stats)
                .equipment(hero.getEquipment() != null ?
                        hero.getEquipment().entrySet().stream()
                                .collect(Collectors.toMap(
                                        e -> e.getKey().name(),
                                        e -> e.getValue()
                                )) : null)
                .build();
    }
}

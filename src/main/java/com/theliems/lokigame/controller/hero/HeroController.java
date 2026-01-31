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
        private final com.theliems.lokigame.mapper.HeroMapper heroMapper;

        @PostMapping("/roll")
        public ResponseEntity<HeroResponse> rollHero() {
                // Get current authenticated player
                var player = playerService.getCurrentPlayer();

                Hero hero = heroRollService.rollHero(player);
                return ResponseEntity.ok(heroMapper.toDto(hero));
        }

        @GetMapping("/my-heroes")
        public ResponseEntity<List<HeroResponse>> getMyHeroes() {
                UUID playerId = playerService.getCurrentPlayer().getPlayerId();
                List<Hero> heroes = heroService.getPlayerHeroes(playerId);
                List<HeroResponse> responses = heroes.stream()
                                .map(heroMapper::toDto)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(responses);
        }

        @GetMapping("/{id}")
        public ResponseEntity<HeroResponse> getById(@PathVariable UUID id) {
                Hero hero = heroService.getHeroById(id);
                return ResponseEntity.ok(heroMapper.toDto(hero));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable UUID id) {
                heroService.deleteHero(id);
                return ResponseEntity.noContent().build();
        }
}

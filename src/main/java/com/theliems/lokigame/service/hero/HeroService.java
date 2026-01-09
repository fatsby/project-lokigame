package com.theliems.lokigame.service.hero;

import com.theliems.lokigame.infrastructure.security.SecurityContextService;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.repository.hero.HeroRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Facade service for hero-related operations.
 * Handles authentication context and delegates to specialized services.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HeroService {

    HeroGenerationService heroGenerationService;
    HeroRepository heroRepository;
    SecurityContextService securityContextService;

    /**
     * Summon a new hero for the currently authenticated player.
     *
     * @return the newly summoned Hero
     */
    public Hero summonHeroForCurrentPlayer() {
        Player player = securityContextService.getCurrentPlayer();
        return heroGenerationService.summonHero(player.getId());
    }

    /**
     * Get all heroes owned by the currently authenticated player.
     *
     * @return list of heroes owned by the current player
     */
    public List<Hero> getHeroesForCurrentPlayer() {
        Player player = securityContextService.getCurrentPlayer();
        return heroRepository.findByOwnerId(player.getId());
    }
}

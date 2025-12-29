package com.theliems.lokigame.service.hero;

import com.theliems.lokigame.infrastructure.rng.WeightedSelector;
import com.theliems.lokigame.model.entity.hero.ClassDefinition;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.model.entity.hero.StatRange;
import com.theliems.lokigame.model.entity.world.WorldDefinition;
import com.theliems.lokigame.repository.hero.HeroRepository;
import com.theliems.lokigame.service.gameData.registry.HeroClassRegistry;
import com.theliems.lokigame.service.gameData.registry.VisualsRegistry;
import com.theliems.lokigame.service.gameData.registry.WorldRegistry;
import com.theliems.lokigame.service.rng.WeightedRngService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HeroServiceTest {

    @Mock
    private HeroRepository heroRepository;
    @Mock
    private HeroClassRegistry heroClassRegistry;
    @Mock
    private WorldRegistry worldRegistry;
    @Mock
    private VisualsRegistry visualsRegistry;
    @Mock
    private WeightedRngService rngService;

    @InjectMocks
    private HeroService heroService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void summonHero_ShouldGenerateAndPersistHero() {
        // Mock Data
        UUID ownerId = UUID.randomUUID();
        
        ClassDefinition mockClass = new ClassDefinition();
        mockClass.setId("mage");
        StatRange healthRange = new StatRange();
        healthRange.setMin(50);
        healthRange.setMax(60);
        mockClass.setBaseStats(Map.of("health", healthRange));
        
        WorldDefinition mockWorld = new WorldDefinition();
        mockWorld.setId("midgard");
        mockWorld.setStatMultiplier(1.0);
        
        // Mock Behaviors
        when(heroClassRegistry.getRandomClass()).thenReturn(mockClass);
        when(worldRegistry.rollRandomWorld()).thenReturn(mockWorld);
        when(visualsRegistry.getRandomHair()).thenReturn("hair_01");
        when(visualsRegistry.getRandomFace()).thenReturn("face_01");
        
        // Mock RNG Service to return a working selector (using real implementation or simple mock)
        // Since we can't easily mock the internal "next()" of the real WeightedSelector without complexity,
        // we'll use a real WeightedSelector but we can't fully control the output unless we mock the selector itself.
        // For this test, we accept random rarity.
        when(rngService.createSelector()).thenAnswer(inv -> new WeightedSelector<Integer>());

        when(heroRepository.save(any(Hero.class))).thenAnswer(invocation -> {
            Hero h = invocation.getArgument(0);
            h.setId(UUID.randomUUID());
            return h;
        });

        // Execute
        Hero hero = heroService.summonHero(ownerId);

        // Verify
        assertNotNull(hero);
        assertNotNull(hero.getId());
        assertEquals(ownerId, hero.getOwnerId());
        assertEquals("mage", hero.getHeroClass());
        assertEquals("midgard", hero.getOriginWorldId());
        assertNotNull(hero.getGender());
        assertTrue(hero.getRarity() >= 1 && hero.getRarity() <= 7);
        assertTrue(hero.getStats().containsKey("health"));
        assertTrue(hero.getVisuals().containsKey("hair_id"));
        assertEquals("hair_01", hero.getVisuals().get("hair_id"));

        verify(heroRepository, times(1)).save(any(Hero.class));
    }
}

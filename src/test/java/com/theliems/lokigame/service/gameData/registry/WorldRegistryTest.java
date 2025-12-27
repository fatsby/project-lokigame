package com.theliems.lokigame.service.gameData.registry;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.infrastructure.rng.WeightedSelector;
import com.theliems.lokigame.model.entity.world.WorldDefinition;
import com.theliems.lokigame.service.rng.WeightedRngService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WorldRegistryTest {

    private WorldRegistry registry;

    @BeforeEach
    void setUp() {
        // We manually instantiate the class because we are testing logic, not Dependency Injection.
        ExceptionFactory exceptionFactory = mock(ExceptionFactory.class);
        WeightedRngService rngService = mock(WeightedRngService.class);

        // Return a REAL selector so we test the actual logic (Integration Test)
        when(rngService.createSelector()).thenReturn(new WeightedSelector<>());

        registry = new WorldRegistry(exceptionFactory, rngService);
        registry.init();
    }

    @Test
    @DisplayName("Monte Carlo Simulation: Verify Super Rare Drop Rate")
    void verifyRarityDistribution() {
        // 1. Setup Scenarios
        // Midgard: 1,000,000 (Common)
        // Asgard: 1 (Super Rare)
        registry.add(createWorld("Midgard", 1_000_000));
        registry.add(createWorld("Asgard", 1));

        long asgardCount = 0;
        int simulations = 10_000_000;

        System.out.println("Starting " + simulations + " simulations...");

        // 2. Run the Loop
        for (int i = 0; i < simulations; i++) {
            WorldDefinition result = registry.rollRandomWorld();
            if ("Asgard".equals(result.getId())) {
                asgardCount++;
            }
        }

        // 3. Output Results
        System.out.println("Simulations: " + simulations);
        System.out.println("Asgard Hits: " + asgardCount);
        double percentage = (double) asgardCount / simulations * 100;
        System.out.printf("Drop Rate: %.6f%%\n", percentage);

        // 4. Assertions
        // In 10 million runs, Asgard should appear ~10 times.
        // We set a safe upper bound (e.g., 100) to account for RNG variance.
        assertTrue(asgardCount < 100, "Asgard is appearing too often! RNG Logic might be broken.");

        // It is statistically possible (though unlikely) to get 0 hits,
        // but for a unit test, >0 confirms the code path is actually reachable.
        assertTrue(asgardCount > 0 || simulations < 1_000_001,
                "Asgard never appeared! (Note: Run again if hits=0, it might just be bad luck)");
    }

    // Helper method to create dummy data
    private WorldDefinition createWorld(String id, int weight) {
        WorldDefinition def = new WorldDefinition();
        def.setId(id);
        def.setRarityWeight(weight);
        def.setName(id); // Optional
        def.setStatMultiplier(1.0); // Optional
        return def;
    }
}
package com.theliems.lokigame.utils;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.hero.World;
import com.theliems.lokigame.repository.hero.WorldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class WorldUtils {
    private final WorldRepository worldRepository;
    private final ExceptionFactory exceptionFactory;
    public World rollWorld(Random random) {
        List<World> worlds = worldRepository.findAll();

        if (worlds.isEmpty()) {
            throw exceptionFactory.internalError("Worlds not initialized");
        }

        double totalWeight = worlds.stream()
                .mapToDouble(World::getRarityWeight)
                .sum();

        double roll = random.nextDouble() * totalWeight;

        double current = 0.0;
        for (World world : worlds) {
            current += world.getRarityWeight();
            if (roll <= current) {
                return world;
            }
        }

        // fallback (should not happen)
        return worlds.get(0);
    }
}

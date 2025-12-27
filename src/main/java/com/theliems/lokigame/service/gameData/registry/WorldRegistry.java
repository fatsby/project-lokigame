package com.theliems.lokigame.service.gameData.registry;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.infrastructure.exception.errorCategories.GameDataError;
import com.theliems.lokigame.infrastructure.rng.WeightedSelector;
import com.theliems.lokigame.model.entity.world.WorldDefinition;
import com.theliems.lokigame.service.rng.WeightedRngService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WorldRegistry implements DataRegistry {
    private final ExceptionFactory exceptionFactory;
    private final WeightedRngService rngService;
    
    // Fast lookup by ID (for UI/Tooltips)
    private final Map<String, WorldDefinition> worldMap = new ConcurrentHashMap<>();

    private WeightedSelector<WorldDefinition> selector;

    @PostConstruct
    public void init() {
        this.selector = rngService.createSelector();
    }

    public void add(WorldDefinition newWorld) {
        // 1. Store for ID lookup
        worldMap.put(newWorld.getId(), newWorld);

        // 2. Add to Weighted Selector
        // Ensure selector is initialized (Unit tests might skip PostConstruct if manual new())
        if (selector == null) selector = rngService.createSelector();
        
        selector.add(newWorld.getRarityWeight(), newWorld);
    }

    public WorldDefinition get(String id) {
        return worldMap.get(id);
    }

    /**
     * Selects a random world using the Cumulative Weight algorithm.
     */
    public WorldDefinition rollRandomWorld() {
        if (selector == null || selector.isEmpty())
            throw exceptionFactory.createValidationException("WorldRegistry", "selector", "empty", GameDataError.WORLD_DATA_LIST_EMPTY);

        return selector.next();
    }

    @Override
    public void clear() {
        worldMap.clear();
        if (selector != null) selector.clear();
    }
}
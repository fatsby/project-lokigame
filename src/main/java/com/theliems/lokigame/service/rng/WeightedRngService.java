package com.theliems.lokigame.service.rng;

import com.theliems.lokigame.infrastructure.rng.WeightedSelector;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@Service
public class WeightedRngService {

    /**
     * Selects a random element from a list with uniform probability.
     * Centralizes RNG for consistency and future seeded/deterministic support.
     */
    public <T> T selectUniform(List<T> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(items.size());
        return items.get(index);
    }

    /**
     * Creates a new empty selector for stateful usage (e.g. Registries).
     */
    public <T> WeightedSelector<T> createSelector() {
        return new WeightedSelector<>();
    }

    /**
     * One-off roll for a collection of items.
     * Useful for Loot Tables generated on the fly.
     */
    public <T> T roll(Collection<T> items, Function<T, Double> weightExtractor) {
        WeightedSelector<T> selector = new WeightedSelector<>();
        for (T item : items) {
            selector.add(weightExtractor.apply(item), item);
        }
        return selector.next();
    }
}

package com.theliems.lokigame.service.rng;

import com.theliems.lokigame.infrastructure.rng.WeightedSelector;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.function.Function;

@Service
public class WeightedRngService {

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

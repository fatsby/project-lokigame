package com.theliems.lokigame.infrastructure.rng;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class WeightedSelector<T> {
    private final NavigableMap<Double, T> map = new TreeMap<>();
    private double totalWeight = 0;

    public void add(double weight, T item) {
        if (weight <= 0) return;
        totalWeight += weight;
        map.put(totalWeight, item);
    }

    public T next() {
        if (map.isEmpty()) return null;
        double value = ThreadLocalRandom.current().nextDouble() * totalWeight;
        return map.higherEntry(value).getValue();
    }
    
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    public void clear() {
        map.clear();
        totalWeight = 0;
    }
}

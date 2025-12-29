package com.theliems.lokigame.service.gameData.registry;

import com.theliems.lokigame.model.entity.hero.ClassDefinition;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HeroClassRegistry implements DataRegistry {
    private final Map<String, ClassDefinition> classes = new ConcurrentHashMap<>();

    public void add(ClassDefinition def) { classes.put(def.getId(), def); }
    public ClassDefinition get(String id) { return classes.get(id); }

    public ClassDefinition getRandomClass() {
        if (classes.isEmpty()) return null;
        return classes.values().stream()
                .skip(java.util.concurrent.ThreadLocalRandom.current().nextInt(classes.size()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void clear() { classes.clear(); }
}

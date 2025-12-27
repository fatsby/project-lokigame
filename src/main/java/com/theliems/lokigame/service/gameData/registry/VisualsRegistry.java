package com.theliems.lokigame.service.gameData.registry;

import com.theliems.lokigame.model.entity.visuals.VisualsContainer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class VisualsRegistry implements DataRegistry {

    private VisualsContainer container;

    public void initialize(VisualsContainer container) {
        this.container = container;
    }

    @Override
    public void clear() {
        this.container = null;
    }

    // --- Internal Logic Methods ---

    public String getRandomHair() {
        if (container == null || container.getHair().isEmpty()) return "default_hair";
        return getRandomFromList(container.getHair());
    }

    public String getRandomFace() {
        if (container == null || container.getFaces().isEmpty()) return "default_face";
        return getRandomFromList(container.getFaces());
    }

    // --- Helper Logic ---

    private String getRandomFromList(List<String> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
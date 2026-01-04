package com.theliems.lokigame.service.gameData.registry;

import com.theliems.lokigame.model.entity.visuals.VisualsContainer;
import com.theliems.lokigame.service.rng.WeightedRngService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VisualsRegistry implements DataRegistry {

    private final WeightedRngService rngService;

    private VisualsContainer container;

    public void initialize(VisualsContainer container) {
        this.container = container;
    }

    @Override
    public void clear() {
        this.container = null;
    }

    // --- Random Selection Methods (delegated to centralized RNG) ---

    public String getRandomHair() {
        if (container == null || container.getHair().isEmpty())
            return "default_hair";
        return rngService.selectUniform(container.getHair());
    }

    public String getRandomFace() {
        if (container == null || container.getFaces().isEmpty())
            return "default_face";
        return rngService.selectUniform(container.getFaces());
    }
}
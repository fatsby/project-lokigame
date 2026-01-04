package com.theliems.lokigame.service.gameData.registry;

import com.theliems.lokigame.model.entity.names.NamesContainer;
import com.theliems.lokigame.model.enums.HeroGender;
import com.theliems.lokigame.service.rng.WeightedRngService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NamesRegistry implements DataRegistry {

    private final WeightedRngService rngService;

    private NamesContainer container;

    public void initialize(NamesContainer container) {
        this.container = container;
    }

    @Override
    public void clear() {
        this.container = null;
    }

    /**
     * Gets a random first name based on gender.
     */
    public String getRandomFirstName(HeroGender gender) {
        if (container == null || container.getFirstNames() == null) {
            return "Unknown";
        }
        List<String> names = container.getFirstNames().get(gender.name());
        if (names == null || names.isEmpty()) {
            return "Unknown";
        }
        return rngService.selectUniform(names);
    }

    /**
     * Gets a random last name (shared across genders).
     */
    public String getRandomLastName() {
        if (container == null || container.getLastNames() == null || container.getLastNames().isEmpty()) {
            return "Unknown";
        }
        return rngService.selectUniform(container.getLastNames());
    }
}

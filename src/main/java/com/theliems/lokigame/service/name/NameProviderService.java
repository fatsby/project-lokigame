package com.theliems.lokigame.service.name;

import com.theliems.lokigame.model.enums.HeroGender;

import java.util.Random;

/**
 * Service for providing randomized names for heroes.
 * This decouples name generation from the HeroFactory.
 */
public interface NameProviderService {
    String getRandomFirstName(HeroGender gender, Random random);

    String getRandomLastName(Random random);
}

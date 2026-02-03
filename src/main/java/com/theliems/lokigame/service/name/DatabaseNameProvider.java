package com.theliems.lokigame.service.name;

import com.theliems.lokigame.model.entity.name.Name;
import com.theliems.lokigame.model.enums.HeroGender;
import com.theliems.lokigame.model.enums.NameType;
import com.theliems.lokigame.repository.system.NameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseNameProvider implements NameProviderService {

    private final NameRepository nameRepository;

    // Hardcoded fallbacks if DB is empty
    private static final String[] FALLBACK_FEMALE = { "Aria", "Luna", "Zara", "Nova", "Ivy" };
    private static final String[] FALLBACK_MALE = { "Kael", "Thorin", "Drake", "Rex", "Orion" };
    private static final String[] FALLBACK_LAST = { "Storm", "Shadow", "Flame", "Frost", "Light", "Dark", "Star",
            "Moon", "Sun", "Void" };

    @Override
    public String getRandomFirstName(HeroGender gender, Random random) {
        NameType type = (gender == HeroGender.FEMALE) ? NameType.FEMALE_HERO_NAME : NameType.MALE_HERO_NAME;
        List<String> names = nameRepository.findByType(type).stream()
                .map(Name::getName)
                .collect(Collectors.toList());

        if (names.isEmpty()) {
            log.warn("No names of type {} found in database, using fallback", type);
            String[] fallbacks = (gender == HeroGender.FEMALE) ? FALLBACK_FEMALE : FALLBACK_MALE;
            return fallbacks[random.nextInt(fallbacks.length)];
        }

        return names.get(random.nextInt(names.size()));
    }

    @Override
    public String getRandomLastName(Random random) {
        List<String> names = nameRepository.findByType(NameType.HERO_LASTNAME).stream()
                .map(Name::getName)
                .collect(Collectors.toList());

        if (names.isEmpty()) {
            log.warn("No hero last names found in database, using fallback");
            return FALLBACK_LAST[random.nextInt(FALLBACK_LAST.length)];
        }

        return names.get(random.nextInt(names.size()));
    }

    private static final String[] FALLBACK_EQUIPMENT = { "Cursed", "Ancient", "Hallowed" };

    @Override
    public String getRandomEquipmentPrefix(Random random) {
        List<String> names = nameRepository.findByType(NameType.EQUIPMENT).stream()
                .map(Name::getName)
                .collect(Collectors.toList());

        if (names.isEmpty()) {
            log.warn("No equipment names found in database, using fallback");
            return FALLBACK_EQUIPMENT[random.nextInt(FALLBACK_EQUIPMENT.length)];
        }

        return names.get(random.nextInt(names.size()));
    }
}

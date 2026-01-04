package com.theliems.lokigame.service.gameData;

import com.theliems.lokigame.model.entity.hero.ClassDefinition;
import com.theliems.lokigame.model.entity.inventory.ItemDefinition;
import com.theliems.lokigame.model.entity.world.WorldDefinition;
import com.theliems.lokigame.model.enums.EquipmentSlot;
import com.theliems.lokigame.service.gameData.registry.HeroClassRegistry;
import com.theliems.lokigame.service.gameData.registry.ItemRegistry;
import com.theliems.lokigame.service.gameData.registry.VisualsRegistry;
import com.theliems.lokigame.service.gameData.registry.WorldRegistry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
/**
 * This class is useless rn i think
 */
public class GameDataService {
    HeroClassRegistry heroClassesRegistry;
    VisualsRegistry visualsRegistry;
    WorldRegistry worldsRegistry;
    ItemRegistry itemRegistry;

    // --- Hero Class Delegation ---

    public ClassDefinition getHeroClass(String id) {
        return heroClassesRegistry.get(id);
    }

    // --- Visuals Delegation (Facade) ---

    /**
     * Gets a random hair ID from the registry.
     */
    public String getRandomHair() {
        return visualsRegistry.getRandomHair();
    }

    /**
     * Gets a random face ID from the registry.
     */
    public String getRandomFace() {
        return visualsRegistry.getRandomFace();
    }

    /**
     * Gets the base default top ID for the specific class.
     * 
     * @param classId The hero class ID (e.g., "mage")
     */
    public String getBaseTop(String classId) {
        ItemDefinition item = itemRegistry.getBaseItem(EquipmentSlot.BODY, classId);
        return item != null ? item.getId() : "default_body";
    }

    /**
     * Gets the base default bottom ID for the specific class.
     * 
     * @param classId The hero class ID (e.g., "mage")
     */
    public String getBaseBottom(String classId) {
        ItemDefinition item = itemRegistry.getBaseItem(EquipmentSlot.LEGS, classId);
        return item != null ? item.getId() : "default_legs";
    }

    public WorldDefinition rollWorld() {
        return worldsRegistry.rollRandomWorld();
    }

    public WorldDefinition getWorld(String id) {
        return worldsRegistry.get(id);
    }
}

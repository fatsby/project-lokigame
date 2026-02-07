package com.theliems.lokigame.service.dungeon;

import com.theliems.lokigame.infrastructure.constants.DungeonConstants;
import com.theliems.lokigame.model.entity.dungeon.DropTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for calculating DropTable values based on dungeon level.
 * Single Responsibility: Reward calculation using scaling formulas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DropTableCalculatorService {

    /**
     * Calculate a DropTable for a specific dungeon level with world difficulty
     * modifier.
     * 
     * @param dungeonLevel       The dungeon level
     * @param worldDifficultyMod The world's difficulty modifier
     * @return A calculated DropTable DTO
     */
    public DropTable calculate(int dungeonLevel, double worldDifficultyMod) {
        // Gold scaling: baseGold * (GOLD_SCALING ^ level) * worldMod
        double goldMultiplier = Math.pow(DungeonConstants.GOLD_SCALING, dungeonLevel) * worldDifficultyMod;
        long baseGold = DungeonConstants.BASE_GOLD;

        // XP scaling: baseXp * (XP_SCALING ^ level) * worldMod
        double xpMultiplier = Math.pow(DungeonConstants.XP_SCALING, dungeonLevel) * worldDifficultyMod;
        long baseXp = DungeonConstants.BASE_XP;

        // Equipment drop chance: increases with level, capped
        double equipDropChance = Math.min(
                DungeonConstants.BASE_EQUIP_DROP_CHANCE + (DungeonConstants.EQUIP_DROP_CHANCE_PER_LEVEL * dungeonLevel),
                DungeonConstants.MAX_EQUIP_DROP_CHANCE);

        // Material drop chance: increases with level, capped
        double materialDropChance = Math.min(
                DungeonConstants.BASE_MATERIAL_DROP_CHANCE
                        + (DungeonConstants.MATERIAL_DROP_CHANCE_PER_LEVEL * dungeonLevel),
                DungeonConstants.MAX_MATERIAL_DROP_CHANCE);

        log.debug(
                "Calculated drop table for level {} with worldMod {}: gold={}x{}, xp={}x{}, equipChance={}, matChance={}",
                dungeonLevel, worldDifficultyMod, baseGold, goldMultiplier, baseXp, xpMultiplier, equipDropChance,
                materialDropChance);

        return DropTable.builder()
                .baseGold(baseGold)
                .goldMultiplier(goldMultiplier)
                .baseXp(baseXp)
                .xpMultiplier(xpMultiplier)
                .equipmentDropChance(equipDropChance)
                .materialDropChance(materialDropChance)
                .build();
    }
}

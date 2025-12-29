package com.theliems.lokigame.model.entity.hero;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ClassDefinition {
    private String id;
    private String name;
    private Map<String, StatRange> baseStats;
    private Map<String, StatRange> statGrowth;
    private List<String> allowedWeaponTypes;
}

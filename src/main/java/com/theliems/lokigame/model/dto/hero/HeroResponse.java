package com.theliems.lokigame.model.dto.hero;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class HeroResponse {
    private UUID heroId;
    private String firstName;
    private String lastName;
    private String heroClassName;
    private String originName;
    private String worldName;
    private Integer level;
    private Integer star;
    private List<HeroStatResponse> stats;
    private Map<String, UUID> equipment; // EquipmentSlot -> Equipment UUID

    @Data
    @Builder
    public static class HeroStatResponse {
        private String statType;
        private Double baseValue;
        private Double finalValue;
    }
}

package com.theliems.lokigame.model.dto.hero;

import com.theliems.lokigame.model.enums.StatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OriginRequest {
    private String name;
    private String description;
    private Map<StatType, Double> statModifiers;
}

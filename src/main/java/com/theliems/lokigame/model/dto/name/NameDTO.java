package com.theliems.lokigame.model.dto.name;

import com.theliems.lokigame.model.enums.NameType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NameDTO {
    private UUID id;
    private String name;
    private NameType type;
}

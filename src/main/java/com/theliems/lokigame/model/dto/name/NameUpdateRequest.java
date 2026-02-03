package com.theliems.lokigame.model.dto.name;

import com.theliems.lokigame.model.enums.NameType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NameUpdateRequest {

    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    private NameType type;
}

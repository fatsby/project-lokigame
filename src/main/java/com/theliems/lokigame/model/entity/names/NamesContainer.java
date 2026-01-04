package com.theliems.lokigame.model.entity.names;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NamesContainer {
    private Map<String, List<String>> firstNames;
    private List<String> lastNames;
}

package com.theliems.lokigame.model.entity.visuals;

import lombok.Data;
import java.util.List;

@Data
public class VisualItem {
    private String id;
    private List<String> classRestriction; // e.g. ["mage"] or null for all
    private boolean isDefault;
}
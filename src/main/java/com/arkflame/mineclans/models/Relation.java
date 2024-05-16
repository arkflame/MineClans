package com.arkflame.mineclans.models;

import java.util.UUID;

import com.arkflame.mineclans.enums.RelationType;

public class Relation {
    private UUID factionId;
    private UUID targetFactionId;
    private RelationType relationType;

    public Relation(UUID factionId, UUID targetFactionId, String relationType) {
        this.factionId = factionId;
        this.targetFactionId = targetFactionId;
        this.relationType = parseRelationType(relationType);
    }
    
    private RelationType parseRelationType(String relationType) {
        try {
            return RelationType.valueOf(relationType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return RelationType.NEUTRAL; // Default to NEUTRAL if the relationType is not valid
        }
    }

    public UUID getFactionId() {
        return factionId;
    }

    public UUID getTargetFactionId() {
        return targetFactionId;
    }

    public RelationType getRelationType() {
        return relationType;
    }
}

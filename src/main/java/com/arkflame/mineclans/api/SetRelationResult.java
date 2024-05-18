package com.arkflame.mineclans.api;

import com.arkflame.mineclans.enums.RelationType;
import com.arkflame.mineclans.models.Faction;

public class SetRelationResult {
    public enum SetRelationResultState {
        SUCCESS,
        INVALID_RELATION_TYPE,
        NO_FACTION,
        OTHER_FACTION_NOT_FOUND, SAME_FACTION
    }

    private final SetRelationResultState state;
    private final Faction faction;
    private final Faction otherFaction;
    private final RelationType relation;

    public SetRelationResult(SetRelationResultState state, Faction faction, Faction otherFaction, RelationType relation) {
        this.state = state;
        this.faction = faction;
        this.otherFaction = otherFaction;
        this.relation = relation;
    }

    public SetRelationResultState getState() {
        return state;
    }

    public Faction getFaction() {
        return faction;
    }

    public Faction getOtherFaction() {
        return otherFaction;
    }

    public RelationType getRelation() {
        return relation;
    }
}

package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.enums.RelationType;
import com.arkflame.mineclans.models.Faction;

public class SetRelationResult {
    public enum SetRelationResultState {
        SUCCESS,
        INVALID_RELATION_TYPE,
        NO_FACTION,
        OTHER_FACTION_NOT_FOUND,
        SAME_FACTION,
        ALREADY_RELATION
    }

    private final SetRelationResultState state;
    private final Faction faction;
    private final Faction otherFaction;
    private final RelationType relation;
    private final RelationType otherRelation;

    public SetRelationResult(SetRelationResultState state, Faction faction, Faction otherFaction, RelationType relation, RelationType otherRelation) {
        this.state = state;
        this.faction = faction;
        this.otherFaction = otherFaction;
        this.relation = relation;
        this.otherRelation = otherRelation;
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

    public RelationType getOtherRelation() {
        return otherRelation;
    }
}

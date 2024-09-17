package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.models.Faction;

public class DisbandResult {
    private DisbandResultState state;
    private Faction faction;

    public DisbandResult(DisbandResultState state, Faction faction) {
        this.state = state;
        this.faction = faction;
    }

    public DisbandResult(DisbandResultState state) {
        this(state, null);
    }

    public DisbandResultState getState() {
        return state;
    }

    public Faction getFaction() {
        return faction;
    }

    public enum DisbandResultState {
        SUCCESS,
        NO_PERMISSION,
        NO_FACTION,
    }
}
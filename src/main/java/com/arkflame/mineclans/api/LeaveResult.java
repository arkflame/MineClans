package com.arkflame.mineclans.api;

import com.arkflame.mineclans.models.Faction;

public class LeaveResult {
    private Faction faction;
    private LeaveResultState state;

    // Constructor
    public LeaveResult(LeaveResultState state, Faction faction) {
        this.state = state;
        this.faction = faction;
    }

    // Getter for Faction
    public Faction getFaction() {
        return faction;
    }

    // Getter for CreateResultState
    public LeaveResultState getState() {
        return state;
    }

    public enum LeaveResultState {
        NO_FACTION,
        FACTION_OWNER,
        SUCCESS,
    }
}
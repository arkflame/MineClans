package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.models.Faction;

public class OpenResult {
    private OpenResultState state;
    private Faction faction = null;
    private boolean open;

    // Constructor
    public OpenResult(OpenResultState state) {
        this.state = state;
    }

    public OpenResult(OpenResultState state, Faction faction, boolean open) {
        this(state);
        this.faction = faction;
        this.open = open;
    }

    // Getter for OpenResultState
    public OpenResultState getState() {
        return state;
    }

    public Faction getFaction() {
        return faction;
    }

    public boolean isOpen() {
        return open;
    }

    public enum OpenResultState {
        SUCCESS,
        NO_FACTION,
        NO_PERMISSION,
    }
}
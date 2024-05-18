package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

public class JoinResult {
    private JoinResultState state;
    private Faction faction;
    private FactionPlayer factionPlayer;

    // Constructor
    public JoinResult(JoinResultState state, Faction faction, FactionPlayer factionPlayer) {
        this.state = state;
        this.faction = faction;
        this.factionPlayer = factionPlayer;
    }

    // Getter for CreateResultState
    public JoinResultState getState() {
        return state;
    }

    // Getter for Faction
    public Faction getFaction() {
        return faction;
    }

    // Getter for Faction
    public FactionPlayer getFactionPlayer() {
        return factionPlayer;
    }

    public enum JoinResultState {
        SUCCESS,
        NULL_NAME,
        NO_FACTION,
        ALREADY_HAVE_FACTION, NOT_INVITED,
    }
}
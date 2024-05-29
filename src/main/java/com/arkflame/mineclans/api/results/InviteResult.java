package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

public class InviteResult {
    private InviteResultState state;
    private FactionPlayer player = null;
    private Faction faction = null;

    // Constructor
    public InviteResult(InviteResultState state) {
        this.state = state;
    }

    public InviteResult(InviteResultState state, FactionPlayer player) {
        this(state);
        this.player = player;
    }

    public InviteResult(InviteResultState state, FactionPlayer player, Faction faction) {
        this(state, player);
        this.faction = faction;
    }

    // Getter for InviteResultState
    public InviteResultState getState() {
        return state;
    }

    public FactionPlayer getPlayer() {
        return player;
    }

    public Faction getFaction() {
        return faction;
    }

    public enum InviteResultState {
        SUCCESS,
        NO_FACTION,
        NO_PERMISSION,
        ALREADY_INVITED,
        MEMBER_EXISTS,
        PLAYER_NOT_FOUND,
    }
}

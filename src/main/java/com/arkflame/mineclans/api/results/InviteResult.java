package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.models.FactionPlayer;

public class InviteResult {
    private InviteResultState state;
    private FactionPlayer player = null;

    // Constructor
    public InviteResult(InviteResultState state) {
        this.state = state;
    }

    public InviteResult(InviteResultState state, FactionPlayer player) {
        this(state);
        this.player = player;
    }

    // Getter for InviteResultState
    public InviteResultState getState() {
        return state;
    }

    public FactionPlayer getPlayer() {
        return player;
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

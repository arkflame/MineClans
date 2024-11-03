package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.models.FactionPlayer;

public class AddDeathResult {
    private AddDeathResultState state;
    private FactionPlayer player;

    public AddDeathResult(AddDeathResultState state, FactionPlayer player) {
        this.state = state;
        this.player = player;
    }

    public AddDeathResultState getState() {
        return state;
    }

    public FactionPlayer getPlayer() {
        return player;
    }

    public enum AddDeathResultState {
        SUCCESS,
        ERROR, 
        NO_PLAYER
    }
}

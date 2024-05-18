package com.arkflame.mineclans.api.results;

public class FriendlyFireResult {
    public enum FriendlyFireResultState {
        ENABLED,
        DISABLED,
        NOT_IN_FACTION,
        ERROR, NO_PERMISSION
    }

    private final FriendlyFireResultState state;

    public FriendlyFireResult(FriendlyFireResultState state) {
        this.state = state;
    }

    public FriendlyFireResultState getState() {
        return state;
    }
}

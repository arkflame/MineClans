package com.arkflame.mineclans.api;

public class FriendlyFireResult {
    public enum FriendlyFireResultState {
        ENABLED,
        DISABLED,
        NOT_IN_FACTION,
        ERROR
    }

    private final FriendlyFireResultState state;

    public FriendlyFireResult(FriendlyFireResultState state) {
        this.state = state;
    }

    public FriendlyFireResultState getState() {
        return state;
    }
}

package com.arkflame.mineclans.api;

public class SetHomeResult {
    private final SetHomeResultState state;

    public SetHomeResult(SetHomeResultState state) {
        this.state = state;
    }

    public SetHomeResultState getState() {
        return state;
    }

    public enum SetHomeResultState {
        SUCCESS, NOT_IN_FACTION, ERROR
    }
}

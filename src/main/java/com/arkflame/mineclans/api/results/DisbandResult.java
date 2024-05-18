package com.arkflame.mineclans.api.results;

public class DisbandResult {
    private DisbandResultState state;

    // Constructor
    public DisbandResult(DisbandResultState state) {
        this.state = state;
    }

    // Getter for CreateResultState
    public DisbandResultState getState() {
        return state;
    }

    public enum DisbandResultState {
        SUCCESS,
        NOT_OWNER,
        NO_FACTION,
    }
}
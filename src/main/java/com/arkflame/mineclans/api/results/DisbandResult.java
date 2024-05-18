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
        NO_PERMISSION,
        NO_FACTION,
    }
}
package com.arkflame.mineclans.api.results;

public class FocusResult {
    private final FocusResultType type;

    public FocusResult(FocusResultType type) {
        this.type = type;
    }

    public FocusResultType getType() {
        return type;
    }

    public enum FocusResultType {
        SUCCESS,
        NOT_IN_FACTION,
        FACTION_NOT_FOUND,
        NO_PERMISSION, 
        SAME_FACTION
    }

}

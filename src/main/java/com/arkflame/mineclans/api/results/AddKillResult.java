package com.arkflame.mineclans.api.results;

public class AddKillResult {
    private final AddKillResultType type;

    public AddKillResult(AddKillResultType type) {
        this.type = type;
    }

    public AddKillResultType getType() {
        return type;
    }

    public enum AddKillResultType {
        SUCCESS,
        PLAYER_NOT_FOUND,
        SAME_FACTION,
        ALREADY_KILLED, NO_FACTION
    }
}

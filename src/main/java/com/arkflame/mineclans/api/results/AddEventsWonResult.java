package com.arkflame.mineclans.api.results;

public class AddEventsWonResult {
    private final AddEventsWonResultType type;

    public AddEventsWonResult(AddEventsWonResultType type) {
        this.type = type;
    }

    public AddEventsWonResultType getType() {
        return type;
    }

    public enum AddEventsWonResultType {
        SUCCESS, NO_FACTION, PLAYER_NOT_FOUND
    }
}

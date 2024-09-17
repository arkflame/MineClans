package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.models.Faction;

public class TransferResult {
    public enum TransferResultState {
        SUCCESS,
        NULL_NAME,
        NO_FACTION,
        NOT_OWNER,
        MEMBER_NOT_FOUND
    }

    private final TransferResultState state;
    private final Faction faction;

    public TransferResult(TransferResultState state, Faction faction) {
        this.state = state;
        this.faction = faction;
    }

    public TransferResultState getState() {
        return state;
    }

    public Faction getFaction() {
        return faction;
    }
}

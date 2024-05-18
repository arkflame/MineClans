package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

public class FactionChatResult {
    public enum FactionChatState {
        SUCCESS,
        NOT_IN_FACTION,
        ERROR
    }

    private final FactionChatState state;
    private final String message;
    private final Faction faction;
    private final FactionPlayer factionPlayer;

    public FactionChatResult(FactionChatState state, String message, Faction faction, FactionPlayer factionPlayer) {
        this.state = state;
        this.message = message;
        this.faction = faction;
        this.factionPlayer = factionPlayer;
    }

    public FactionChatState getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }

    public Faction getFaction() {
        return faction;
    }

    public FactionPlayer getFactionPlayer() {
        return factionPlayer;
    }
}

package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

public class KickResult {
    private KickResultType state;
    private Faction faction;
    private FactionPlayer factionPlayer;

    public KickResult(KickResultType state, Faction faction, FactionPlayer factionPlayer) {
        this.state = state;
        this.faction = faction;
        this.factionPlayer = factionPlayer;
    }

    public KickResultType getState() {
        return state;
    }

    public Faction getFaction() {
        return faction;
    }

    public FactionPlayer getFactionPlayer() {
        return factionPlayer;
    }

    public enum KickResultType {
        SUCCESS,
        NOT_IN_FACTION,
        NOT_MODERATOR,
        PLAYER_NOT_FOUND,
        SUPERIOR_RANK, NOT_YOURSELF;
    }
}
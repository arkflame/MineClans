package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

public class OpenChestResult {
    private final OpenChestResultType resultType;
    private final Faction faction;
    private final FactionPlayer player;

    public OpenChestResult(OpenChestResultType resultType, Faction faction, FactionPlayer player) {
        this.resultType = resultType;
        this.faction = faction;
        this.player = player;
    }

    public OpenChestResultType getResultType() {
        return resultType;
    }

    public Faction getFaction() {
        return faction;
    }

    public FactionPlayer getPlayer() {
        return player;
    }

    public enum OpenChestResultType {
        SUCCESS,
        NOT_IN_FACTION,
        ERROR,
        NO_PERMISSION
    }
}

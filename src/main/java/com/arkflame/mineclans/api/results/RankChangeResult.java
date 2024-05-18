package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.enums.Rank;

public class RankChangeResult {
    private RankChangeResultType resultType;
    private Rank rank;

    // Constructor
    public RankChangeResult(RankChangeResultType resultType, Rank rank) {
        this.resultType = resultType;
        this.rank = rank;
    }

    // Getter for ResultType
    public RankChangeResultType getResultType() {
        return resultType;
    }

    // Getter for Rank
    public Rank getRank() {
        return rank;
    }

    public enum RankChangeResultType {
        SUCCESS,
        PLAYER_NOT_FOUND,
        NOT_IN_FACTION,
        ADMIN_REQUIRED,
        SUPERIOR_RANK, 
        CANNOT_PROMOTE,
        CANNOT_DEMOTE, 
        CANNOT_PROMOTE_TO_LEADER,
    }
}

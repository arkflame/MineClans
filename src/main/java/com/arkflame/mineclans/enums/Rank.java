package com.arkflame.mineclans.enums;

public enum Rank {
    MEMBER,
    CAPTAIN,
    MODERATOR,
    ADMIN,
    COLEADER,
    LEADER;

    public Rank getNext() {
        switch (this) {
            case MEMBER:
                return CAPTAIN;
            case CAPTAIN:
                return MODERATOR;
            case MODERATOR:
                return ADMIN;
            case ADMIN:
                return COLEADER;
            case COLEADER:
                return LEADER;
            default:
                return null; // LEADER has no next rank
        }
    }

    public Rank getPrevious() {
        switch (this) {
            case LEADER:
                return COLEADER;
            case COLEADER:
                return ADMIN;
            case ADMIN:
                return MODERATOR;
            case MODERATOR:
                return CAPTAIN;
            case CAPTAIN:
                return MEMBER;
            default:
                return null; // MEMBER has no previous rank
        }
    }
}

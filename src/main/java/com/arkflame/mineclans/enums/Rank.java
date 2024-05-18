package com.arkflame.mineclans.enums;

public enum Rank {
    RECRUIT,
    MEMBER,
    MODERATOR,
    COLEADER,
    LEADER;

    public Rank getNext() {
        switch (this) {
            case RECRUIT:
                return MEMBER;
            case MEMBER:
                return MODERATOR;
            case MODERATOR:
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
                return MODERATOR;
            case MODERATOR:
                return MEMBER;
            case MEMBER:
                return RECRUIT;
            default:
                return null; // RECRUIT has no previous rank
        }
    }

    public boolean isEqualOrHigherThan(Rank rank) {
        return this.ordinal() >= rank.ordinal();
    }

    public boolean isLowerThan(Rank rank) {
        return this.ordinal() < rank.ordinal();
    }
}

package com.arkflame.mineclans.enums;

public enum EventObjectiveType {
    FACTION_KILL("Kill enemy faction members"),
    BLOCK_MINE("Mine blocks"),
    MOB_KILL("Kill mobs"),
    DIAMOND_MINE("Mine diamonds"),
    CROP_HARVEST("Harvest crops"),
    WOOD_MINE("Cut down trees"),
    FISHING("Catch fish");

    private final String action;

    EventObjectiveType(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}

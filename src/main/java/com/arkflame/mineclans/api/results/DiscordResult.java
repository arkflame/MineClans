package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.models.Faction;

public class DiscordResult {
    private DiscordResultState state;
    private Faction faction;
    private String discordLink; // Optional additional field for the Discord link, if applicable

    // Constructor for state, faction, and discord link
    public DiscordResult(DiscordResultState state, Faction faction, String discordLink) {
        this.state = state;
        this.faction = faction;
        this.discordLink = discordLink;
    }

    // Constructor for state and faction without discord link
    public DiscordResult(DiscordResultState state, Faction faction) {
        this(state, faction, null);
    }

    // Constructor for state only
    public DiscordResult(DiscordResultState state) {
        this(state, null, null);
    }

    // Getters
    public DiscordResultState getState() {
        return state;
    }

    public Faction getFaction() {
        return faction;
    }

    public String getDiscordLink() {
        return discordLink;
    }

    // Enum for the possible states of setting Discord
    public enum DiscordResultState {
        SUCCESS,
        NO_PERMISSION,
        NO_FACTION,
        INVALID_DISCORD_LINK,
        ERROR
    }
}

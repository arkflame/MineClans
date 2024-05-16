package com.arkflame.mineclans.models;

import java.util.Date;
import java.util.UUID;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.enums.Rank;

public class FactionPlayer {
    private UUID playerId;
    private String factionName;
    private Date joinDate;
    private Date lastActive;
    private int kills;
    private int deaths;

    public FactionPlayer(UUID playerId) {
        this.playerId = playerId;
        this.factionName = "";
        this.joinDate = null;
        this.lastActive = null;
        this.kills = 0;
        this.deaths = 0;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Faction getFaction() {
        if (factionName == null || factionName.isEmpty()) {
            return null;
        }
        return MineClans.getInstance().getFactionManager().getFaction(factionName);
    }

    public String getFactionName() {
        return factionName;
    }

    public void setFaction(Faction faction) {
        if (faction == null) {
            this.factionName = null;
        } else {
            this.factionName = faction.getName();
        }
    }

    public Rank getRank() {
        Faction faction = getFaction();
        if (faction == null) {
            return null;
        }
        return faction.getRank(playerId);
    }
    
    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setLastActive(Date lastActive) {
        this.lastActive = lastActive;
    }

    public Date getLastActive() {
        return lastActive;
    }

    public void updateLastActive() {
        this.lastActive = new Date();
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
}

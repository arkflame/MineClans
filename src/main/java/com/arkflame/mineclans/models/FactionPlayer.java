package com.arkflame.mineclans.models;

import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.ToggleChatResult;
import com.arkflame.mineclans.api.results.ToggleChatResult.ToggleChatState;
import com.arkflame.mineclans.enums.Rank;
import com.arkflame.mineclans.menus.EnteringType;

import java.util.Collection;
import java.util.HashSet;

public class FactionPlayer {
    private UUID playerId;
    private UUID factionId;
    private String name;
    private Date joinDate;
    private Date lastActive;
    private int kills;
    private int deaths;
    private ToggleChatResult.ToggleChatState chat = ToggleChatState.DISABLED;
    private Collection<UUID> killedPlayers = new HashSet<>();
    private EnteringType enteringType = EnteringType.DEPOSIT;
    private long enteringTime = 0;

    public FactionPlayer(UUID playerId) {
        this.playerId = playerId;
        this.name = null;
        this.factionId = null;
        this.joinDate = null;
        this.lastActive = null;
        this.kills = 0;
        this.deaths = 0;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Faction getFaction() {
        if (factionId == null) {
            return null;
        }
        return MineClans.getInstance().getFactionManager().getFaction(factionId);
    }

    public void setFaction(Faction faction) {
        if (faction == null) {
            this.factionId = null;
        } else {
            this.factionId = faction.getId();
        }
    }

    public UUID getFactionId() {
        return factionId;
    }

    public void setFactionId(UUID factionId) {
        this.factionId = factionId;
    }

    public void setFactionId(String factionId) {
        if (factionId == null) {
            this.factionId = null;
            return;
        }
        setFactionId(UUID.fromString(factionId));
    }

    public Rank getRank() {
        Faction faction = getFaction();
        if (faction == null) {
            return Rank.RECRUIT;
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

    public boolean addKill(UUID killedPlayerId) {
        // If player is already killed by this player, return false
        if (killedPlayers.contains(killedPlayerId)) {
            return false;
        }

        killedPlayers.add(killedPlayerId);
        kills++;

        return true;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerId);
    }

    public void toggleChat() {
        this.chat = getChatMode().getNext();
    }

    public ToggleChatResult.ToggleChatState getChatMode() {
        return getFactionId() != null ? (chat == ToggleChatResult.ToggleChatState.NOT_IN_FACTION ? ToggleChatResult.ToggleChatState.DISABLED : chat) : ToggleChatResult.ToggleChatState.NOT_IN_FACTION;
    }

    public boolean isOnline() {
        Player player = getPlayer();
        if (player == null) {
            return false;
        }
        return player.isOnline();
    }

    public void sendMessage(String msg) {
        Player player = getPlayer();
        if (player != null) {
            player.sendMessage(msg);
        }
    }

    public void setEnteringAmount(EnteringType type) {
        this.enteringType = type;
        this.enteringTime = System.currentTimeMillis();
    }

    public EnteringType getEnteringTypeIfValid() {
        long currentTime = System.currentTimeMillis();
        
        // Check if less than 30 seconds (30,000 milliseconds) have passed
        if (currentTime - enteringTime <= 30_000) {
            return enteringType;
        } else {
            return null; // Timeout, returning null
        }
    }    
}

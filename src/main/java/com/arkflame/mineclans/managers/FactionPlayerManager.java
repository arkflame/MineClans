package com.arkflame.mineclans.managers;

import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.enums.Rank;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FactionPlayerManager {
    // Cache for faction players
    private Map<UUID, FactionPlayer> factionPlayerCache = new ConcurrentHashMap<>();

    // Get or load a FactionPlayer by UUID
    public FactionPlayer getOrLoad(UUID playerId) {
        // Check cache first
        FactionPlayer factionPlayer = factionPlayerCache.get(playerId);
        if (factionPlayer != null) {
            return factionPlayer;
        }

        // If not in cache, load from database
        factionPlayer = loadFactionPlayerFromDatabase(playerId);
        if (factionPlayer != null) {
            factionPlayerCache.put(playerId, factionPlayer);
        } else {
            // Return a new faction player if not found
            factionPlayer = new FactionPlayer(playerId);
            factionPlayerCache.put(playerId, factionPlayer);
        }
        return factionPlayer;
    }

    // Save a FactionPlayer to the database
    public void save(FactionPlayer factionPlayer) {
        MineClans.getInstance().getMySQLProvider().getFactionPlayerDAO().insertOrUpdatePlayer(factionPlayer);
    }

    // Clear a FactionPlayer from the cache
    public void clearFromCache(UUID playerId) {
        factionPlayerCache.remove(playerId);
    }

    // Clear all faction players from cache
    public void clearFactionPlayers() {
        factionPlayerCache.clear();
    }

    // Placeholder method to load faction player from database
    private FactionPlayer loadFactionPlayerFromDatabase(UUID playerId) {
        return MineClans.getInstance().getMySQLProvider().getFactionPlayerDAO().getPlayerById(playerId);
    }

    public void updateJoinDate(UUID playerId) {
        FactionPlayer factionPlayer = factionPlayerCache.get(playerId);
        if (factionPlayer != null && factionPlayer.getJoinDate() == null) {
            factionPlayer.setJoinDate(new Date());
            save(factionPlayer); // Save to database after update
        }
    }

    // Update a FactionPlayer's last active time
    public void updateLastActive(UUID playerId) {
        FactionPlayer factionPlayer = factionPlayerCache.get(playerId);
        if (factionPlayer != null) {
            factionPlayer.setLastActive(new Date());
            save(factionPlayer); // Save to database after update
        }
    }

    // Add a kill to a FactionPlayer
    public void addKill(UUID playerId) {
        FactionPlayer factionPlayer = factionPlayerCache.get(playerId);
        if (factionPlayer != null) {
            factionPlayer.setKills(factionPlayer.getKills() + 1);
            save(factionPlayer); // Save to database after update
        }
    }

    // Add a death to a FactionPlayer
    public void addDeath(UUID playerId) {
        FactionPlayer factionPlayer = factionPlayerCache.get(playerId);
        if (factionPlayer != null) {
            factionPlayer.setDeaths(factionPlayer.getDeaths() + 1);
            save(factionPlayer); // Save to database after update
        }
    }

    // Update a FactionPlayer's faction
    public void updateFaction(UUID playerId, Faction faction) {
        FactionPlayer factionPlayer = factionPlayerCache.get(playerId);
        if (factionPlayer != null) {
            factionPlayer.setFaction(faction);
            save(factionPlayer); // Save to database after update
        }
    }

    // Update a FactionPlayer's rank
    public void updateRank(UUID playerId, Rank rank) {
        FactionPlayer factionPlayer = factionPlayerCache.get(playerId);
        if (factionPlayer != null) {
            Faction faction = factionPlayer.getFaction();
            faction.setRank(playerId, rank);
        }
    }
}

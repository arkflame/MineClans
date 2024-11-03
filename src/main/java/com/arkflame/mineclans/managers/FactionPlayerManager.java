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
    private Map<UUID, FactionPlayer> factionPlayerCacheById = new ConcurrentHashMap<>();

    // Cache for faction players
    private Map<String, FactionPlayer> factionPlayerCacheByName = new ConcurrentHashMap<>();

    // Get or load a FactionPlayer by UUID
    public FactionPlayer getOrLoad(UUID playerId) {
        if (playerId == null) {
            return null;
        }
        // Check cache first
        FactionPlayer factionPlayer = factionPlayerCacheById.get(playerId);
        if (factionPlayer != null) {
            if (factionPlayer.getName() != null && !factionPlayerCacheByName.containsKey(factionPlayer.getName())) {
                factionPlayerCacheByName.put(factionPlayer.getName(), factionPlayer);
            }

            return factionPlayer;
        }

        // If not in cache, load from database
        factionPlayer = loadFactionPlayerFromDatabase(playerId);
        if (factionPlayer != null) {
            factionPlayerCacheById.put(playerId, factionPlayer);
            if (factionPlayer.getName() != null) {
                factionPlayerCacheByName.put(factionPlayer.getName(), factionPlayer);
            }
        } else {
            // Return a new faction player if not found
            factionPlayer = new FactionPlayer(playerId);
            factionPlayerCacheById.put(playerId, factionPlayer);
        }
        return factionPlayer;
    }

    // Get or load a FactionPlayer by name
    public FactionPlayer getOrLoad(String playerName) {
        if (playerName == null) {
            return null;
        }
        // Check cache first
        FactionPlayer factionPlayer = factionPlayerCacheByName.get(playerName);
        if (factionPlayer != null) {
            if (factionPlayer.getPlayerId() != null && !factionPlayerCacheById.containsKey(factionPlayer.getPlayerId())) {
                factionPlayerCacheById.put(factionPlayer.getPlayerId(), factionPlayer);
            }

            return factionPlayer;
        }

        // If not in cache, load from database
        factionPlayer = loadFactionPlayerFromDatabase(playerName);
        if (factionPlayer != null) {
            if (factionPlayer.getPlayerId() != null) {
                factionPlayerCacheById.put(factionPlayer.getPlayerId(), factionPlayer);
            }
            factionPlayerCacheByName.put(playerName, factionPlayer);
        }
        return factionPlayer;
    }

    public void save(UUID uuid) {
        save(getOrLoad(uuid));
    }

    // Save a FactionPlayer to the database
    public void save(FactionPlayer factionPlayer) {
        if (factionPlayer == null) return;
        MineClans.getInstance().getMySQLProvider().getFactionPlayerDAO().insertOrUpdatePlayer(factionPlayer);
    }

    // Clear a FactionPlayer from the cache
    public void clearFromCache(UUID playerId) {
        factionPlayerCacheById.remove(playerId);
    }

    // Clear all faction players from cache
    public void clearFactionPlayers() {
        factionPlayerCacheById.clear();
    }

    // Placeholder method to load faction player from database
    public FactionPlayer loadFactionPlayerFromDatabase(UUID playerId) {
        return MineClans.getInstance().getMySQLProvider().getFactionPlayerDAO().getPlayerById(playerId);
    }

    public FactionPlayer loadFactionPlayerFromDatabase(String name) {
        return MineClans.getInstance().getMySQLProvider().getFactionPlayerDAO().getPlayerByName(name);
    }

    public void updateJoinDate(UUID playerId) {
        FactionPlayer factionPlayer = getOrLoad(playerId);
        if (factionPlayer != null && factionPlayer.getJoinDate() == null) {
            factionPlayer.setJoinDate(new Date());
        }
    }

    // Update a FactionPlayer's last active time
    public void updateLastActive(UUID playerId) {
        FactionPlayer factionPlayer = getOrLoad(playerId);
        if (factionPlayer != null) {
            factionPlayer.setLastActive(new Date());
        }
    }

    // Add a death to a FactionPlayer
    public void addDeath(UUID playerId) {
        FactionPlayer factionPlayer = getOrLoad(playerId);
        if (factionPlayer != null) {
            factionPlayer.setDeaths(factionPlayer.getDeaths() + 1);
        }
    }

    // Update a FactionPlayer's faction
    public void updateFaction(UUID playerId, Faction faction) {
        FactionPlayer factionPlayer = getOrLoad(playerId);
        if (factionPlayer != null) {
            factionPlayer.setFaction(faction);
        }
    }

    public void updateName(UUID playerId, String name) {
        FactionPlayer factionPlayer = getOrLoad(playerId);
        if (factionPlayer != null) {
            factionPlayer.setName(name);
        }
    }

    // Update a FactionPlayer's rank
    public void updateRank(UUID playerId, Rank rank) {
        FactionPlayer factionPlayer = getOrLoad(playerId);
        if (factionPlayer != null) {
            Faction faction = factionPlayer.getFaction();
            if (faction != null) {
                faction.setRank(playerId, rank);
            }
        }
    }
}

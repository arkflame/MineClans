package com.arkflame.mineclans.managers;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.providers.daos.PowerDAO;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class LeaderboardManager {
    private final PowerDAO powerDAO;
    private final Map<Integer, AtomicReference<UUID>> cacheManagerByPosition;
    private final Map<UUID, Integer> cacheManagerByFaction;

    public LeaderboardManager(PowerDAO powerDAO) {
        this.powerDAO = powerDAO;
        this.cacheManagerByPosition = new ConcurrentHashMap<>();
        this.cacheManagerByFaction = new ConcurrentHashMap<>();
    }

    public Faction getFactionByPosition(int position) {
        if (cacheManagerByPosition.containsKey(position)) {
            UUID id = cacheManagerByPosition.get(position).get();
            if (id != null) {
                return MineClans.getInstance().getAPI().getFaction(id);
            } else {
                return null;
            }
        }
        UUID id = powerDAO.getFactionIdByPosition(position);
        updateCache(position, id);
        if (id == null) {
            return null; // Handle case where no faction exists at this position
        }
        return MineClans.getInstance().getAPI().getFaction(id);
    }

    public int getPositionByFaction(UUID factionId) {
        Integer position = cacheManagerByFaction.get(factionId);
        if (position != null) {
            return position;
        }
        position = powerDAO.getFactionPosition(factionId);
        updateCache(position, factionId);
        return position;
    }

    public void onFactionUpdatePower(UUID factionId) {
        int newPosition = powerDAO.getFactionPosition(factionId);
        Integer cachedPosition = cacheManagerByFaction.get(factionId);

        if (cachedPosition != null && newPosition != cachedPosition) {
            clearCache();
        }
        updateCache(newPosition, factionId);
    }

    private void updateCache(int position, UUID factionId) {
        cacheManagerByPosition.put(position, new AtomicReference<>(factionId));
        cacheManagerByFaction.put(factionId, position);
    }

    public void removeFaction(UUID factionId) {
        powerDAO.removeFaction(factionId);
        Integer position = cacheManagerByFaction.get(factionId);
        if (position != null) {
            clearCache();
        }
    }

    private void clearCache() {
        cacheManagerByPosition.clear();
        cacheManagerByFaction.clear();
    }
}

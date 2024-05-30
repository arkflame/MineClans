package com.arkflame.mineclans.managers;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.providers.daos.PowerDAO;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LeaderboardManager {
    private final PowerDAO powerDAO;
    private final LocalCacheManager<Integer, UUID> cacheManagerByPosition;
    private final LocalCacheManager<UUID, Integer> cacheManagerByFaction;

    public LeaderboardManager(PowerDAO powerDAO) {
        this.powerDAO = powerDAO;
        this.cacheManagerByPosition = new LocalCacheManager<>(60, TimeUnit.SECONDS);
        this.cacheManagerByFaction = new LocalCacheManager<>(60, TimeUnit.SECONDS);
    }

    public Faction getFactionByPosition(int position) {
        UUID id = cacheManagerByPosition.getCache(position);
        if (id != null) {
            return MineClans.getInstance().getAPI().getFaction(id);
        }
        id = powerDAO.getFactionIdByPosition(position);
        if (id == null) {
            updateCache(position, id);
            return null; // Handle case where no faction exists at this position
        }
        updateCache(position, id);
        return MineClans.getInstance().getAPI().getFaction(id);
    }

    public int getPositionByFaction(UUID factionId) {
        Integer position = cacheManagerByFaction.getCache(factionId);
        if (position != null) {
            return position;
        }
        position = powerDAO.getFactionPosition(factionId);
        updateCache(position, factionId);
        return position;
    }

    public void updateFactionPower(UUID factionId, double power) {
        powerDAO.updateFactionPower(factionId, power);
        int position = powerDAO.getFactionPosition(factionId);
        updateCache(position, factionId, true);
    }

    private void updateCache(int position, UUID factionId, boolean changed) {
        if (changed) {
            cacheManagerByPosition.invalidateCache();
            cacheManagerByFaction.invalidateCache();
        }
        cacheManagerByPosition.setCache(position, factionId, 60, TimeUnit.SECONDS);
        cacheManagerByFaction.setCache(factionId, position, 60, TimeUnit.SECONDS);
    }

    private void updateCache(int position, UUID factionId) {
        updateCache(position, factionId, false);
    }

    public void removeFaction(UUID factionId) {
        powerDAO.removeFaction(factionId);
        Integer position = cacheManagerByFaction.getCache(factionId);
        if (position != null) {
            cacheManagerByPosition.invalidateCache(position);
            cacheManagerByFaction.invalidateCache(factionId);
        }
    }
}

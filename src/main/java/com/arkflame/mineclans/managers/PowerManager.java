package com.arkflame.mineclans.managers;

import com.arkflame.mineclans.providers.daos.PowerDAO;

import java.util.UUID;

public class PowerManager {
    private final PowerDAO powerDAO;
    private final LeaderboardManager leaderboardManager;

    public PowerManager(PowerDAO powerDAO, LeaderboardManager leaderboardManager) {
        this.powerDAO = powerDAO;
        this.leaderboardManager = leaderboardManager;
    }

    public void updatePower(UUID factionId, double newPower) {
        // Update the power in the database
        powerDAO.updateFactionPower(factionId, newPower);
        // Notify the leaderboard manager about the power update
        leaderboardManager.onFactionUpdatePower(factionId);
    }
}

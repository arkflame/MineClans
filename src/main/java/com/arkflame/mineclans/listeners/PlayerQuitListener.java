package com.arkflame.mineclans.listeners;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.managers.FactionPlayerManager;

public class PlayerQuitListener implements Listener {
    private FactionPlayerManager factionPlayerManager;

    public PlayerQuitListener(FactionPlayerManager factionPlayerManager) {
        this.factionPlayerManager = factionPlayerManager;
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        MineClans.runAsync(() -> {
            UUID id = event.getPlayer().getUniqueId();
            factionPlayerManager.updateLastActive(id);
        });
    }
}

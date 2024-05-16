package com.arkflame.mineclans.listeners;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.managers.FactionPlayerManager;

public class PlayerJoinListener implements Listener {
    private FactionPlayerManager factionPlayerManager;

    public PlayerJoinListener(FactionPlayerManager factionPlayerManager) {
        this.factionPlayerManager = factionPlayerManager;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        MineClans.runAsync(() -> {
            UUID id = event.getPlayer().getUniqueId();
            factionPlayerManager.updateJoinDate(id);
            factionPlayerManager.updateLastActive(id);
            factionPlayerManager.updateName(id, event.getPlayer().getName());
        });
    }
}

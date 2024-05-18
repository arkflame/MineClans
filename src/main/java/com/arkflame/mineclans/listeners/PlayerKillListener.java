package com.arkflame.mineclans.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.arkflame.mineclans.MineClans;

import org.bukkit.entity.Player;

public class PlayerKillListener implements Listener {

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        // Get the killed player
        Player killed = event.getEntity();

        // Check if the killer is a player
        if (killed.getKiller() != null) {
            Player killer = killed.getKiller();

            MineClans.runAsync(() -> {
                MineClans.getInstance().getAPI().addKill(killer, killed).getType().name();
            });
        }
    }
}

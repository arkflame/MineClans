package com.arkflame.mineclans.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.tasks.TeleportScheduler;

public class PlayerMoveListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        TeleportScheduler teleportScheduler = MineClans.getInstance().getTeleportScheduler();

        // Check if player is teleporting
        if (teleportScheduler.isTeleporting(player)) {
            // Ignore if only the player's head rotated (yaw/pitch change)
            if (event.getFrom().getX() == event.getTo().getX() &&
                    event.getFrom().getY() == event.getTo().getY() &&
                    event.getFrom().getZ() == event.getTo().getZ()) {
                return; // No actual movement, only head rotation
            }

            // Get the original teleport start location
            Location startLocation = teleportScheduler.getStartLocation(player);
            if (startLocation != null) {
                // Check if the player moved more than 1 block from the starting location
                if (startLocation.distance(event.getTo()) > 1.0) {
                    ConfigWrapper messages = MineClans.getInstance().getMessages();
                    String basePath = "factions.home.";
                    teleportScheduler.cancelTeleport(player);
                    player.sendMessage(messages.getText(basePath + "cancelled"));
                }
            }
        }
    }
}

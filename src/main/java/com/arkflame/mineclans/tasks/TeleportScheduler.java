package com.arkflame.mineclans.tasks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.utils.LocationData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportScheduler {

    private final JavaPlugin plugin;
    private final Map<UUID, TeleportTask> teleportMap = new ConcurrentHashMap<>();

    private BukkitTask task;

    public TeleportScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
        startTask();
    }

    /**
     * Schedule a teleport for a player.
     * Replaces any existing scheduled teleport for that player.
     *
     * @param player         the player to teleport
     * @param locationData   the target location
     * @param delayInSeconds the delay before teleporting
     */
    public void schedule(Player player, LocationData locationData, int delayInSeconds) {
        if (player == null || locationData == null || delayInSeconds <= 0)
            return;
        UUID playerId = player.getUniqueId();
        long scheduledTime = System.currentTimeMillis() + (delayInSeconds * 1000L);
        Location startLocation = player.getLocation();

        // Replace old teleport if exists
        teleportMap.put(playerId, new TeleportTask(player, locationData, scheduledTime, startLocation));
    }

    /**
     * Check if a player is currently scheduled for teleport.
     *
     * @param player the player to check
     * @return true if the player has a scheduled teleport, false otherwise
     */
    public boolean isTeleporting(Player player) {
        return player != null && teleportMap.containsKey(player.getUniqueId());
    }

    /**
     * Get the starting location of a player's teleport.
     *
     * @param player the player to check
     * @return the starting location or null if not found
     */
    public Location getStartLocation(Player player) {
        TeleportTask task = teleportMap.get(player.getUniqueId());
        return task != null ? task.getStartLocation() : null;
    }

    /**
     * Cancel a scheduled teleport for a player.
     *
     * @param player the player whose teleport should be cancelled
     * @return true if a teleport was cancelled, false if none existed
     */
    public boolean cancelTeleport(Player player) {
        if (player == null)
            return false;
        return teleportMap.remove(player.getUniqueId()) != null;
    }

    public void stopTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    // Start the repeating task to check and process scheduled teleports
    private void startTask() {
        stopTask();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                teleportMap.forEach((uuid, teleportTask) -> {
                    if (currentTime >= teleportTask.getScheduledTime()) {
                        teleportTask.teleport();
                        teleportMap.remove(uuid); // Remove after teleporting
                    }
                });
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 20L); // Runs every second (20 ticks)
    }

    // Inner class to hold teleport data
    private static class TeleportTask {
        private final Player player;
        private final LocationData locationData;
        private final long scheduledTime;
        private final Location startLocation;

        public TeleportTask(Player player, LocationData locationData, long scheduledTime, Location startLocation) {
            this.player = player;
            this.locationData = locationData;
            this.scheduledTime = scheduledTime;
            this.startLocation = startLocation;
        }

        public LocationData getLocationData() {
            return locationData;
        }

        public long getScheduledTime() {
            return scheduledTime;
        }

        public Location getStartLocation() {
            return startLocation;
        }

        public void teleport() {
            if (player != null && player.isOnline()) {
                MineClans.runSync(() -> {
                    String basePath = "factions.home.";
                    locationData.teleport(player);
                    player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success"));
                });
            }
        }
    }
}

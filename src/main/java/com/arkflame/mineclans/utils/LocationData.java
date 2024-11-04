package com.arkflame.mineclans.utils;

import com.arkflame.mineclans.MineClans;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LocationData {
    private final String worldName;
    private final double x, y, z;
    private final float pitch, yaw;
    private final String serverName;

    public LocationData(Location location, String serverName) {
        this(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getPitch(),
                location.getYaw(), serverName);
    }

    public LocationData(String worldName, double x, double y, double z, float pitch, float yaw, String serverName) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.serverName = serverName;
    }

    /**
     * Gets the world name of this location.
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Gets the server name of this location.
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Gets the X coordinate of this location.
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the Y coordinate of this location.
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the Z coordinate of this location.
     */
    public double getZ() {
        return z;
    }

    /**
     * Gets the pitch of this location.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Gets the yaw of this location.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Converts this LocationData to a Bukkit Location, if possible.
     * Returns null if the world does not exist on the current server.
     */
    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);
        return (world != null) ? new Location(world, x, y, z, yaw, pitch) : null;
    }

    /**
     * Checks if this LocationData can resolve to a valid Bukkit Location.
     */
    public boolean isValidLocation() {
        return getLocation() != null;
    }

    /**
     * Effectively teleport the player to the location
     * in the current server world and location if present.
     * 
     * @param player
     * @return
     */
    public boolean teleportNow(Player player) {
        // Teleport locally if the world is valid
        Location location = getLocation();
        if (location != null) {
            player.teleport(location);
            return true;
        }
        return false;
    }

    /**
     * Attempts to teleport the player to this LocationData.
     * If serverName is specified, it will use BungeeUtil to teleport the player to
     * the correct server.
     * If serverName is null, it teleports the player locally if the world exists.
     *
     * @param player The player to teleport.
     * @return True if teleportation was successful, false otherwise.
     */
    public boolean teleport(Player player) {
        if (serverName != null) {
            // Use BungeeUtil to teleport across servers
            BungeeUtil bungeeUtil = MineClans.getInstance().getBungeeUtil();
            bungeeUtil.getCurrentServer(player, (currentServer) -> {
                if (currentServer == null || currentServer.equals(serverName)) {
                    teleportNow(player);
                } else {
                    bungeeUtil.sendPlayerToServer(player, serverName);

                    // Send Redis request for cross-server handling
                    MineClans.getInstance().getRedisProvider().requestHome(player.getUniqueId());
                }
            });
            return true;
        } else {
            return teleportNow(player);
        }
    }

    /**
     * Provides a string representation of this LocationData.
     */
    @Override
    public String toString() {
        return "LocationData{" +
                "worldName='" + worldName + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", pitch=" + pitch +
                ", yaw=" + yaw +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}

package com.arkflame.mineclans.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {

    public static Location parseLocation(String locationString) {
        // Assuming the location string is in the format "world,x,y,z,yaw,pitch"
        String[] parts = locationString.split(",");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid location string format");
        }

        String worldName = parts[0];
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);

        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public static String locationToString(Location location) {
        // Convert location to a string in the format "world,x,y,z,yaw,pitch"
        StringBuilder builder = new StringBuilder();
        builder.append(location.getWorld().getName()).append(",");
        builder.append(location.getX()).append(",");
        builder.append(location.getY()).append(",");
        builder.append(location.getZ()).append(",");
        builder.append(location.getYaw()).append(",");
        builder.append(location.getPitch());
        return builder.toString();
    }
}

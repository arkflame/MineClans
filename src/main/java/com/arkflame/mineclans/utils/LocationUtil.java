package com.arkflame.mineclans.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {

    public static Location parseLocation(String locationString) {
        if (locationString == null) {
            return null;
        }
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
        if (location == null) {
            return null;
        }
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

    /**
     * Parses a location string to a LocationData object.
     * Supports old format "world,x,y,z,yaw,pitch" and new format "world,x,y,z,yaw,pitch,serverName".
     *
     * @param locationString The location string to parse.
     * @return A LocationData object representing the location.
     */
    public static LocationData parseLocationData(String locationString) {
        if (locationString == null) {
            return null;
        }
        
        String[] parts = locationString.split(",");
        if (parts.length < 6 || parts.length > 7) {
            throw new IllegalArgumentException("Invalid location string format");
        }
        
        String worldName = parts[0];
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);
        String serverName = (parts.length == 7) ? parts[6] : null;
        
        return new LocationData(worldName, x, y, z, pitch, yaw, serverName);
    }

    /**
     * Converts a LocationData object to a string.
     * Outputs format "world,x,y,z,yaw,pitch" if serverName is null,
     * otherwise "world,x,y,z,yaw,pitch,serverName".
     *
     * @param locationData The LocationData object to convert.
     * @return A string representation of the LocationData.
     */
    public static String locationDataToString(LocationData locationData) {
        if (locationData == null) {
            return null;
        }
        
        StringBuilder builder = new StringBuilder();
        builder.append(locationData.getWorldName()).append(",");
        builder.append(locationData.getX()).append(",");
        builder.append(locationData.getY()).append(",");
        builder.append(locationData.getZ()).append(",");
        builder.append(locationData.getYaw()).append(",");
        builder.append(locationData.getPitch());
        
        if (locationData.getServerName() != null) {
            builder.append(",").append(locationData.getServerName());
        }
        
        return builder.toString();
    }
}

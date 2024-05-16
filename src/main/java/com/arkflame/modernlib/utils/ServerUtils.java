package com.arkflame.modernlib.utils;

import org.bukkit.Bukkit;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

public class ServerUtils {
    private static String serverVersion;
    private static Method getServerMethod;
    private static Field tpsField;
    private static Object serverInstance;
    private static final DecimalFormat format = new DecimalFormat("##.##");

    static {
        initializeServerUtils();
    }

    private static void initializeServerUtils() {
        try {
            serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            int majorVersion = Integer.parseInt(serverVersion.split("_")[1]);

            String className;
            if (majorVersion >= 17) {
                className = "net.minecraft.server.MinecraftServer";
            } else {
                className = "net.minecraft.server." + serverVersion + ".MinecraftServer";
            }

            Class<?> minecraftServerClass = Class.forName(className);
            getServerMethod = minecraftServerClass.getMethod("getServer");
            serverInstance = getServerMethod.invoke(null);
            tpsField = minecraftServerClass.getDeclaredField("recentTps");
            tpsField.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize server utilities", e);
        }
    }

    public static double getTPS(int time) {
        try {
            double[] tpsArray = (double[]) tpsField.get(serverInstance);
            return tpsArray[Math.min(time, tpsArray.length - 1)];
        } catch (IllegalAccessException e) {
            // Failed to get TPS
        }
        return 20D;
    }

    public static String getTPSFormatted(int time) {
        double tps = getTPS(time);
        String tpsColor = tps >= 18.0 ? "&a" : tps >= 15.0 ? "&e" : "&c"; // Set color based on TPS value
        return tpsColor + format.format(tps);
    }
}

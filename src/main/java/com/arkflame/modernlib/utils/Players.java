package com.arkflame.modernlib.utils;

import java.util.List;

import org.bukkit.entity.Player;

public class Players {
    public static void setFlying(Player player, boolean flying) {
        if (player.getAllowFlight() != flying) {
            player.setAllowFlight(flying);
        }
        if (player.isFlying() != flying) {
            player.setFlying(flying);
        }
    }

    public static void clearInventory(Player player) {
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
    }

    public static void sendMessage(Player player, List<String> textList) {
        for (String text : textList) {
            player.sendMessage(text);
        }
    }

    public static void heal(Player player) {
        player.setHealth(20D);
        player.setFoodLevel(20);
    }
}

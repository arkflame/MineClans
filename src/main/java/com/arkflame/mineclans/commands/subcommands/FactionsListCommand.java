package com.arkflame.mineclans.commands.subcommands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;

public class FactionsListCommand {
    public static void onCommand(Player player) {
        // HashMap to store factions and their members count
        Map<String, Integer> factionCountMap = new HashMap<>();

        // Loop through all online players and count faction members
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Faction faction = MineClans.getInstance().getAPI().getFaction(onlinePlayer);

            if (faction != null) {
                String factionName = faction.getName();
                factionCountMap.put(factionName, factionCountMap.getOrDefault(factionName, 0) + 1);
            }
        }

        // StringBuilder to create the message
        StringBuilder factionsListMessage = new StringBuilder(ChatColor.GOLD + "Factions Online:\n");

        for (Map.Entry<String, Integer> entry : factionCountMap.entrySet()) {
            String factionName = entry.getKey();
            int memberCount = entry.getValue();
            factionsListMessage.append(ChatColor.YELLOW)
                               .append(factionName)
                               .append(ChatColor.WHITE)
                               .append(" - ")
                               .append(ChatColor.GREEN)
                               .append(memberCount)
                               .append(" members\n");
        }

        // Send the list to the player
        player.sendMessage(factionsListMessage.toString());
    }
}

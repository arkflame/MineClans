package com.arkflame.mineclans.commands.subcommands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;

public class FactionsListCommand {
    public static void onCommand(Player player) {
        Map<String, Integer> factionCountMap = new HashMap<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Faction faction = MineClans.getInstance().getAPI().getFaction(onlinePlayer);

            if (faction != null) {
                String factionName = faction.getName();
                factionCountMap.put(factionName, factionCountMap.getOrDefault(factionName, 0) + 1);
            }
        }

        StringBuilder factionsListMessage = new StringBuilder(MineClans.getInstance().getMessages().getText("factions.list.header"));

        for (Map.Entry<String, Integer> entry : factionCountMap.entrySet()) {
            String factionName = entry.getKey();
            int memberCount = entry.getValue();
            String entryMessage = MineClans.getInstance().getMessages().getText("factions.list.entry")
                                 .replace("%faction%", factionName)
                                 .replace("%members%", String.valueOf(memberCount));
            factionsListMessage.append(entryMessage);
        }

        player.sendMessage(factionsListMessage.toString());
    }
}

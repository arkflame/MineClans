package com.arkflame.mineclans.commands.subcommands;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;

public class FactionsListCommand {
    private static final int FACTIONS_PER_PAGE = 10;

    public static void onCommand(Player player, ModernArguments args) {
        ConfigWrapper messages = MineClans.getInstance().getMessages();

        // Get the page number from arguments, default to page 1 if not provided
        int page = 1;
        if (args.hasArg(1)) {
            try {
                page = Math.max(1, Integer.parseInt(args.getText(1)));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Get factions and member counts
        Map<Faction, Integer> factionCountMap = new HashMap<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Faction faction = MineClans.getInstance().getAPI().getFaction(onlinePlayer);
            if (faction != null) {
                factionCountMap.put(faction, factionCountMap.getOrDefault(faction, 0) + 1);
            }
        }

        // Sort factions by size (member count) and convert to list
        List<Map.Entry<Faction, Integer>> sortedFactions = factionCountMap.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()))
                .collect(Collectors.toList());

        // Determine the total number of pages
        int totalPages = (int) Math.ceil((double) sortedFactions.size() / FACTIONS_PER_PAGE);
        totalPages = Math.max(1, totalPages);  // Ensure at least one page exists

        // Validate the requested page number
        if (page > totalPages) {
            player.sendMessage(messages.getText("factions.list.invalid_page"));
            return;
        }

        // Display header
        player.sendMessage(messages.getText("factions.list.header"));

        // Paginate and display factions
        int startIndex = (page - 1) * FACTIONS_PER_PAGE;
        int endIndex = Math.min(startIndex + FACTIONS_PER_PAGE, sortedFactions.size());

        for (int i = startIndex; i < endIndex; i++) {
            Map.Entry<Faction, Integer> entry = sortedFactions.get(i);
            Faction faction = entry.getKey();
            int memberCount = faction.getOnlineMembers().size();
            int maxMembers = faction.getMembers().size();  // Assuming this method exists

            String entryMessage = messages.getText("factions.list.entry")
                    .replace("%faction%", faction.getName())
                    .replace("%members%", String.valueOf(memberCount))
                    .replace("%max_members%", String.valueOf(maxMembers))
                    .replace("%faction_level%", String.valueOf(faction.getPower()));  // Assuming faction level exists

            player.sendMessage(entryMessage);
        }

        // Display footer with pagination info
        String footerMessage = messages.getText("factions.list.footer")
                .replace("%current_page%", String.valueOf(page))
                .replace("%total_pages%", String.valueOf(totalPages));

        player.sendMessage(footerMessage);
    }
}

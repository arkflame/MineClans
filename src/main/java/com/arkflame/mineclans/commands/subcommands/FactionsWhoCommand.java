package com.arkflame.mineclans.commands.subcommands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.ChatColors;
import com.arkflame.mineclans.utils.NumberUtil;

import com.arkflame.mineclans.api.MineClansAPI;
import com.arkflame.mineclans.enums.Rank;

public class FactionsWhoCommand {
    public static void onCommand(Player player, ModernArguments args) {
        MineClans mineClansInstance = MineClans.getInstance();
        MineClansAPI api = mineClansInstance.getAPI();
        ConfigWrapper messages = mineClansInstance.getMessages();

        String basePath = "factions.who.";
        Faction faction = null;
        String text = args.getText(1);

        if (text != null) {
            faction = api.getFaction(text);

            if (faction == null) {
                FactionPlayer factionPlayer = api.getFactionPlayer(text);

                if (factionPlayer != null) {
                    faction = factionPlayer.getFaction();
                }
            }

            if (faction == null) {
                player.sendMessage(messages.getText(basePath + "invalid_faction"));
                return;
            }
        } else {
            faction = api.getFaction(player);
            if (faction == null) {
                player.sendMessage(messages.getText(basePath + "not_in_faction"));
                return;
            }
        }

        // Faction name, online counts, hq
        String factionName = faction.getName();
        int onlineCount = (int) faction.getMembers().stream().filter(uuid -> api.getFactionPlayer(uuid).isOnline()).count();
        int memberCount = faction.getMembers().size();
        Location hqLocation = faction.getHome();
        String hqCoords = hqLocation != null ? hqLocation.getBlockX() + ", " + hqLocation.getBlockY() + ", " + hqLocation.getBlockZ() : "N/A";
        String inviteStatus = faction.isOpen() ? "Open" : "Closed";

        // Member List
        Map<Rank, String> memberLists = new HashMap<>();
        
        for (UUID member : faction.getMembers()) {
            Rank rank = faction.getRank(member);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member);
            String memberList = memberLists.getOrDefault(rank, "");
            FactionPlayer factionPlayer = mineClansInstance.getAPI().getFactionPlayer(member);
            int kills = factionPlayer.getKills();
            memberLists.put(rank, memberList + offlinePlayer.getName() + "[&f" + kills + "&7]"  + (memberList.isEmpty() ? "" : ", "));
        }
        
        String memberList = "";
        
        for (Entry<Rank, String> entry : memberLists.entrySet()) {
            String format = messages.getText(basePath + entry.getKey().name().toLowerCase());
            if (format != null && !format.isEmpty()) {
                format = format.replace("%members%", entry.getValue());
                memberList = memberList + format + "\n";
            }
        }

        // Balance and Stats
        String formattedBalance = NumberUtil.formatBalance(faction.getBalance());
        String kills = String.valueOf(faction.getKills());
        String power = String.valueOf(faction.getPower());
        String foundedDate = faction.getCreationDate();

        // Custom Texts
        String announcement = faction.getAnnouncement() != null ? faction.getAnnouncement() : "No announcements.";
        String discordLink = faction.getDiscord() != null ? faction.getDiscord() : "No Discord link.";

        // Format the message
        String formattedMessage = messages.getText(basePath + "format")
                .replace("%faction_name%", factionName)
                .replace("%online_count%", String.valueOf(onlineCount))
                .replace("%member_count%", String.valueOf(memberCount))
                .replace("%hq_coords%", hqCoords)
                .replace("%invite_status%", inviteStatus)
                .replace("%members%", ChatColors.color(memberList))
                .replace("%announcement%", messages.getText(basePath + "announcement").replace("%announcement%", announcement))
                .replace("%discord%", messages.getText(basePath + "discord").replace("%link%", discordLink))
                .replace("%balance%", formattedBalance)
                .replace("%kills%", kills)
                .replace("%power%", power)
                .replace("%founded_date%", foundedDate);

        // Send the message to the player
        player.sendMessage(formattedMessage.trim());
    }
}

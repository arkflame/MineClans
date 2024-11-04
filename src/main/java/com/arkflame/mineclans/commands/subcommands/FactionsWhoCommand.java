package com.arkflame.mineclans.commands.subcommands;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.MineClansAPI;
import com.arkflame.mineclans.enums.Rank;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.ChatColors;
import com.arkflame.mineclans.utils.NumberUtil;

public class FactionsWhoCommand {

    private static final String BASE_PATH = "factions.who.";

    public static void onCommand(Player player, ModernArguments args) {
        MineClans mineClansInstance = MineClans.getInstance();
        MineClansAPI api = mineClansInstance.getAPI();
        ConfigWrapper messages = mineClansInstance.getMessages();

        Faction faction = getFaction(api, player, args.getText(1));
        if (faction == null) {
            player.sendMessage(messages.getText(BASE_PATH + (args.getText(1) != null ? "invalid_faction" : "not_in_faction")));
            return;
        }

        String formattedMessage = buildFactionInfoMessage(messages, faction, api);
        player.sendMessage(formattedMessage.trim());
    }

    private static Faction getFaction(MineClansAPI api, Player player, String text) {
        if (text != null) {
            return Optional.ofNullable(api.getFaction(text))
                    .orElseGet(() -> Optional.ofNullable(api.getFactionPlayer(text))
                    .map(FactionPlayer::getFaction)
                    .orElse(null));
        }
        return api.getFaction(player);
    }

    private static String buildFactionInfoMessage(ConfigWrapper messages, Faction faction, MineClansAPI api) {
        String factionName = faction.getName();
        int onlineCount = (int) faction.getMembers().stream()
                .map(api::getFactionPlayer)
                .filter(FactionPlayer::isOnline)
                .count();
        int memberCount = faction.getMembers().size();
        String hqCoords = Optional.ofNullable(faction.getHome().getLocation())
                .map(loc -> loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ())
                .orElse("N/A");
        String inviteStatus = faction.isOpen() ? "Open" : "Closed";
        String memberList = buildMemberList(messages, faction, api);

        String formattedBalance = NumberUtil.formatBalance(faction.getBalance());
        String kills = String.valueOf(faction.getKills());
        String power = String.valueOf(faction.getPower());
        String foundedDate = faction.getCreationDate();
        String announcement = Optional.ofNullable(faction.getAnnouncement()).orElse("No announcements.");
        String discordLink = Optional.ofNullable(faction.getDiscord()).orElse("No Discord link.");

        return messages.getText(BASE_PATH + "format")
                .replace("%faction_name%", factionName)
                .replace("%online_count%", String.valueOf(onlineCount))
                .replace("%member_count%", String.valueOf(memberCount))
                .replace("%hq_coords%", hqCoords)
                .replace("%invite_status%", inviteStatus)
                .replace("%members%", ChatColors.color(memberList))
                .replace("%announcement%", messages.getText(BASE_PATH + "announcement").replace("%announcement%", announcement))
                .replace("%discord%", messages.getText(BASE_PATH + "discord").replace("%link%", discordLink))
                .replace("%balance%", formattedBalance)
                .replace("%kills%", kills)
                .replace("%power%", power)
                .replace("%founded_date%", foundedDate);
    }
    
    private static String buildMemberList(ConfigWrapper messages, Faction faction, MineClansAPI api) {
        Map<Rank, StringBuilder> memberLists = new HashMap<>();
    
        // Iterate through faction members and organize by rank
        for (UUID memberUUID : faction.getMembers()) {
            Rank rank = faction.getRank(memberUUID);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(memberUUID);
            FactionPlayer factionPlayer = api.getFactionPlayer(memberUUID);
    
            String playerInfo = offlinePlayer.getName() + "[&f" + factionPlayer.getKills() + "&7]";
            memberLists.computeIfAbsent(rank, k -> new StringBuilder()).append(playerInfo).append(", ");
        }
    
        // Order the ranks manually: Leaders first, Officers, then Members last (you can add more if needed)
        StringBuilder orderedMemberList = new StringBuilder();
        for (Rank rank : Rank.values()) {
            if (memberLists.containsKey(rank)) {
                String formattedList = memberLists.get(rank).toString();
    
                // Remove trailing comma and space, if present
                if (formattedList.endsWith(", ")) {
                    formattedList = formattedList.substring(0, formattedList.length() - 2);
                }
    
                // Add the formatted member list by rank to the output
                String formattedRankList = formatMemberList(messages, rank, new StringBuilder(formattedList));
                if (formattedRankList != null) {
                    orderedMemberList.append(formattedRankList).append("\n");
                }
            }
        }
    
        return orderedMemberList.toString().trim();  // Trim any trailing newlines
    }
    
    private static String formatMemberList(ConfigWrapper messages, Rank rank, StringBuilder members) {
        String format = messages.getText(BASE_PATH + rank.name().toLowerCase());
    
        return format != null && !format.isEmpty() ? format.replace("%members%", members.toString()) : null;
    }
      
}

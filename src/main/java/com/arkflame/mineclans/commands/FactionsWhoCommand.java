package com.arkflame.mineclans.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsWhoCommand {
    public static void onCommand(Player player, ModernArguments args) {
        Faction faction = null;
        String text = args.getText(1);
        if (text != null) {
            faction = MineClans.getInstance().getAPI().getFaction(text);

            if (faction == null) {
                FactionPlayer factionPlayer = MineClans.getInstance().getAPI().getFactionPlayer(text);

                if (factionPlayer != null) {
                    faction = factionPlayer.getFaction();
                }
            }

            if (faction == null) {
                player.sendMessage(ChatColor.RED + "The faction is not valid.");
                return;
            }
        } else if (faction == null) {
            faction = MineClans.getInstance().getAPI().getFaction(player);
            if (faction == null) {
                player.sendMessage(ChatColor.RED + "You are not in a faction.");
                return;
            }
        }

        StringBuilder message = new StringBuilder(
                ChatColor.GOLD + "Information of " + ChatColor.YELLOW + faction.getName() + ChatColor.GOLD + ": ");
        message.append(ChatColor.RESET).append("\n")
                .append(ChatColor.AQUA).append("ID: ").append(ChatColor.WHITE).append(faction.getId()).append("\n");

        FactionPlayer owner = MineClans.getInstance().getAPI().getFactionPlayer(faction.getOwner());
        String ownerDisplay = owner != null ? owner.getName() : faction.getOwner().toString();
        message.append(ChatColor.AQUA).append("Owner: ").append(ChatColor.WHITE).append(ownerDisplay).append("\n");

        message.append(ChatColor.AQUA).append("Members:").append(ChatColor.RESET);
        for (UUID memberId : faction.getMembers()) {
            FactionPlayer member = MineClans.getInstance().getAPI().getFactionPlayer(memberId);
            if (member != null) {
                message.append("\n").append(ChatColor.YELLOW).append(" - ")
                        .append(ChatColor.WHITE).append(member.getName())
                        .append(ChatColor.GRAY).append(" (").append(member.getRank().name()).append(")");
            } else {
                message.append("\n").append(ChatColor.YELLOW).append(" - ")
                        .append(ChatColor.WHITE).append(memberId.toString());
            }
        }

        player.sendMessage(message.toString());
    }
}

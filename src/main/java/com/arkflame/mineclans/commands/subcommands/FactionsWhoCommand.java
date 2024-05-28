package com.arkflame.mineclans.commands.subcommands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.enums.RelationType;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.utils.NumberUtil;

public class FactionsWhoCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String basePath = "factions.who.";
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
                player.sendMessage(ChatColor.RED + MineClans.getInstance().getMessages().getText(basePath + "invalid_faction"));
                return;
            }
        } else if (faction == null) {
            faction = MineClans.getInstance().getAPI().getFaction(player);
            if (faction == null) {
                player.sendMessage(ChatColor.RED + MineClans.getInstance().getMessages().getText(basePath + "not_in_faction"));
                return;
            }
        }

        StringBuilder message = new StringBuilder(
                ChatColor.GOLD + MineClans.getInstance().getMessages().getText(basePath + "information_title")
                .replace("%faction_name%", faction.getName()) + ": ");
        message.append(ChatColor.RESET).append("\n")
                .append(ChatColor.AQUA).append(MineClans.getInstance().getMessages().getText(basePath + "id"))
                .append(ChatColor.GRAY).append(faction.getId()).append("\n");

        FactionPlayer owner = MineClans.getInstance().getAPI().getFactionPlayer(faction.getOwner());
        String ownerDisplay = owner != null ? owner.getName() : faction.getOwner().toString();
        message.append(ChatColor.AQUA).append(MineClans.getInstance().getMessages().getText(basePath + "owner"))
                .append(ChatColor.WHITE).append(ownerDisplay).append("\n");

        message.append(ChatColor.AQUA).append(MineClans.getInstance().getMessages().getText(basePath + "members_title")
                .replace("%faction_members%", String.valueOf(faction.getMembers().size()))).append(ChatColor.RESET);
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

        // Get the player's faction
        Faction playerFaction = MineClans.getInstance().getAPI().getFaction(player);

        // Check if the player's faction and the displayed faction are different
        if (playerFaction != null && !playerFaction.equals(faction)) {
            RelationType relation = MineClans.getInstance().getAPI().getRelation(player, faction.getName());
            message.append("\n").append(ChatColor.AQUA).append(MineClans.getInstance().getMessages().getText(basePath + "relation"))
                    .append(ChatColor.WHITE).append(relation.name());
        }

        // Display faction balance
        double factionBalance = faction.getBalance();
        String formattedBalance = NumberUtil.formatBalance(factionBalance);
        message.append("\n").append(ChatColor.AQUA).append(MineClans.getInstance().getMessages().getText(basePath + "balance"))
                .append(ChatColor.GREEN).append(formattedBalance);
        message.append("\n").append(ChatColor.AQUA).append(MineClans.getInstance().getMessages().getText(basePath + "kills"))
                .append(ChatColor.RED).append(faction.getKills());
        message.append("\n").append(ChatColor.AQUA).append(MineClans.getInstance().getMessages().getText(basePath + "power"))
                .append(ChatColor.GREEN).append(faction.calculatePower());

        player.sendMessage(message.toString());
    }
}

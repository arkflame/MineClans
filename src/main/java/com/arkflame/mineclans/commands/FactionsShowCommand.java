package com.arkflame.mineclans.commands;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

public class FactionsShowCommand {
    public static void onCommand(Player player) {
        Faction faction = MineClans.getInstance().getAPI().getFaction(player);
        if (faction == null) {
            player.sendMessage("You are not in a faction.");
            return;
        }
        StringBuilder message = new StringBuilder("Information of " + faction.getName() + ": ");
        message.append("\nID: ");
        message.append("\n " + faction.getId());
        FactionPlayer owner = MineClans.getInstance().getAPI().getFactionPlayer(faction.getOwner());
        String ownerDisplay = owner != null ? owner.getName() : faction.getOwner().toString();
        message.append("\nOwner: ");
        message.append("\n " + ownerDisplay);
        message.append("\nMembers:");
        for (UUID memberId : faction.getMembers()) {
            FactionPlayer member = MineClans.getInstance().getAPI().getFactionPlayer(memberId);
            if (member != null) {
                message.append("\n " + member.getName()).append(", ").append(member.getRank().name());
            } else {
                message.append("\n " + memberId.toString());
            }
        }
        player.sendMessage(message.toString());
    }
}

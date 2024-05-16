package com.arkflame.mineclans.commands;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

public class FactionsShowCommand {
    public static void onCommand(Player player) {
        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager().getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();
        if (faction == null) {
            player.sendMessage("You are not in a faction.");
            return;
        }
        StringBuilder message = new StringBuilder("Information of " + faction.getName() + ": ");
        message.append("\nID: " + faction.getId());
        message.append("\nOwner: " + faction.getOwner());
        message.append("\nMembers:");
        for (UUID memberId : faction.getMembers()) {
            FactionPlayer member = MineClans.getInstance().getFactionPlayerManager().getOrLoad(memberId);
            if (member != null) {
                message.append("\n" + memberId.toString()).append(" ").append(member.getRank().name());
            }
        }
        player.sendMessage(message.toString());
    }
}

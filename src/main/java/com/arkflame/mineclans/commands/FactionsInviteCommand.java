package com.arkflame.mineclans.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsInviteCommand {
    public static void onCommand(Player player, ModernArguments args) {
        if (!args.hasArg(1)) {
            player.sendMessage("Usage: /factions invite <player>");
            return;
        }

        String targetPlayerName = args.getText(1);
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            player.sendMessage("Player not found.");
            return;
        }

        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager().getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();

        if (faction == null) {
            player.sendMessage("You are not in a faction.");
            return;
        }

        if (!faction.getOwner().equals(factionPlayer.getPlayerId())) {
            player.sendMessage("You are not the owner.");
            return;
        }

        UUID targetPlayerId = targetPlayer.getUniqueId();
        faction.invitePlayer(targetPlayerId);
        player.sendMessage(targetPlayerName + " has been invited to the faction.");
        targetPlayer.sendMessage("You have been invited to join the faction " + faction.getName() + ".");
    }
}

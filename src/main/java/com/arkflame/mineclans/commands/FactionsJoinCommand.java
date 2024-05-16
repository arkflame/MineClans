package com.arkflame.mineclans.commands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsJoinCommand {
    public static void onCommand(Player player, ModernArguments args) {
        if (!args.hasArg(1)) {
            player.sendMessage("Usage: /factions join <faction>");
            return;
        }

        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager().getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();

        if (faction != null) {
            player.sendMessage("You are already in a faction.");
            return;
        }

        String factionName = args.getText(1);
        faction = MineClans.getInstance().getFactionManager().getFaction(factionName);

        if (faction != null) {
            if (faction.getInvited().contains(player.getUniqueId())) {
                MineClans.getInstance().getFactionPlayerManager().updateFaction(player.getUniqueId(), faction);
                MineClans.getInstance().getFactionManager().addPlayerToFaction(factionName, player.getUniqueId());
                player.sendMessage("You have joined the faction " + faction.getName() + ".");
            } else {
                player.sendMessage("You are not invited to this faction.");
            }
        } else {
            player.sendMessage("No faction with this name.");
        }
    }
}

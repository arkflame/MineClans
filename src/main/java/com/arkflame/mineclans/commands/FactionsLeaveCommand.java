package com.arkflame.mineclans.commands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsLeaveCommand {
    public static void onCommand(Player player, ModernArguments args) {
        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager().getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();

        if (faction == null) {
            player.sendMessage("You are not in a faction.");
            return;
        }

        if (faction.getOwner().equals(factionPlayer.getPlayerId())) {
            player.sendMessage("You are the owner. Disband your faction or transfer ownership instead.");
            return;
        }

        MineClans.getInstance().getFactionManager().removePlayerFromFaction(faction.getName(), factionPlayer.getPlayerId());
        MineClans.getInstance().getFactionPlayerManager().updateFaction(factionPlayer.getPlayerId(), faction);
        player.sendMessage("You left your faction.");
    }
}

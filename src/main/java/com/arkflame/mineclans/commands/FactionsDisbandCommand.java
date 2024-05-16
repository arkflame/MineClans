package com.arkflame.mineclans.commands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsDisbandCommand {
    public static void onCommand(Player player, ModernArguments args) {
        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager().getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();
        if (faction == null) {
            player.sendMessage("You have no faction.");
            return;
        }
        if (!faction.getOwner().equals(factionPlayer.getPlayerId())) {
            player.sendMessage("You are not the owner.");
            return;
        }
        MineClans.getInstance().getFactionManager().disbandFaction(faction.getName());
        MineClans.getInstance().getFactionPlayerManager().updateFaction(factionPlayer.getPlayerId(), null);
        player.sendMessage("Disbanded faction.");
    }
}

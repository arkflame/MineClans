package com.arkflame.mineclans.commands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.enums.Rank;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsCreateCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String factionName = args.getText(1);
        if (factionName == null) {
            player.sendMessage(MineClans.getInstance().getMsg().getText("factions.create.usage"));
            return;
        }
        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager().getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();
        if (faction != null) {
            player.sendMessage("You already have a faction. Disband it or leave first.");
            return;
        }

        faction = MineClans.getInstance().getFactionManager().createFaction(player, factionName);
        MineClans.getInstance().getFactionPlayerManager().updateFaction(factionPlayer.getPlayerId(), faction);
        MineClans.getInstance().getFactionPlayerManager().updateRank(factionPlayer.getPlayerId(), Rank.LEADER);
        player.sendMessage("Created faction.");
    }
}

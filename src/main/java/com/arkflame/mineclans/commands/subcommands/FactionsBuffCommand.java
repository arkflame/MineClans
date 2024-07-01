package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsBuffCommand {
    public static void onCommand(Player player, ModernArguments args) {
        Faction faction = MineClans.getInstance().getAPI().getFaction(player);
        if (faction != null) {
            MineClans.getInstance().getBuffManager().openBuffMenu(player, faction);
        } else {
            player.sendMessage("You have no faction");
        }
    }
}

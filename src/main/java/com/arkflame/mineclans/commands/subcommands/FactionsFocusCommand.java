package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.api.results.FocusResult;
import com.arkflame.mineclans.api.results.FocusResult.FocusResultType;
import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsFocusCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String factionName = args.getText(1);
        FocusResult focusResult = MineClans.getInstance().getAPI().focus(player, factionName);
        FocusResultType type = focusResult.getType();
        
        switch (type) {
            case SUCCESS:
                player.sendMessage(ChatColor.GREEN + "Successfully focused on faction: " + factionName);
                break;
            case NOT_IN_FACTION:
                player.sendMessage(ChatColor.RED + "You are not in a faction.");
                break;
            case FACTION_NOT_FOUND:
                player.sendMessage(ChatColor.RED + "Faction '" + factionName + "' not found. Cleared focus.");
                break;
            case NO_PERMISSION:
                player.sendMessage(ChatColor.RED + "You need to be RECRUIT to focus on factions.");
                break;
            case SAME_FACTION:
                player.sendMessage(ChatColor.RED + "You are trying to focus your own faction.");
                break;
            default:
                player.sendMessage(ChatColor.RED + "An unexpected error occurred.");
                break;
        }
    }
}

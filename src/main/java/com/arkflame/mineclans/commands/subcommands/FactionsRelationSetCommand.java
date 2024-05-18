package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.SetRelationResult;
import com.arkflame.mineclans.api.results.SetRelationResult.SetRelationResultState;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsRelationSetCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String relationType = args.getText(0);
        String otherFactionName = args.getText(1);

        SetRelationResult result = MineClans.getInstance().getAPI().setRelation(player, otherFactionName, relationType);
        SetRelationResultState state = result.getState();

        switch (state) {
            case INVALID_RELATION_TYPE:
                player.sendMessage(ChatColor.RED + "Invalid relation type: " + relationType);
                break;
            case NO_FACTION:
                player.sendMessage(ChatColor.RED + "You are not in a faction.");
                break;
            case SAME_FACTION:
                player.sendMessage(ChatColor.RED + "You cannot set relation towards your own faction.");
                break;
            case OTHER_FACTION_NOT_FOUND:
                player.sendMessage(ChatColor.RED + "Faction " + otherFactionName + " not found.");
                break;
            case SUCCESS:
                player.sendMessage(ChatColor.GREEN + "Relation with faction " + otherFactionName + " set to "
                        + relationType + ".");
                break;
            default:
                player.sendMessage(ChatColor.RED + "An unexpected error occurred.");
                break;
        }
    }
}

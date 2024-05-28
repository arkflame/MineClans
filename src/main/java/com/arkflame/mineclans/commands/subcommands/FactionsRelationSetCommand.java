package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.SetRelationResult;
import com.arkflame.mineclans.api.results.SetRelationResult.SetRelationResultState;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsRelationSetCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String relationType = args.getText(0);
        String otherFactionName = args.getText(1);
        String basePath = "factions.relation_set.";

        SetRelationResult result = MineClans.getInstance().getAPI().setRelation(player, otherFactionName, relationType);
        SetRelationResultState state = result.getState();

        switch (state) {
            case INVALID_RELATION_TYPE:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "invalid_relation_type")
                           .replace("%relation_type%", relationType));
                break;
            case NO_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_faction"));
                break;
            case SAME_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "same_faction"));
                break;
            case OTHER_FACTION_NOT_FOUND:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "other_faction_not_found")
                           .replace("%other_faction%", otherFactionName));
                break;
            case SUCCESS:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success")
                           .replace("%other_faction%", otherFactionName)
                           .replace("%relation_type%", relationType));
                break;
            default:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "unexpected_error"));
                break;
        }
    }
}

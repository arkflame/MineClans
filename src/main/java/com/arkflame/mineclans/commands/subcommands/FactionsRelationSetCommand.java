package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.SetRelationResult;
import com.arkflame.mineclans.api.results.SetRelationResult.SetRelationResultState;
import com.arkflame.mineclans.enums.RelationType;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;

public class FactionsRelationSetCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String relationType = args.getText(0);
        String otherFactionName = args.getText(1);
        String basePath = "factions.relation_set.";

        SetRelationResult result = MineClans.getInstance().getAPI().setRelation(player, otherFactionName, relationType);
        SetRelationResultState state = result.getState();
        ConfigWrapper messages = MineClans.getInstance().getMessages();

        switch (state) {
            case INVALID_RELATION_TYPE:
                player.sendMessage(messages.getText(basePath + "invalid_relation_type")
                        .replace("%relation_type%", relationType));
                break;
            case NO_FACTION:
                player.sendMessage(messages.getText(basePath + "no_faction"));
                break;
            case SAME_FACTION:
                player.sendMessage(messages.getText(basePath + "same_faction"));
                break;
            case OTHER_FACTION_NOT_FOUND:
                player.sendMessage(messages.getText(basePath + "other_faction_not_found")
                        .replace("%other_faction%", otherFactionName));
                break;
            case ALREADY_RELATION:
                player.sendMessage(messages.getText(basePath + "already_relation")
                        .replace("%other_faction%", otherFactionName).replace("%relation_type%", relationType));
                break;
            case SUCCESS:
                result.getFaction().sendMessage(messages.getText(basePath + "success")
                        .replace("%faction%", result.getFaction().getName())
                        .replace("%other_faction%", otherFactionName)
                        .replace("%relation_type%", relationType));
                result.getOtherFaction().sendMessage(messages.getText(basePath + "success_other")
                        .replace("%faction%", otherFactionName)
                        .replace("%other_faction%", result.getFaction().getName())
                        .replace("%relation_type%", relationType));

                RelationType relationType1 = result.getRelation();
                RelationType relationType2 = result.getOtherRelation();

                if (relationType1 == relationType2 && relationType1 == RelationType.ALLY) {
                    result.getFaction().sendMessage(messages.getText(basePath + "now_allies"));
                    result.getOtherFaction().sendMessage(messages.getText(basePath + "now_allies"));
                } else {
                    result.getFaction().sendMessage(messages.getText(basePath + "now_enemies"));
                    result.getOtherFaction().sendMessage(messages.getText(basePath + "now_enemies"));
                }
                break;
            default:
                player.sendMessage(messages.getText(basePath + "unexpected_error"));
                break;
        }
    }
}

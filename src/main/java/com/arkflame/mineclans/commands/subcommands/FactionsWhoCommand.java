package com.arkflame.mineclans.commands.subcommands;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.enums.RelationType;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.utils.NumberUtil;
import com.arkflame.mineclans.api.MineClansAPI;

public class FactionsWhoCommand {
    public static void onCommand(Player player, ModernArguments args) {
        MineClans mineClansInstance = MineClans.getInstance();
        MineClansAPI api = mineClansInstance.getAPI();
        ConfigWrapper messages = mineClansInstance.getMessages();

        String basePath = "factions.who.";
        Faction faction = null;
        String text = args.getText(1);

        if (text != null) {
            faction = api.getFaction(text);

            if (faction == null) {
                FactionPlayer factionPlayer = api.getFactionPlayer(text);

                if (factionPlayer != null) {
                    faction = factionPlayer.getFaction();
                }
            }

            if (faction == null) {
                player.sendMessage(messages.getText(basePath + "invalid_faction"));
                return;
            }
        } else {
            faction = api.getFaction(player);
            if (faction == null) {
                player.sendMessage(messages.getText(basePath + "not_in_faction"));
                return;
            }
        }

        String factionName = faction.getName();
        String factionId = faction.getId().toString();
        int factionMembersSize = faction.getMembers().size();
        UUID factionOwner = faction.getOwner();
        FactionPlayer owner = api.getFactionPlayer(factionOwner);
        String ownerDisplay = (owner != null) ? owner.getName() : factionOwner.toString();
        String membersTitleText = messages.getText(basePath + "members_title").replace("%faction_members%",
                String.valueOf(factionMembersSize));
        String informationTitleText = messages.getText(basePath + "information_title").replace("%faction_name%",
                factionName);
        String factionIdText = messages.getText(basePath + "id").replace("%id%", factionId);
        String ownerText = messages.getText(basePath + "owner").replace("%owner%", ownerDisplay);
        String memberEntry = messages.getText(basePath + "member_entry");
        String memberEntryNoData = messages.getText(basePath + "member_entry_no_data");
        String balanceText = messages.getText(basePath + "balance");
        String killsText = messages.getText(basePath + "kills");
        String powerText = messages.getText(basePath + "power");
        String relationText = messages.getText(basePath + "relation");

        StringBuilder message = new StringBuilder(informationTitleText);
        message.append("\n")
                .append(factionIdText).append("\n")
                .append(ownerText).append("\n")
                .append(membersTitleText);

        for (UUID memberId : faction.getMembers()) {
            FactionPlayer member = api.getFactionPlayer(memberId);
            if (member != null) {
                message.append("\n")
                        .append(memberEntry.replace("%faction_member%", member.getName())
                                .replace("%faction_member_rank%", member.getRank().name()));
            } else {
                message.append("\n")
                        .append(memberEntryNoData.replace("%faction_member_id%", memberId.toString()));
            }
        }

        Faction playerFaction = api.getFaction(player);

        if (playerFaction != null && !playerFaction.equals(faction)) {
            RelationType relation = api.getRelation(player, factionName);
            message.append("\n")
                    .append(relationText.replace("%relation%", relation.name()));
        }

        double factionBalance = faction.getBalance();
        String formattedBalance = NumberUtil.formatBalance(factionBalance);
        message.append("\n")
                .append(balanceText.replace("%balance%", formattedBalance)).append("\n")
                .append(killsText.replace("%kills%", String.valueOf(faction.getKills()))).append("\n")
                .append(powerText.replace("%power%", String.valueOf(faction.getPower())));

        player.sendMessage(message.toString());
    }
}

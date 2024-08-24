package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class FactionsInvitesCommand {
    public static void onCommand(Player player, ModernArguments args) {
        ConfigWrapper messages = MineClans.getInstance().getMessages();
        FactionPlayer factionPlayer = MineClans.getInstance().getAPI().getFactionPlayer(player);

        // Fetch faction invites received by the player
        Collection<UUID> invitesReceived = MineClans.getInstance().getMySQLProvider().getInvitedDAO()
                .getInvitingFactions(player.getUniqueId());

        // Format invites received
        String receivedInvitesMessage;
        if (invitesReceived.isEmpty()) {
            receivedInvitesMessage = messages.getText("factions.invites.received.none");
        } else {
            int inviteCount = invitesReceived.size();
            String invites = invitesReceived.stream()
                    .map(invite -> {
                        Faction inviterFaction = MineClans.getInstance().getAPI().getFaction(invite);
                        if (inviterFaction == null) {
                            return invite.toString().substring(0, 4) + "...";
                        }
                        return inviterFaction.getName();
                    })
                    .collect(Collectors.joining(", "));
            receivedInvitesMessage = messages.getText("factions.invites.received.list")
                    .replace("%invites%", invites)
                    .replace("%inviteCount%", String.valueOf(inviteCount));
        }

        // Check if the player belongs to a faction
        if (factionPlayer != null && factionPlayer.getFaction() != null) {
            Faction faction = factionPlayer.getFaction();

            // Fetch invites sent by the faction
            Collection<UUID> invitesSent = faction.getInvited();

            // Format invites sent
            String sentInvitesMessage;
            if (invitesSent.isEmpty()) {
                sentInvitesMessage = messages.getText("factions.invites.sent.none");
            } else {
                int inviteCount = invitesSent.size();
                String invites = invitesSent.stream()
                        .map(invite -> {
                            FactionPlayer invitedPlayer = MineClans.getInstance().getAPI().getFactionPlayer(invite);
                            if (invitedPlayer == null) {
                                return invite.toString().substring(0, 4) + "...";
                            }
                            return factionPlayer.getName();
                        })
                        .collect(Collectors.joining(", "));
                sentInvitesMessage = messages.getText("factions.invites.sent.list")
                        .replace("%faction%", faction.getName())
                        .replace("%invites%", invites)
                        .replace("%inviteCount%", String.valueOf(inviteCount));
            }

            // Send both messages (invites received and invites sent by the faction)
            player.sendMessage(sentInvitesMessage);
        }

        // Send message about received invites
        player.sendMessage(receivedInvitesMessage);
    }
}

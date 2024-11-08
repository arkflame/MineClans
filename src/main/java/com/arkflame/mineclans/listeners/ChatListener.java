package com.arkflame.mineclans.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.FactionChatResult.FactionChatState;
import com.arkflame.mineclans.api.results.ToggleChatResult.ToggleChatState;
import com.arkflame.mineclans.commands.subcommands.FactionsDepositCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsWithdrawCommand;
import com.arkflame.mineclans.menus.EnteringType;
import com.arkflame.mineclans.models.FactionPlayer;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        FactionPlayer factionPlayer = MineClans.getInstance().getAPI().getFactionPlayer(player.getUniqueId());
        String message = event.getMessage();

        if (factionPlayer != null) {
            // Check if the player has chat enabled for faction communication
            if (factionPlayer.getChatMode() == ToggleChatState.FACTION) {
                if (MineClans.getInstance().getAPI().sendFactionMessage(player, message)
                        .getState() == FactionChatState.SUCCESS) {
                    event.setCancelled(true); // Cancel the default chat event
                    return;
                }
            } else if (factionPlayer.getChatMode() == ToggleChatState.ALLIANCE) {
                if (MineClans.getInstance().getAPI().sendAllianceMessage(player, message).getState() == FactionChatState.SUCCESS) {
                    event.setCancelled(true); // Cancel the default chat event
                    return;
                }
            }

            // Check for an active entering type for deposit/withdraw within 30 seconds
            EnteringType enteringType = factionPlayer.getEnteringTypeIfValid();
            if (enteringType != null) {
                event.setCancelled(true); // Cancel chat input as we are handling it here

                try {
                    double amount = Double.parseDouble(message);

                    switch (enteringType) {
                        case WITHDRAWAL:
                            FactionsWithdrawCommand.onCommand(player, amount);
                            break;
                        case DEPOSIT:
                            FactionsDepositCommand.onCommand(player, amount);
                            break;
                    }

                } catch (NumberFormatException e) {
                    String basePath = enteringType == EnteringType.WITHDRAWAL ? "factions.withdraw." : "factions.deposit.";
                    player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "invalid_amount").replace("%amount%", message));
                }
            }
        }
    }
}

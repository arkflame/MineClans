package com.arkflame.mineclans.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.DepositResult;
import com.arkflame.mineclans.api.results.FactionChatResult.FactionChatState;
import com.arkflame.mineclans.api.results.ToggleChatResult.ToggleChatState;
import com.arkflame.mineclans.api.results.WithdrawResult;
import com.arkflame.mineclans.menus.EnteringType;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.utils.NumberUtil;

import net.milkbowl.vault.economy.Economy;

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
                    Economy economy = MineClans.getInstance().getVaultEconomy();

                    switch (enteringType) {
                        case WITHDRAWAL:
                            if (economy.has(player, amount)) {
                                WithdrawResult withdrawResult = MineClans.getInstance().getAPI().withdraw(player, amount);
                                if (withdrawResult.getResultType() == WithdrawResult.WithdrawResultType.SUCCESS) {
                                    player.sendMessage(ChatColor.GREEN + "You have withdrawn " + NumberUtil.formatBalance(amount) + ".");
                                } else {
                                    player.sendMessage(ChatColor.RED + "Failed to withdraw. " + withdrawResult.getResultType().toString());
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Insufficient funds.");
                            }
                            break;

                        case DEPOSIT:
                            DepositResult depositResult = MineClans.getInstance().getAPI().deposit(player, amount);
                            if (depositResult.getResultType() == DepositResult.DepositResultType.SUCCESS) {
                                player.sendMessage(ChatColor.GREEN + "You have deposited " + NumberUtil.formatBalance(amount) + ".");
                            } else {
                                player.sendMessage(ChatColor.RED + "Failed to deposit. " + depositResult.getResultType().toString());
                            }
                            break;

                        default:
                            player.sendMessage(ChatColor.RED + "Invalid operation.");
                            break;
                    }

                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid number entered. Please enter a valid amount.");
                }
            }
        }
    }
}

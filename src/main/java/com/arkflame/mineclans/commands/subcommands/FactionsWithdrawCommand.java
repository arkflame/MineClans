package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.WithdrawResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.utils.NumberUtil;

public class FactionsWithdrawCommand {
    public static void onCommand(Player player, ModernArguments args) {
        double amount = args.getDouble(1);
        WithdrawResult withdrawResult = MineClans.getInstance().getAPI().withdraw(player, amount);

        switch (withdrawResult.getResultType()) {
            case SUCCESS:
                player.sendMessage(
                        ChatColor.GREEN + "Withdrawal of $" + NumberUtil.formatBalance(amount) + " successful!");
                break;
            case NOT_IN_FACTION:
                player.sendMessage(ChatColor.RED + "You are not in a faction.");
                break;
            case NO_PERMISSION:
                player.sendMessage(ChatColor.RED + "You have to be COLEADER to withdraw funds.");
                break;
            case INSUFFICIENT_FUNDS:
                player.sendMessage(
                        ChatColor.RED + "Your faction does not have enough funds to withdraw $" + NumberUtil.formatBalance(amount) + ".");
                break;
            case ERROR:
                player.sendMessage(
                        ChatColor.RED + "An error occurred while processing your withdrawal. Please try again later.");
                break;
            case INVALID_AMOUNT:
                player.sendMessage(ChatColor.RED + "Invalid amount entered: $" + NumberUtil.formatBalance(amount));
                break;
            case NO_ECONOMY:
                player.sendMessage(ChatColor.RED + "Vault is not installed. No economy system present.");
                break;
            default:
                break;
        }
    }
}
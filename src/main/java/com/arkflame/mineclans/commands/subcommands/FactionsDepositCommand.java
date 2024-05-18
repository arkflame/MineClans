package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.DepositResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.utils.NumberUtil;

public class FactionsDepositCommand {
    public static void onCommand(Player player, ModernArguments args) {
        double amount = args.getDouble(1);
        DepositResult depositResult = MineClans.getInstance().getAPI().deposit(player, amount);

        switch (depositResult.getResultType()) {
            case SUCCESS:
                player.sendMessage(
                        ChatColor.GREEN + "Deposit of $" + NumberUtil.formatBalance(amount) + " successful!");
                break;
            case NOT_IN_FACTION:
                player.sendMessage(ChatColor.RED + "You are not in a faction.");
                break;
            case NO_PERMISSION:
                player.sendMessage(ChatColor.RED + "You do not have permission to deposit funds.");
                break;
            case NO_MONEY:
                player.sendMessage(ChatColor.RED + "You have insufficent funds.");
                break;
            case ERROR:
                player.sendMessage(
                        ChatColor.RED + "An error occurred while processing your deposit. Please try again later.");
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

package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.DepositResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsDepositCommand {
    public static void onCommand(Player player, ModernArguments args) {
        int amount = args.getNumber(1);
        DepositResult depositResult = MineClans.getInstance().getAPI().deposit(player, amount);

        switch (depositResult.getResultType()) {
            case SUCCESS:
                player.sendMessage(
                        ChatColor.GREEN + "Deposit of " + depositResult.getAmountDeposited() + " successful!");
                break;
            case NOT_IN_FACTION:
                player.sendMessage(ChatColor.RED + "You are not in a faction.");
                break;
            case NO_PERMISSION:
                player.sendMessage(ChatColor.RED + "You do not have permission to deposit funds.");
                break;
            case ERROR:
                player.sendMessage(
                        ChatColor.RED + "An error occurred while processing your deposit. Please try again later.");
                break;
            case INVALID_AMOUNT:
                player.sendMessage(ChatColor.RED + "Invalid amount entered: " + amount);
                break;
            case NO_ECONOMY:
                player.sendMessage(ChatColor.RED + "Vault is not installed. No economy system present.");
                break;
            default:
                break;
        }
    }
}

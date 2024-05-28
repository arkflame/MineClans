package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.WithdrawResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.utils.NumberUtil;

public class FactionsWithdrawCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String basePath = "factions.withdraw.";
        double amount = args.getDouble(1);
        WithdrawResult withdrawResult = MineClans.getInstance().getAPI().withdraw(player, amount);

        switch (withdrawResult.getResultType()) {
            case SUCCESS:
                player.sendMessage(ChatColor.GREEN + MineClans.getInstance().getMessages().getText(basePath + "success")
                        .replace("%amount%", NumberUtil.formatBalance(amount)));
                break;
            case NOT_IN_FACTION:
                player.sendMessage(ChatColor.RED + MineClans.getInstance().getMessages().getText(basePath + "not_in_faction"));
                break;
            case NO_PERMISSION:
                player.sendMessage(ChatColor.RED + MineClans.getInstance().getMessages().getText(basePath + "no_permission"));
                break;
            case INSUFFICIENT_FUNDS:
                player.sendMessage(ChatColor.RED + MineClans.getInstance().getMessages().getText(basePath + "insufficient_funds")
                        .replace("%amount%", NumberUtil.formatBalance(amount)));
                break;
            case ERROR:
                player.sendMessage(ChatColor.RED + MineClans.getInstance().getMessages().getText(basePath + "error"));
                break;
            case INVALID_AMOUNT:
                player.sendMessage(ChatColor.RED + MineClans.getInstance().getMessages().getText(basePath + "invalid_amount")
                        .replace("%amount%", NumberUtil.formatBalance(amount)));
                break;
            case NO_ECONOMY:
                player.sendMessage(ChatColor.RED + MineClans.getInstance().getMessages().getText(basePath + "no_economy"));
                break;
            default:
                break;
        }
    }
}

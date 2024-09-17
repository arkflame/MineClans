package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.DepositResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.utils.NumberUtil;

public class FactionsDepositCommand {
    public static void onCommand(Player player, ModernArguments args) {
        double amount = args.getDouble(1);
        MineClans mineClans = MineClans.getInstance();
        String basePath = "factions.deposit.";

        DepositResult depositResult = mineClans.getAPI().deposit(player, amount);

        switch (depositResult.getResultType()) {
            case SUCCESS:
                player.sendMessage(mineClans.getMessages().getText(basePath + "success").replace("%amount%", NumberUtil.formatBalance(amount)));
                break;
            case NOT_IN_FACTION:
                player.sendMessage(mineClans.getMessages().getText(basePath + "not_in_faction"));
                break;
            case NO_PERMISSION:
                player.sendMessage(mineClans.getMessages().getText(basePath + "no_permission"));
                break;
            case NO_MONEY:
                player.sendMessage(mineClans.getMessages().getText(basePath + "no_money"));
                break;
            case ERROR:
                player.sendMessage(mineClans.getMessages().getText(basePath + "error"));
                break;
            case INVALID_AMOUNT:
                player.sendMessage(mineClans.getMessages().getText(basePath + "invalid_amount").replace("%amount%", NumberUtil.formatBalance(amount)));
                break;
            case NO_ECONOMY:
                player.sendMessage(mineClans.getMessages().getText(basePath + "no_economy"));
                break;
            default:
                break;
        }
    }
}

package com.arkflame.mineclans.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.commands.subcommands.FactionsChatCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsChestCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsCreateCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsDemoteCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsDepositCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsDisbandCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsDisplaynameCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsFocusCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsFriendlyFireCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsHomeCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsInviteCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsJoinCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsKickCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsLeaveCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsListCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsPromoteCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsRelationSetCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsRenameCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsSetHomeCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsTellLocationCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsTransferCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsWhoCommand;
import com.arkflame.mineclans.commands.subcommands.FactionsWithdrawCommand;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.commands.ModernCommand;

public class FactionsCommand extends ModernCommand {
    public FactionsCommand() {
        super("factions", "f", "clans", "clan", "guilds", "guild");
    }

    @Override
    public void onCommand(CommandSender sender, ModernArguments args) {
        if (!args.hasArg(0)) {
            sender.sendMessage(MineClans.getInstance().getMsg().getText("factions.usage"));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Not from console.");
            return;
        }

        MineClans.runAsync(() -> {
            Player player = (Player) sender;
            String subcommand = args.getText(0);
            switch (subcommand.toLowerCase()) {
                case "create":
                    FactionsCreateCommand.onCommand(player, args);
                    break;
                case "disband":
                    FactionsDisbandCommand.onCommand(player, args);
                    break;
                case "who":
                case "show":
                    FactionsWhoCommand.onCommand(player, args);
                    break;
                case "invite":
                    FactionsInviteCommand.onCommand(player, args);
                    break;
                case "join":
                    FactionsJoinCommand.onCommand(player, args);
                    break;
                case "leave":
                    FactionsLeaveCommand.onCommand(player, args);
                    break;
                case "transfer":
                    FactionsTransferCommand.onCommand(player, args);
                    break;
                case "rename":
                    FactionsRenameCommand.onCommand(player, args);
                    break;
                case "displayname":
                    FactionsDisplaynameCommand.onCommand(player, args);
                    break;
                case "list":
                    FactionsListCommand.onCommand(player);
                    break;
                case "chat":
                case "c":
                    FactionsChatCommand.onCommand(player);
                    break;
                case "tl":
                case "telllocation":
                    FactionsTellLocationCommand.onCommand(player, args);
                    break;
                case "ff":
                case "friendlyfire":
                    FactionsFriendlyFireCommand.onCommand(player, args);
                    break;
                case "home":
                    FactionsHomeCommand.onCommand(player);
                    break;
                case "sethome":
                    FactionsSetHomeCommand.onCommand(player);
                    break;
                case "chest":
                    FactionsChestCommand.onCommand(player);
                    break;
                case "enemy":
                case "neutral":
                case "ally":
                    FactionsRelationSetCommand.onCommand(player, args);
                    break;
                case "promote":
                    FactionsPromoteCommand.onCommand(player, args);
                    break;
                case "demote":
                    FactionsDemoteCommand.onCommand(player, args);
                    break;
                case "kick":
                    FactionsKickCommand.onCommand(player, args);
                    break;
                case "focus":
                    FactionsFocusCommand.onCommand(player, args);
                    break;
                case "deposit":
                case "d":
                    FactionsDepositCommand.onCommand(player, args);
                    break;
                case "withdraw":
                case "w":
                    FactionsWithdrawCommand.onCommand(player, args);
                    break;
                default:
                    sender.sendMessage(MineClans.getInstance().getMsg().getText("factions.usage"));
            }
        });
    }
}

package com.arkflame.mineclans.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.commands.ModernCommand;

public class FactionsCommand extends ModernCommand {
    public FactionsCommand() {
        super("factions");
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

        MineClans.getInstance().runAsync(() -> {
            Player player = (Player) sender;
            String subcommand = args.getText(0);
            switch (subcommand.toLowerCase()) {
                case "create":
                    createFaction(player, args);
                    break;
                case "disband":
                    disbandFaction(player, args);
                    break;
                default:
                    sender.sendMessage(MineClans.getInstance().getMsg().getText("factions.usage"));
            }
        });
    }

    private void createFaction(Player player, ModernArguments args) {
        String factionName = args.getText(1);
        if (factionName == null) {
            player.sendMessage(MineClans.getInstance().getMsg().getText("factions.create.usage"));
            return;
        }
        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager().getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();
        if (faction != null) {
            player.sendMessage("You already have a faction. Disband it or leave first.");
            return;
        }

        faction = MineClans.getInstance().getFactionManager().createFaction(player, factionName);
        MineClans.getInstance().getFactionPlayerManager().updateFaction(factionPlayer.getPlayerId(), faction);
        player.sendMessage("Created faction.");
    }

    private void disbandFaction(Player player, ModernArguments args) {
        String factionName = args.getText(1);
        if (factionName == null) {
            player.sendMessage(MineClans.getInstance().getMsg().getText("factions.disband.usage"));
            return;
        }

        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager().getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();
        if (faction == null) {
            player.sendMessage("You have no faction.");
            return;
        }
        if (faction.getOwner() != factionPlayer.getPlayerId()) {
            player.sendMessage("You are not the owner.");
            return;
        }
        MineClans.getInstance().getFactionManager().disbandFaction(factionName);
        MineClans.getInstance().getFactionPlayerManager().updateFaction(factionPlayer.getPlayerId(), null);
        player.sendMessage("Disbanded faction.");
    }
}

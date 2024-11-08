package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.menus.PersonalBankMenu;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;

public class FactionsBankCommand {
    public static void onCommand(Player player, ModernArguments args) {
        // Get the messages config wrapper
        ConfigWrapper messages = MineClans.getInstance().getMessages();

        // Get the faction player associated with the player executing the command
        FactionPlayer factionPlayer = MineClans.getInstance().getAPI().getFactionPlayer(player.getUniqueId());

        // Check if the player is in a faction
        if (factionPlayer != null && factionPlayer.getFaction() != null) {
            MineClans.runSync(() -> {
                // Open the faction bank menu for the player
                new PersonalBankMenu(player).openInventory(player);
            });
        } else {
            // Send a message if the player is not part of any faction
            player.sendMessage(messages.getText("factions.bank.no_faction"));
        }
    }
}

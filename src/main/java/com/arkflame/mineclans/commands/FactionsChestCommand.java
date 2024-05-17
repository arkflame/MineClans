package com.arkflame.mineclans.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;

public class FactionsChestCommand {
    public static void onCommand(Player player) {
        Faction faction = MineClans.getInstance().getAPI().getFaction(player);
        if (faction == null) {
            player.sendMessage("You are not in a faction.");
            return;
        }

        Inventory chestInventory = faction.getChest();

        player.openInventory(chestInventory);
    }
}

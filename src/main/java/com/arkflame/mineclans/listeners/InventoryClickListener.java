package com.arkflame.mineclans.listeners;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;

public class InventoryClickListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();

        if (humanEntity instanceof Player) {
            InventoryView view = event.getView();
            Inventory inventory = view.getTopInventory();

            if (inventory != null) {
                InventoryHolder holder = inventory.getHolder();

                if (holder instanceof Faction) {
                    Faction faction = (Faction) holder;

                    MineClans.getInstance().getInventorySaveTask().save(faction);
                }
            }
        }
    }
}

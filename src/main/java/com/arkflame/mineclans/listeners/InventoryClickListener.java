package com.arkflame.mineclans.listeners;

import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.ClickType;

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

                    event.setCancelled(true);

                    MineClans.runAsync(() -> {
                        AtomicBoolean updateChestContent = new AtomicBoolean(false);
                        try {
                            // Send status to other servers to avoid concurrent modification
                            updateChestContent.set(MineClans.getInstance().getAPI().startChestUpdate(faction));
                        } finally {
                            if (updateChestContent.get()) {
                                MineClans.runSync(() -> {
                                    AtomicBoolean doUpdate = new AtomicBoolean(false);
                                    try {
                                        // Update inventory based on player input
                                        doUpdate.set(updateInventory(event));
                                    } finally {
                                        MineClans.runAsync(() -> {
                                            // Save data and notify completion of chest update
                                            MineClans.getInstance().getAPI().endChestUpdate(faction, doUpdate.get());
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    public boolean updateInventory(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();

        // Return false if outside of inventory or bottom inventory is clicked
        if (clickedInventory == null || event.getSlotType() == SlotType.OUTSIDE) {
            return false;
        }

        ItemStack cursorItem = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();
        ClickType clickType = event.getClick();
        boolean modified = false;
        boolean isTopInventoryClicked = clickedInventory == topInventory;

        // Handle double-click separately to cancel it for the top (faction) inventory
        if (clickType == ClickType.DOUBLE_CLICK) {
            if (isTopInventoryClicked) {
                event.setCancelled(true); // Cancels double-click action
                return false;
            }
        }

        // Basic actions based on click type
        switch (clickType) {
            case LEFT:
                if (currentItem == null) {
                    event.setCurrentItem(cursorItem);
                    event.setCursor(null);
                    modified = isTopInventoryClicked;
                } else if (cursorItem != null && cursorItem.isSimilar(currentItem)) {
                    int maxStackSize = currentItem.getMaxStackSize();
                    int amountToAdd = Math.min(cursorItem.getAmount(), maxStackSize - currentItem.getAmount());

                    currentItem.setAmount(currentItem.getAmount() + amountToAdd);
                    cursorItem.setAmount(cursorItem.getAmount() - amountToAdd);

                    if (cursorItem.getAmount() <= 0) {
                        event.setCursor(null);
                    }
                    modified = isTopInventoryClicked;
                } else {
                    event.setCurrentItem(cursorItem);
                    event.setCursor(currentItem);
                    modified = isTopInventoryClicked;
                }
                break;

            case RIGHT:
                if (currentItem == null) {
                    ItemStack singleItem = cursorItem.clone();
                    singleItem.setAmount(1);
                    event.setCurrentItem(singleItem);

                    cursorItem.setAmount(cursorItem.getAmount() - 1);
                    event.setCursor(cursorItem.getAmount() > 0 ? cursorItem : null);
                    modified = isTopInventoryClicked;
                } else if (cursorItem != null && cursorItem.isSimilar(currentItem)) {
                    if (currentItem.getAmount() < currentItem.getMaxStackSize()) {
                        currentItem.setAmount(currentItem.getAmount() + 1);
                        cursorItem.setAmount(cursorItem.getAmount() - 1);

                        event.setCursor(cursorItem.getAmount() > 0 ? cursorItem : null);
                        modified = isTopInventoryClicked;
                    }
                }
                break;

            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                // Move only the current item stack (single stack only, not similar items)
                Inventory bottomInventory = view.getBottomInventory();
                ItemStack clickedItem = event.getCurrentItem();
                int clickedSlot = event.getSlot();
                if (isTopInventoryClicked) {
                    modified = moveItemToInventory(topInventory, bottomInventory, clickedItem, clickedSlot);
                } else {
                    modified = moveItemToInventory(bottomInventory, topInventory, clickedItem, clickedSlot);
                }
                break;

            default:
                break;
        }
        return modified;
    }

    private boolean moveItemToInventory(Inventory fromInventory, Inventory toInventory, ItemStack clickedItem,
            int clickedSlot) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR)
            return false;

        for (int i = 0; i < toInventory.getSize(); i++) {
            ItemStack targetItem = toInventory.getItem(i);

            if (targetItem == null) {
                // Move the entire clicked item stack to an empty slot
                toInventory.setItem(i, clickedItem.clone()); // Clone to avoid modifying the original reference
                fromInventory.setItem(clickedSlot, null); // Clear the item in the original inventory slot
                return true;
            } else if (targetItem.isSimilar(clickedItem) && targetItem.getAmount() < targetItem.getMaxStackSize()) {
                // Stack the item into an existing stack if possible
                int amountToAdd = Math.min(clickedItem.getAmount(),
                        targetItem.getMaxStackSize() - targetItem.getAmount());
                targetItem.setAmount(targetItem.getAmount() + amountToAdd);
                clickedItem.setAmount(clickedItem.getAmount() - amountToAdd);

                if (clickedItem.getAmount() <= 0) {
                    fromInventory.setItem(clickedSlot, null); // Clear the item in the original inventory slot
                    return true;
                }
            }
        }
        return false;
    }

    // Cancel all inventory actions except for normal clicks
    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof Faction) {
            event.setCancelled(true); // Cancel drag events for Faction inventory
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof Faction) {
            Faction faction = (Faction) inventory.getHolder();

            // Handle any required updates or finalization when inventory is closed
            // MineClans.runAsync(() ->
            // MineClans.getInstance().getAPI().finalizeChestUpdate(faction));
        }
    }
}

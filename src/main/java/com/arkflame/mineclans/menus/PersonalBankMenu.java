package com.arkflame.mineclans.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.menus.Menu;
import com.arkflame.mineclans.modernlib.menus.items.MenuItem;
import net.milkbowl.vault.economy.Economy;

public class PersonalBankMenu extends Menu {
    private final Player player;

    public PersonalBankMenu(Player player) {
        super("Personal Bank", 4); // 4 rows for the menu
        this.player = player;

        // Setting the Deposit and Withdraw buttons
        setItem(11, new MenuItem(Material.CHEST, 1, (short) 0, ChatColor.GREEN + "Deposit",
                ChatColor.GRAY + "Click to deposit money") {
            @Override
            public void onClick(Player player, int slot) {
                new DepositWithdrawMenu(player, EnteringType.DEPOSIT).openInventory(player);
            }
        });

        setItem(15, new MenuItem(Material.HOPPER, 1, (short) 0, ChatColor.RED + "Withdraw",
                ChatColor.GRAY + "Click to withdraw money") {
            @Override
            public void onClick(Player player, int slot) {
                new DepositWithdrawMenu(player, EnteringType.WITHDRAWAL).openInventory(player);
            }
        });

        // Set the close button
        setItem(31, new MenuItem(Material.BARRIER, 1, (short) 0, ChatColor.RED + "Close",
                ChatColor.GRAY + "Click to close the menu") {
            @Override
            public void onClick(Player player, int slot) {
                player.closeInventory();
            }
        });

        // Set background stained glass
        setBackground(Material.getMaterial("STAINED_GLASS_PANE"), (short) 7, " ");
    }
}

class DepositWithdrawMenu extends Menu {
    private final Player player;
    private final EnteringType enteringType;

    public DepositWithdrawMenu(Player player, EnteringType enteringType) {
        super((enteringType == EnteringType.DEPOSIT ? "Deposit" : "Withdraw"), 4); // 4 rows menu
        this.player = player;
        this.enteringType = enteringType;

        // Set deposit/withdraw specific items
        if (enteringType == EnteringType.DEPOSIT) {
            setDepositItems();
        } else {
            setWithdrawItems();
        }

        // Set the close button
        setItem(31, new MenuItem(Material.BARRIER, 1, (short) 0, ChatColor.RED + "Close",
                ChatColor.GRAY + "Click to close the menu") {
            @Override
            public void onClick(Player player, int slot) {
                player.closeInventory();
            }
        });

        // Set background stained glass
        setBackground(Material.getMaterial("STAINED_GLASS_PANE"), (short) 7, " ");
    }

    private void setDepositItems() {
        // Add percentage buttons
        setItem(11, new PercentageItem("Deposit 25%", Material.CHEST, 25));
        setItem(13, new PercentageItem("Deposit 50%", Material.CHEST, 50));
        setItem(15, new PercentageItem("Deposit 100%", Material.CHEST, 100));

        // Add specific amount button
        setItem(29, new MenuItem(Material.PAPER, 1, (short) 0, ChatColor.YELLOW + "Specific Amount",
                ChatColor.GRAY + "Enter specific amount to deposit") {
            @Override
            public void onClick(Player player, int slot) {
                handleSpecificAmount();
            }
        });
    }

    private void setWithdrawItems() {
        setItem(13, new MenuItem(Material.HOPPER, ChatColor.RED + "Withdraw"));

        // Add percentage buttons
        setItem(10, new PercentageItem("Withdraw 25%", Material.HOPPER, 25));
        setItem(12, new PercentageItem("Withdraw 50%", Material.HOPPER, 50));
        setItem(14, new PercentageItem("Withdraw 100%", Material.HOPPER, 100));

        // Add specific amount button
        setItem(29, new MenuItem(Material.PAPER, 1, (short) 0, ChatColor.YELLOW + "Specific Amount",
                ChatColor.GRAY + "Enter specific amount to withdraw") {
            @Override
            public void onClick(Player player, int slot) {
                handleSpecificAmount();
            }
        });
    }

    private void handleSpecificAmount() {
        FactionPlayer factionPlayer = MineClans.getInstance().getAPI().getFactionPlayer(player.getUniqueId());

        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Please enter the amount in the chat:");

        Bukkit.getScheduler().runTaskLater(MineClans.getInstance(), () -> {
            player.sendMessage(ChatColor.RED + "Time expired. Please try again.");
        }, 20 * 30); // 30 seconds timeout

        factionPlayer.setEnteringAmount(enteringType); // Set deposit or withdraw based on user choice
    }

    // Class for percentage items
    private class PercentageItem extends MenuItem {
        private final int percentage;

        public PercentageItem(String name, Material material, int percentage) {
            super(material, 1, (short) 0, ChatColor.GREEN + name,
                    ChatColor.GRAY + "Click to deposit/withdraw " + percentage + "% of your balance");
            this.percentage = percentage;
        }

        @Override
        public void onClick(Player player, int slot) {
            Faction faction = MineClans.getInstance().getAPI().getFaction(player);
            if (faction == null) {
                player.sendMessage("You have no faction");
            } else if (enteringType == EnteringType.DEPOSIT) {
                Economy economy = MineClans.getInstance().getVaultEconomy();
                double balance = economy.getBalance(player);
                double amount = balance * (percentage / 100.0);
                MineClans.getInstance().getAPI().deposit(player, amount); // Deposit into faction's bank
                player.sendMessage(
                        ChatColor.GREEN + "You have deposited " + amount + " (" + percentage + "% of your balance).");
            } else {
                double balance = faction.getBalance();
                double amount = balance * (percentage / 100.0);
                MineClans.getInstance().getAPI().withdraw(player, amount); // Withdraw from faction's bank
                player.sendMessage(
                        ChatColor.RED + "You have withdrawn " + amount + " (" + percentage + "% of your balance).");
            }

            player.closeInventory();
        }
    }
}

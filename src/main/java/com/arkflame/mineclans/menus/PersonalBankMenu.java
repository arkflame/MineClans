package com.arkflame.mineclans.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.modernlib.menus.Menu;
import com.arkflame.mineclans.modernlib.menus.items.MenuItem;
import com.arkflame.mineclans.modernlib.utils.Sounds;
import com.arkflame.mineclans.utils.NumberUtil;

import net.milkbowl.vault.economy.Economy;

public class PersonalBankMenu extends Menu {
    public PersonalBankMenu(Player player) {
        super(MineClans.getInstance().getConfig().getString("menus.personal_bank.title", "Personal Bank"), 4);
        Menu menu = this;
        // Setting the Deposit and Withdraw buttons
        setItem(11, new MenuItem(Material.CHEST, 1, (short) 0, getConfigString("menus.personal_bank.deposit.name"),
                getConfigString("menus.personal_bank.deposit.lore")) {
            @Override
            public void onClick(Player player, int slot) {
                new DepositWithdrawMenu(player, EnteringType.DEPOSIT, menu).openInventory(player);
                Sounds.play(player, 1.0F, 1.0F, "CLICK", "UI_BUTTON_CLICK");
            }
        });

        setItem(15, new MenuItem(Material.HOPPER, 1, (short) 0, getConfigString("menus.personal_bank.withdraw.name"),
                getConfigString("menus.personal_bank.withdraw.lore")) {
            @Override
            public void onClick(Player player, int slot) {
                new DepositWithdrawMenu(player, EnteringType.WITHDRAWAL, menu).openInventory(player);
                Sounds.play(player, 1.0F, 1.0F, "CLICK", "UI_BUTTON_CLICK");
            }
        });

        // Set the close button
        setItem(31, new MenuItem(Material.BARRIER, 1, (short) 0, getConfigString("menus.personal_bank.close.name"),
                getConfigString("menus.personal_bank.close.lore")) {
            @Override
            public void onClick(Player player, int slot) {
                player.closeInventory();
                Sounds.play(player, 1.0F, 1.0F, "CLICK", "UI_BUTTON_CLICK");
            }
        });

        // Set background stained glass
        setBackground(Material.getMaterial("STAINED_GLASS_PANE"), (short) 7, " ");
    }

    private String getConfigString(String path) {
        return MineClans.getInstance().getMessages().getString(path);
    }
}

class DepositWithdrawMenu extends Menu {
    private final Player player;
    private final EnteringType enteringType;

    public DepositWithdrawMenu(Player player, EnteringType enteringType, Menu oldMenu) {
        super(MineClans.getInstance().getConfig().getString(
                enteringType == EnteringType.DEPOSIT ? "menus.deposit_withdraw.deposit.title"
                        : "menus.deposit_withdraw.withdraw.title",
                enteringType == EnteringType.DEPOSIT ? "Deposit" : "Withdraw"), 4); // 4 rows menu
        this.player = player;
        this.enteringType = enteringType;

        // Set deposit/withdraw specific items
        if (enteringType == EnteringType.DEPOSIT) {
            setDepositItems();
        } else {
            setWithdrawItems();
        }

        // Set the close button
        setItem(31, new MenuItem(Material.ARROW, 1, (short) 0, getConfigString("menus.personal_bank.close.name"),
                getConfigString("menus.personal_bank.close.lore")) {
            @Override
            public void onClick(Player player, int slot) {
                oldMenu.openInventory(player);
                Sounds.play(player, 1.0F, 1.0F, "CLICK", "UI_BUTTON_CLICK");
            }
        });

        // Set background stained glass
        setBackground(Material.getMaterial("STAINED_GLASS_PANE"), (short) 7, " ");
    }

    private void setDepositItems() {
        setItem(11, new PercentageItem(enteringType, "menus.deposit_withdraw.deposit.percentages.25"));
        setItem(13, new PercentageItem(enteringType, "menus.deposit_withdraw.deposit.percentages.50"));
        setItem(15, new PercentageItem(enteringType, "menus.deposit_withdraw.deposit.percentages.100"));

        // Add specific amount button
        setItem(29,
                new MenuItem(Material.PAPER, 1, (short) 0,
                        getConfigString("menus.deposit_withdraw.deposit.specific.name"),
                        getConfigString("menus.deposit_withdraw.deposit.specific.lore")) {
                    @Override
                    public void onClick(Player player, int slot) {
                        handleSpecificAmount();
                        Sounds.play(player, 1.0F, 1.0F, "CLICK", "UI_BUTTON_CLICK");
                    }
                });
    }

    private void setWithdrawItems() {
        setItem(11, new PercentageItem(enteringType, "menus.deposit_withdraw.withdraw.percentages.25"));
        setItem(13, new PercentageItem(enteringType, "menus.deposit_withdraw.withdraw.percentages.50"));
        setItem(15, new PercentageItem(enteringType, "menus.deposit_withdraw.withdraw.percentages.100"));

        // Add specific amount button
        setItem(29,
                new MenuItem(Material.PAPER, 1, (short) 0,
                        getConfigString("menus.deposit_withdraw.withdraw.specific.name"),
                        getConfigString("menus.deposit_withdraw.withdraw.specific.lore")) {
                    @Override
                    public void onClick(Player player, int slot) {
                        handleSpecificAmount();
                        Sounds.play(player, 1.0F, 1.0F, "CLICK", "UI_BUTTON_CLICK");
                    }
                });
    }

    private String getConfigString(String path) {
        return MineClans.getInstance().getMessages().getString(path);
    }

    private void handleSpecificAmount() {
        FactionPlayer factionPlayer = MineClans.getInstance().getAPI().getFactionPlayer(player.getUniqueId());

        player.closeInventory();
        player.sendMessage(MineClans.getInstance().getMessages().getText("factions.bank.type_amount"));

        Bukkit.getScheduler().runTaskLater(MineClans.getInstance(), () -> {
            player.sendMessage(MineClans.getInstance().getMessages().getText("factions.bank.expired"));
        }, 20 * 30); // 30 seconds timeout

        factionPlayer.setEnteringAmount(enteringType); // Set deposit or withdraw based on user choice
    }

    // Class for percentage items
    private class PercentageItem extends MenuItem {
        private final EnteringType enteringType;
        private final int percentage;

        public PercentageItem(EnteringType enteringType, String path) {
            super(enteringType == EnteringType.DEPOSIT ? Material.CHEST : Material.HOPPER, 1, (short) 0,
                    getConfigString(path + ".name"),
                    getConfigString(path + ".lore"));
            this.enteringType = enteringType;
            this.percentage = Integer.parseInt(path.substring(path.lastIndexOf('.') + 1));
        }

        @Override
        public void onClick(Player player, int slot) {
            Faction faction = MineClans.getInstance().getAPI().getFaction(player);
            if (faction == null) {
                player.sendMessage(MineClans.getInstance().getMessages().getText("factions.bank.no_faction"));
            } else if (enteringType == EnteringType.DEPOSIT) {
                Economy economy = MineClans.getInstance().getVaultEconomy();
                double balance = economy.getBalance(player);
                double amount = balance * (percentage / 100.0);
                MineClans.getInstance().getAPI().deposit(player, amount); // Deposit into faction's bank
                player.sendMessage(MineClans.getInstance().getMessages().getText("factions.deposit.success_menu")
                        .replace("%amount%", NumberUtil.formatBalance(amount))
                        .replace("%percentage%", String.valueOf(percentage)));
            } else {
                double balance = faction.getBalance();
                double amount = balance * (percentage / 100.0);
                MineClans.getInstance().getAPI().withdraw(player, amount); // Withdraw from faction's bank
                player.sendMessage(MineClans.getInstance().getMessages().getText("factions.withdraw.success_menu")
                        .replace("%amount%", NumberUtil.formatBalance(amount))
                        .replace("%percentage%", String.valueOf(percentage)));
            }
            Sounds.play(player, 1.0F, 1.0F, "CLICK", "UI_BUTTON_CLICK");
        }
    }
}

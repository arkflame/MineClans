package com.arkflame.mineclans.menus.items;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.buff.ActiveBuff;
import com.arkflame.mineclans.buff.Buff;
import com.arkflame.mineclans.buff.BuffEffect;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.menus.items.MenuItem;
import com.arkflame.mineclans.modernlib.utils.Materials;

import net.milkbowl.vault.economy.Economy;

public class BuffItem extends MenuItem {
    private Buff buff;

    public BuffItem(Buff buff) {
        super(Materials.get(buff.getMaterial(), "PAPER"), 1, (short) 0,
                buff.getDisplayName(), buff.getLore());
        this.buff = buff;
    }

    @Override
    public void onClick(Player player, int slot) {
        if (buff == null) {
            return;
        }
        double price = 0;
        if (MineClans.getInstance().isVaultHooked()) {
            Economy economy = MineClans.getInstance().getVaultEconomy();
            price = buff.getPrice();
            if (price > 0) {
                if (economy.has(player, price)) {
                    economy.withdrawPlayer(player, price);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have enough money."));
                    return;
                }
            }
        }
        Faction faction = MineClans.getInstance().getAPI().getFaction(player);
        String msg = "&rYou activated &e" + buff.getDisplayName();
        if (price > 0) {
            msg = msg + "&r for $" + price;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        for (BuffEffect effect : buff.getEffects()) {
            ActiveBuff activeBuff = faction.addBuff(new ActiveBuff(effect.getType(), effect.getAmplifier(),
                    effect.getDuration() * 1000, faction));
            MineClans.runSync(() -> {
                activeBuff.giveEffectToFaction();
            });
        }
        player.closeInventory();
    }
}

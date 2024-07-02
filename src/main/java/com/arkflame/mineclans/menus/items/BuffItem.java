package com.arkflame.mineclans.menus.items;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.buff.ActiveBuff;
import com.arkflame.mineclans.buff.Buff;
import com.arkflame.mineclans.buff.BuffEffect;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.menus.items.MenuItem;
import com.arkflame.mineclans.modernlib.utils.ChatColors;
import com.arkflame.mineclans.modernlib.utils.Materials;
import com.arkflame.mineclans.modernlib.utils.Titles;
import com.arkflame.mineclans.utils.MelodyUtil;
import com.arkflame.mineclans.utils.MelodyUtil.Melody;

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
        MineClans.runAsync(() -> {
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
                        player.sendMessage(ChatColors
                                .color(MineClans.getInstance().getMessages().getString("factions.buffs.no_money")));
                        return;
                    }
                }
            }
            Faction faction = MineClans.getInstance().getAPI().getFaction(player);
            String msg = MineClans.getInstance().getMessages().getString("factions.buffs.activated");
            if (price > 0) {
                msg = msg
                        .replace("%activated_price%",
                                MineClans.getInstance().getMessages().getString("factions.buffs.activated_price"))
                        .replace("%price%", String.valueOf(price));
            }
            msg = msg.replace("%buff%", buff.getDisplayName());
            player.sendMessage(ChatColors.color(msg));
            for (BuffEffect effect : buff.getEffects()) {
                ActiveBuff activeBuff = faction.addBuff(new ActiveBuff(buff, effect.getType(), effect.getAmplifier(),
                        effect.getDuration() * 1000, faction));
                MineClans.getInstance().getBuffManager().getActiveBuffs().add(activeBuff);
                MineClans.runSync(() -> {
                    activeBuff.giveEffectToFaction();
                });
            }
            String title = MineClans.getInstance().getMessages().getString("factions.buffs.activated_other.title");
            String subtitle = MineClans.getInstance().getMessages()
                    .getString("factions.buffs.activated_other.subtitle")
                    .replace("%player%", player.getName())
                    .replace("%buff%", buff.getDisplayName());
            String notificationMsg = MineClans.getInstance().getMessages()
                    .getString("factions.buffs.activated_other.msg")
                    .replace("%player%", player.getName())
                    .replace("%buff%", buff.getDisplayName());
            for (UUID uuid : faction.getOnlineMembers()) {
                Player otherPlayer = Bukkit.getPlayer(uuid);
                if (otherPlayer != null) {
                    Titles.sendTitle(player, ChatColors.color(title), ChatColors.color(subtitle), 10, 40, 10);
                    player.sendMessage(ChatColors.color(notificationMsg));
                    MelodyUtil.playMelody(MineClans.getInstance(), player, Melody.BUFF_ACTIVE);
                }
            }
            player.closeInventory();
        });
    }
}

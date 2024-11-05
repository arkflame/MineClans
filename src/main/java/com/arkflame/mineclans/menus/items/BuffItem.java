package com.arkflame.mineclans.menus.items;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.buff.Buff;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.menus.items.MenuItem;
import com.arkflame.mineclans.modernlib.utils.ChatColors;
import com.arkflame.mineclans.modernlib.utils.Materials;

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
                price = buff.getPrice();
                if (!buff.withdraw(player)) {
                    return;
                }
            }
            ConfigWrapper messages = MineClans.getInstance().getMessages();
            String msg = messages.getString("factions.buffs.activated");
            if (price > 0) {
                msg = msg
                        .replace("%activated_price%",
                                messages.getString("factions.buffs.activated_price"))
                        .replace("%price%", String.valueOf(price));
            } else {
                msg = msg.replace("%activated_price%", "");
            }
            msg = msg.replace("%buff%", buff.getDisplayName());
            player.sendMessage(ChatColors.color(msg));

            Faction faction = MineClans.getInstance().getAPI().getFaction(player);
            buff.giveEffects(faction);
            buff.notify(player.getName(), faction);
            MineClans.getInstance().getRedisProvider().activateBuff(faction.getId(), player.getName(), buff.getName());
            MineClans.runSync(() -> {
                player.closeInventory();
            });
        });
    }
}

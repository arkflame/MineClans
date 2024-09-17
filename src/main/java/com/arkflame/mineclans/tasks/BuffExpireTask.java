package com.arkflame.mineclans.tasks;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.buff.ActiveBuff;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.tasks.ModernTask;
import com.arkflame.mineclans.modernlib.utils.ChatColors;
import com.arkflame.mineclans.modernlib.utils.Titles;

public class BuffExpireTask extends ModernTask {
    public BuffExpireTask() {
        super(MineClans.getInstance(), 20L, true);
    }

    @Override
    public void run() {
        Iterator<ActiveBuff> iterator = MineClans.getInstance().getBuffManager().getActiveBuffs().iterator();
        while (iterator.hasNext()) {
            ActiveBuff activeBuff = iterator.next();
            if (!activeBuff.isActive()) {
                iterator.remove();
                Faction faction = activeBuff.getFaction();
                if (faction != null) {
                    faction.removeBuff(activeBuff);
                    String title = MineClans.getInstance().getMessages().getString("factions.buffs.expired.title")
                            .replace("%buff%", activeBuff.getDisplayName());
                    String subtitle = MineClans.getInstance().getMessages()
                            .getString("factions.buffs.expired.subtitle")
                            .replace("%buff%", activeBuff.getDisplayName());
                    String msg = ChatColors
                            .color(MineClans.getInstance().getMessages().getString("factions.buffs.expired.msg")
                                    .replace("%buff%", activeBuff.getDisplayName()));
                    for (UUID uuid : faction.getOnlineMembers()) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            Titles.sendTitle(player,
                                    title,
                                    subtitle, 0,
                                    0, 0);
                            player.sendMessage(msg);
                        }
                    }
                }
            }
        }
    }

}

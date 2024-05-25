package com.arkflame.mineclans.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.events.ClanEvent;
import com.arkflame.mineclans.events.ClanEventManager;
import com.arkflame.mineclans.models.FactionPlayer;

import org.bukkit.entity.Player;
import org.bukkit.entity.Monster;

public class ClanEventListener implements Listener {
    private final ClanEventManager clanEventManager;

    public ClanEventListener(ClanEventManager clanEventManager) {
        this.clanEventManager = clanEventManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if (killer != null) {
            FactionPlayer factionPlayer = MineClans.getInstance().getAPI().getFactionPlayer(player);
            ClanEvent currentEvent = clanEventManager.getCurrentEvent();
            if (currentEvent != null) {
                currentEvent.onFactionKill(factionPlayer);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        FactionPlayer factionPlayer = MineClans.getInstance().getAPI().getFactionPlayer(player);
        ClanEvent currentEvent = clanEventManager.getCurrentEvent();
        if (currentEvent != null) {
            currentEvent.onBlockBreak(event.getBlock(), factionPlayer);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster) {
            Monster monster = (Monster) event.getEntity();
            Player killer = monster.getKiller();
            if (killer != null) {
                FactionPlayer factionPlayer = MineClans.getInstance().getAPI().getFactionPlayer(killer);
                ClanEvent currentEvent = clanEventManager.getCurrentEvent();
                if (currentEvent != null) {
                    currentEvent.onMonsterKill(factionPlayer);
                }
            }
        }
    }
}

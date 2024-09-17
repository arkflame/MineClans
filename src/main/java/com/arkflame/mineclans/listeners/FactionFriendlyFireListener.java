package com.arkflame.mineclans.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

public class FactionFriendlyFireListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        FactionPlayer attackerFP = MineClans.getInstance().getAPI().getFactionPlayer(attacker.getUniqueId());
        FactionPlayer victimFP = MineClans.getInstance().getAPI().getFactionPlayer(victim.getUniqueId());

        if (attackerFP == null || victimFP == null || attackerFP.getFaction() == null || victimFP.getFaction() == null) {
            return;
        }

        Faction attackerFaction = attackerFP.getFaction();
        Faction victimFaction = victimFP.getFaction();

        // Check if both players are in the same faction and friendly fire is disabled
        if (attackerFaction.equals(victimFaction) && !attackerFaction.isFriendlyFire()) {
            event.setCancelled(true);
        }
    }
}

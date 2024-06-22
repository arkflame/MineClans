package com.arkflame.mineclans.buff;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.arkflame.mineclans.models.Faction;

public class ActiveBuff {
    private final Buff buff;
    private final Faction faction;
    private final long endTimeStamp;

    /**
     * Constructs an ActiveBuff instance.
     *
     * @param buff     the Buff type
     * @param duration the duration in milliseconds for which the buff is active
     */
    public ActiveBuff(Buff buff, Faction faction) {
        this.buff = buff;
        this.faction = faction;
        this.endTimeStamp = System.currentTimeMillis() + buff.getDuration();
    }

    /**
     * Gets the Buff type.
     *
     * @return the Buff
     */
    public Buff getBuff() {
        return buff;
    }

    /**
     * Gets the Faction.
     *
     * @return the Faction
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Gets the end timestamp.
     *
     * @return the end timestamp in milliseconds
     */
    public long getEndTimeStamp() {
        return endTimeStamp;
    }

    /**
     * Checks if the buff is still active.
     *
     * @return true if the buff is active, false otherwise
     */
    public boolean isActive() {
        return System.currentTimeMillis() < endTimeStamp;
    }

    /**
     * Gets the remaining time in milliseconds.
     *
     * @return the remaining time in milliseconds
     */
    public long getRemainingMillis() {
        return endTimeStamp - System.currentTimeMillis();
    }

    /**
     * Gets the remaining time in ticks.
     *
     * @return the remaining time in ticks
     */
    public long getRemainingTicks() {
        return (getRemainingMillis() / 1000) * 20;
    }

    /**
     * Gives the buff effect to an individual player.
     *
     * @param player the Player to give the effect to
     */
    public void giveEffectToPlayer(Player player) {
        for (PotionEffectType effectType : buff.getEffects()) {
            int newDuration = (int) getRemainingTicks();
            int newAmplifier = 1; // This can be adjusted based on your buff logic

            PotionEffect currentEffect = player.getActivePotionEffects().stream()
                    .filter(effect -> effect.getType().equals(effectType))
                    .findFirst()
                    .orElse(null);

            if (currentEffect != null) {
                int currentDuration = currentEffect.getDuration();
                int currentAmplifier = currentEffect.getAmplifier();

                // Replace the current effect if the new one is stronger or lasts longer
                if (newAmplifier > currentAmplifier || newDuration > currentDuration) {
                    player.removePotionEffect(effectType);
                    player.addPotionEffect(new PotionEffect(effectType, newDuration, newAmplifier));
                }
            } else {
                // If the player doesn't have the effect, just add it
                player.addPotionEffect(new PotionEffect(effectType, newDuration, newAmplifier));
            }
        }
    }

    /**
     * Gives the buff effect to all members of the faction.
     */
    public void giveEffectToFaction() {
        for (UUID uuid : faction.getOnlineMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            giveEffectToPlayer(player);
        }
    }

    /**
     * Removes the buff effect from an individual player.
     *
     * @param player the Player to remove the effect from
     */
    public void removeEffectFromPlayer(Player player) {
        for (PotionEffectType effectType : buff.getEffects()) {
            PotionEffect currentEffect = player.getActivePotionEffects().stream()
                    .filter(effect -> effect.getType().equals(effectType))
                    .findFirst()
                    .orElse(null);

            if (currentEffect != null) {
                int currentDuration = currentEffect.getDuration();
                int currentAmplifier = currentEffect.getAmplifier();
                int buffAmplifier = 1; // This can be adjusted based on your buff logic
                int buffDuration = (int) getRemainingTicks();

                // Only remove the effect if the current effect has less or equal duration and equal amplifier
                if (currentAmplifier == buffAmplifier && currentDuration <= buffDuration) {
                    player.removePotionEffect(effectType);
                }
            }
        }
    }
}

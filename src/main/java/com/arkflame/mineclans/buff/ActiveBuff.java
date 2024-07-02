package com.arkflame.mineclans.buff;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.arkflame.mineclans.models.Faction;

public class ActiveBuff {
    private final Buff buff;
    private final PotionEffectType potionEffect;
    private final int amplifier;
    private final int duration;
    private final Faction faction;
    private final long endTimeStamp;

    /**
     * Constructs an ActiveBuff instance.
     *
     * @param potionEffect the Buff type
     * @param faction the faction that has the buff
     */
    public ActiveBuff(Buff buff, PotionEffectType potionEffect, int amplifier, int duration, Faction faction) {
        this.buff = buff;
        this.potionEffect = potionEffect;
        this.amplifier = amplifier;
        this.duration = duration;
        this.faction = faction;
        this.endTimeStamp = System.currentTimeMillis() + duration;
    }

    /**
     * Gets the Buff type.
     *
     * @return the Buff
     */
    public PotionEffectType getPotionEffect() {
        return potionEffect;
    }

    public int getAmplifier() {
        return amplifier;
    }

    /**
     * Gets the total duration.
     *
     * @return the duration
     */
    public int getDuration() {
        return duration;
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
            int newDuration = (int) getRemainingTicks();
            int newAmplifier = getAmplifier(); // This can be adjusted based on your buff logic

            PotionEffect currentEffect = player.getActivePotionEffects().stream()
                    .filter(effect -> effect.getType().equals(potionEffect))
                    .findFirst()
                    .orElse(null);

            if (currentEffect != null) {
                int currentDuration = currentEffect.getDuration();
                int currentAmplifier = currentEffect.getAmplifier();

                // Replace the current effect if the new one is stronger or lasts longer
                if (newAmplifier > currentAmplifier || newDuration > currentDuration) {
                    player.removePotionEffect(potionEffect);
                    player.addPotionEffect(new PotionEffect(potionEffect, newDuration, newAmplifier));
                }
            } else {
                // If the player doesn't have the effect, just add it
                player.addPotionEffect(new PotionEffect(potionEffect, newDuration, newAmplifier));
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
            PotionEffect currentEffect = player.getActivePotionEffects().stream()
                    .filter(effect -> effect.getType().equals(potionEffect))
                    .findFirst()
                    .orElse(null);

            if (currentEffect != null) {
                int currentDuration = currentEffect.getDuration();
                int currentAmplifier = currentEffect.getAmplifier();
                int buffAmplifier = 1; // This can be adjusted based on your buff logic
                int buffDuration = (int) getRemainingTicks();

                // Only remove the effect if the current effect has less or equal duration and equal amplifier
                if (currentAmplifier == buffAmplifier && currentDuration <= buffDuration) {
                    player.removePotionEffect(potionEffect);
                }
            }
    }

    public String getDisplayName() {
        return buff.getDisplayName();
    }
}

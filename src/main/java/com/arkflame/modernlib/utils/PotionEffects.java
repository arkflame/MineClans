package com.arkflame.modernlib.utils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffects {
    /**
     * Applies the specified potion effects to the player.
     *
     * @param player    The player to apply the effects to.
     * @param amplifier The amplifier for the effects.
     * @param duration  The duration of the effects in ticks.
     * @param effects   The names of the potion effects to apply.
     */
    public static void add(Player player, int amplifier, int duration, String... effects) {
        for (String effect : effects) {
            PotionEffectType effectType = PotionEffectType.getByName(effect);
            if (effectType != null) {
                PotionEffect potionEffect = new PotionEffect(effectType, duration, amplifier);
                player.addPotionEffect(potionEffect);
            }
        }
    }

    /**
     * Removes the specified potion effects from the player.
     *
     * @param player  The player to remove the effects from.
     * @param effects The names of the potion effects to remove.
     */
    public static void remove(Player player, String... effects) {
        for (String effect : effects) {
            PotionEffectType effectType = PotionEffectType.getByName(effect);
            if (effectType != null) {
                player.removePotionEffect(effectType);
            }
        }
    }
}

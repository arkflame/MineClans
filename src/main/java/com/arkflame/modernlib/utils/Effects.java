package com.arkflame.modernlib.utils;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Effects {
    private Effects() {
    }

    /**
     * Attempts to get the first valid Particle or Effect from a list of names.
     * If no valid Particle or Effect is found, returns null.
     *
     * @param names One or more String names of the Particle or Effect to retrieve.
     * @return The first found Particle or Effect, or null if none are found.
     */
    public static Object get(String... names) {
        for (String name : names) {
            // Try retrieving an Effect
            try {
                return Effect.valueOf(name.toUpperCase());
            } catch (Exception ex) {
                // Effect with specified name not found, continue searching
            }
        }
        // Return null if no valid Effect was found
        return null;
    }

    /**
     * Plays an effect at a specified location for a player, if the effect is not null.
     *
     * @param player    The player to play the effect for.
     * @param effects   One or more String names of the Effect to play.
     */
    public static void play(Player player, String... effects) {
        Object effectObject = get(effects);
        if (effectObject instanceof Effect) {
            Effect effect = (Effect) effectObject;
            player.playEffect(player.getLocation(), effect, 0);
        }
    }

    /**
     * Plays an effect at a specified location for a location, if the effect is not null.
     *
     * @param loc    The location to play the effect
     * @param effects   One or more String names of the Effect to play.
     */
    public static void play(Location loc, String... effects) {
        Object effectObject = get(effects);
        if (effectObject instanceof Effect) {
            Effect effect = (Effect) effectObject;
            loc.getWorld().playEffect(loc, effect, 0);
        }
    }
}

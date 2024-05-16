package com.arkflame.modernlib.utils;

import org.bukkit.Material;

import java.util.List;

public class Materials {
    private Materials() {}

    /**
     * Attempts to get the first valid Material from a list of names.
     * If no valid Material is found, returns Material.AIR.
     *
     * @param names One or more String names of the Material to retrieve.
     * @return The first found Material, or Material.AIR if none are found.
     */
    public static Material get(String... names) {
        for (String name : names) {
            Material material = Material.getMaterial(name.toUpperCase());
            if (material != null) {
                return material;
            }
        }
        // Return Material.AIR as a default if no valid Material was found
        return Material.AIR;
    }

    public static Material get(List<String> names) {
        return get(names.toArray(new String[0]));
    }
}

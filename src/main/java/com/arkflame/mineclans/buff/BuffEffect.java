package com.arkflame.mineclans.buff;

import org.bukkit.potion.PotionEffectType;

public class BuffEffect {
    private PotionEffectType type;
    private int amplifier;
    private int duration;

    public BuffEffect(PotionEffectType type, int amplifier, int duration) {
        this.type = type;
        this.amplifier = amplifier;
        this.duration = duration;
    }

    public PotionEffectType getType() {
        return type;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public int getDuration() {
        return duration;
    }
}

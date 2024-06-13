package com.arkflame.mineclans.buff;

import java.util.Arrays;
import java.util.List;
import org.bukkit.potion.PotionEffectType;

public enum Buff {
    SPEED(PotionEffectType.SPEED),
    MINING_SPEED(PotionEffectType.FAST_DIGGING),
    HEALTH_BOOST(PotionEffectType.HEALTH_BOOST),
    REGENERATION(PotionEffectType.REGENERATION);

    private final List<PotionEffectType> effects;

    Buff(PotionEffectType... effects) {
        this.effects = Arrays.asList(effects);
    }

    public List<PotionEffectType> getEffects() {
        return effects;
    }

    public boolean isEnabled() {
        return true;
    }

    public long getDuration() {
        return 10000L;
    }
}
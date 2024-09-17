package com.arkflame.mineclans.buff;

import java.util.List;

public class Buff {
    private String displayName;
    private List<String> lore;
    private List<BuffEffect> effects;
    private double price;
    private int slot;
    private String material;

    public Buff(String displayName, List<String> lore, List<BuffEffect> effects, double price, int slot, String material) {
        this.displayName = displayName;
        this.lore = lore;
        this.effects = effects;
        this.price = price;
        this.slot = slot;
        this.material = material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<BuffEffect> getEffects() {
        return effects;
    }

    public double getPrice() {
        return price;
    }

    public int getSlot() {
        return slot;
    }

    public String getMaterial() {
        return material;
    }
}

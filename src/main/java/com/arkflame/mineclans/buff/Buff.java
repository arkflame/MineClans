package com.arkflame.mineclans.buff;

import java.util.List;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.ChatColors;
import com.arkflame.mineclans.utils.MelodyUtil.Melody;

import net.milkbowl.vault.economy.Economy;

public class Buff {
    private String name;
    private String displayName;
    private List<String> lore;
    private List<BuffEffect> effects;
    private double price;
    private int slot;
    private String material;

    public Buff(String name, String displayName, List<String> lore, List<BuffEffect> effects, double price, int slot,
            String material) {
        this.name = name;
        this.displayName = displayName;
        this.lore = lore;
        this.effects = effects;
        this.price = price;
        this.slot = slot;
        this.material = material;
    }

    public String getName() {
        return name;
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

    public boolean withdraw(Player player) {
        Economy economy = MineClans.getInstance().getVaultEconomy();
        double price = getPrice();
        if (price > 0) {
            if (economy.has(player, price)) {
                economy.withdrawPlayer(player, price);
            } else {
                player.sendMessage(MineClans.getInstance().getMessages().getText("factions.buffs.no_money"));
                return false;
            }
        }
        return true;
    }

    public void giveEffects(Faction faction) {
        for (BuffEffect effect : getEffects()) {
            ActiveBuff activeBuff = faction.addBuff(new ActiveBuff(this, effect.getType(), effect.getAmplifier(),
                    effect.getDuration() * 1000, faction));
            MineClans.getInstance().getBuffManager().getActiveBuffs().add(activeBuff);
            MineClans.runSync(() -> {
                activeBuff.giveEffectToFaction();
            });
        }
    }

    public void notify(String playerName, Faction faction) {
        ConfigWrapper messages = MineClans.getInstance().getMessages();
        String title = messages.getString("factions.buffs.activated_other.title");
        String subtitle = messages
                .getString("factions.buffs.activated_other.subtitle")
                .replace("%player%", playerName)
                .replace("%buff%", getDisplayName());
        String notificationMsg = messages
                .getString("factions.buffs.activated_other.msg")
                .replace("%player%", playerName)
                .replace("%buff%", getDisplayName());
        faction.sendMessageTitleMelody(ChatColors.color(notificationMsg), ChatColors.color(title),
                ChatColors.color(subtitle), 10, 40, 10, Melody.BUFF_ACTIVE);
    }
}

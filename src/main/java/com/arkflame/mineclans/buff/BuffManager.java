package com.arkflame.mineclans.buff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.menus.BuffsMenu;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.menus.Menu;

public class BuffManager {
    private Map<String, Buff> buffs = new HashMap<>();
    private Collection<ActiveBuff> activeBuffs = ConcurrentHashMap.newKeySet();
    private ConfigWrapper config;
    private Menu menu;

    public BuffManager(ConfigWrapper config) {
        this.config = config;
        loadBuffs();
    }

    public void reload() {
        loadBuffs();
    }

    private void loadBuffs() {
        buffs.clear();
        if (config.contains("buffs.custom-buffs")) {
            for (String buffName : config.getConfigurationSection("buffs.custom-buffs").getKeys(false)) {
                String path = "buffs.custom-buffs." + buffName;

                String displayName = config.getString(path + ".display_name");
                List<String> lore = config.getStringList(path + ".lore");
                double price = config.getDouble(path + ".price");
                int slot = config.getInt(path + ".slot");

                List<BuffEffect> effects = new ArrayList<>();
                for (String effectStr : config.getStringList(path + ".effects")) {
                    String[] parts = effectStr.split(",");
                    PotionEffectType type = PotionEffectType.getByName(parts[0]);
                    if (type == null) {
                        MineClans.getInstance().getLogger().warning("Invalid effect: " + parts[0]);
                        continue;
                    }
                    int amplifier = Integer.parseInt(parts[1]);
                    int duration = Integer.parseInt(parts[2]);
                    effects.add(new BuffEffect(type, amplifier, duration));
                }
                String material = config.getString(path + ".material");

                Buff buff = new Buff(buffName, displayName, lore, effects, price, slot, material);
                buffs.put(buffName, buff);
            }
        }
        menu = new BuffsMenu(buffs.values());
    }

    public Buff getBuff(String name) {
        return buffs.get(name);
    }

    public Map<String, Buff> getBuffs() {
        return buffs;
    }

    public void openBuffMenu(Player player, Faction faction) {
        // Open the menu for the player
        menu.openInventory(player);
    }

    public Collection<ActiveBuff> getActiveBuffs() {
        return activeBuffs;
    }
}

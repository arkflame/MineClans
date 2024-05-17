package com.arkflame.mineclans.placeholders;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class FactionsPlaceholder extends PlaceholderExpansion {

    private MineClans plugin;

    public FactionsPlaceholder(MineClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true; // This is required to tell PlaceholderAPI to not unregister your expansion on reload
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "factions";
    }

    @Override
    public String getAuthor() {
        return "ArkFlame";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null || !player.isOnline()) {
            return null;
        }

        Player onlinePlayer = (Player) player;
        FactionPlayer factionPlayer = plugin.getFactionPlayerManager().getOrLoad(onlinePlayer.getUniqueId());
        Faction faction = factionPlayer.getFaction();

        if (faction == null) {
            return "";
        }

        switch (identifier) {
            case "name":
                return faction.getName();
            case "online":
                return String.valueOf(faction.getOnlineMembers().size());
            case "owner":
                return plugin.getFactionPlayerManager().getOrLoad(faction.getOwner()).getName();
            case "balance":
                return String.valueOf(faction.getBalance());
            case "members":
                return String.valueOf(faction.getMembers().size());
            default:
                return null;
        }
    }
}
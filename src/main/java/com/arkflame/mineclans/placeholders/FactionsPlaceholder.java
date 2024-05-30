package com.arkflame.mineclans.placeholders;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.events.ClanEvent;
import com.arkflame.mineclans.events.ClanEventScheduler;
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
        return false;
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
        ClanEventScheduler eventScheduler = plugin.getClanEventScheduler();
        ClanEvent currentEvent = eventScheduler.getEvent();
        ClanEvent nextEvent = eventScheduler.getNextEvent();

        switch (identifier) {
            case "event_name":
                return currentEvent == null ? "" : currentEvent.getName();
            case "next_event_name":
                return nextEvent == null ? "" : nextEvent.getName();
            case "next_event_time":
                return eventScheduler.getTimeLeftFormatted();
            default:
                break;
        }

        if (faction == null) {
            return "";
        }

        Faction focusedFaction = plugin.getFactionManager().getFaction(faction.getFocusedFaction());

        switch (identifier) {
            case "name":
                return faction.getName();
            case "displayname":
                return faction.getDisplayName();
            case "prefix":
                return ChatColor.GREEN + "**" + faction.getDisplayName() + ChatColor.RESET + " ";
            case "online":
                return String.valueOf(faction.getOnlineMembers().size());
            case "owner":
                return plugin.getFactionPlayerManager().getOrLoad(faction.getOwner()).getName();
            case "balance":
                return String.valueOf(faction.getBalance());
            case "members":
                return String.valueOf(faction.getMembers().size());
            case "focus_name":
                return focusedFaction == null ? "" : focusedFaction.getDisplayName();
            case "focus_online":
                return focusedFaction == null ? "" : String.valueOf(focusedFaction.getOnlineMembers().size());
            case "event_name":
                return currentEvent == null ? "" : currentEvent.getName();
            case "next_event_name":
                return nextEvent == null ? "" : nextEvent.getName();
            case "next_event_time":
                return eventScheduler.getTimeLeftFormatted();
            default:
                return "";
        }
    }
}

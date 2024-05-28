package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.utils.MelodyUtil;
import com.arkflame.mineclans.utils.MelodyUtil.Melody;

public class FactionsMelodyCommand {

    public static void onCommand(Player player, ModernArguments args) {
        if (!args.hasArg(1)) {
            player.sendMessage(ChatColor.RED + "Usage: /f melody <melody>");
            return;
        }

        String melodyName = args.getText(1);
        MelodyUtil.Melody melody;
        try {
            melody = MelodyUtil.Melody.valueOf(melodyName.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid melody name.");
            player.sendMessage(ChatColor.RED + "Available melodies: " + MelodyUtil.getAvailableMelodies());
            return;
        }

        MelodyUtil.playMelody(MineClans.getInstance(), player, melody);
    }
}

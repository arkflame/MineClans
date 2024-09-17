package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.utils.MelodyUtil;

public class FactionsMelodyCommand {
    public static void onCommand(Player player, ModernArguments args) {
        if (!args.hasArg(1)) {
            player.sendMessage(MineClans.getInstance().getMessages().getText("factions.melody.usage"));
            player.sendMessage(MineClans.getInstance().getMessages().getText("factions.melody.available")
                           .replace("%melodies%", MelodyUtil.getAvailableMelodies().toString()));
            return;
        }

        String melodyName = args.getText(1);
        MelodyUtil.Melody melody;
        try {
            melody = MelodyUtil.Melody.valueOf(melodyName.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(MineClans.getInstance().getMessages().getText("factions.melody.invalid"));
            player.sendMessage(MineClans.getInstance().getMessages().getText("factions.melody.available")
                           .replace("%melodies%", MelodyUtil.getAvailableMelodies().toString()));
            return;
        }

        MelodyUtil.playMelody(MineClans.getInstance(), player, melody);
    }
}

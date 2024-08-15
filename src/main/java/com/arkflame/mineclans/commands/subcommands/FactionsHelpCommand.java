package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.ChatColors;

import java.util.ArrayList;
import java.util.List;

public class FactionsHelpCommand {
    private static final String basePath = "factions.help.";
    private static final int commandsPerPage = 5; // Number of commands per page
    private static final List<HelpCommand> helpCommands = new ArrayList<>();

    static {
        // Populate the helpCommands list with command objects
        helpCommands.add(new HelpCommand("create", "/f create <factionName>"));
        helpCommands.add(new HelpCommand("accept", "/f accept <factionName>"));
        helpCommands.add(new HelpCommand("leave", "/f leave"));
        helpCommands.add(new HelpCommand("home", "/f home"));
        helpCommands.add(new HelpCommand("sethome", "/f sethome"));
        helpCommands.add(new HelpCommand("focus", "/f focus"));
        helpCommands.add(new HelpCommand("unfocus", "/f unfocus"));
        helpCommands.add(new HelpCommand("deposit", "/f deposit <amount:all>"));
        helpCommands.add(new HelpCommand("withdraw", "/f withdraw <amount:all>"));
        helpCommands.add(new HelpCommand("carry", "/f carry"));
        helpCommands.add(new HelpCommand("who", "/f who <player:factionName>"));
        helpCommands.add(new HelpCommand("invite", "/f invite <player>"));
        helpCommands.add(new HelpCommand("uninvite", "/f uninvite <player>"));
        helpCommands.add(new HelpCommand("invites", "/f invites"));
        helpCommands.add(new HelpCommand("kick", "/f kick <player>"));
        helpCommands.add(new HelpCommand("announcement", "/f announcement [message here]"));
        helpCommands.add(new HelpCommand("promote", "/f promote <player>"));
        helpCommands.add(new HelpCommand("demote", "/f demote <player>"));
        helpCommands.add(new HelpCommand("open", "/f open"));
        helpCommands.add(new HelpCommand("rename", "/f rename <newName>"));
        helpCommands.add(new HelpCommand("setdiscord", "/f setdiscord <discord>"));
        helpCommands.add(new HelpCommand("disband", "/f disband"));
    }

    public static void onCommand(CommandSender sender, int page) {
        int totalCommands = helpCommands.size();
        int maxPages = (int) Math.ceil((double) totalCommands / commandsPerPage);

        // Handle page boundaries
        if (page < 1) {
            page = 1;
        } else if (page > maxPages) {
            page = maxPages;
        }

        ConfigWrapper messages = MineClans.getInstance().getMessages();
        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(messages.getText(basePath + "header")
                .replace("{page}", String.valueOf(page))
                .replace("{maxPages}", String.valueOf(maxPages)))
                .append("\n");

        int startIndex = (page - 1) * commandsPerPage;
        int endIndex = Math.min(startIndex + commandsPerPage, totalCommands);

        for (int i = startIndex; i < endIndex; i++) {
            HelpCommand command = helpCommands.get(i);
            messageBuilder.append(command.getHelpLine()).append("\n");
        }

        String footer = messages.getText(basePath + "footer");

        if (!footer.isEmpty()) {
            messageBuilder
                    .append(footer
                            .replace("{page}", String.valueOf(page))
                            .replace("{maxPages}", String.valueOf(maxPages)))
                    .append("\n");
        }

        sender.sendMessage(messageBuilder.toString().trim());
    }

    private static class HelpCommand {
        private final String name;
        private final String usage;

        public HelpCommand(String name, String usage) {
            this.name = name;
            this.usage = usage;
        }

        public String getName() {
            return name;
        }

        public String getUsage() {
            return usage;
        }

        public String getDescription() {
            ConfigWrapper messages = MineClans.getInstance().getMessages();
            return messages.getText(basePath + "description." + getName());
        }

        public String getHelpLine() {
            ConfigWrapper messages = MineClans.getInstance().getMessages();
            String lineFormat = messages.getText(basePath + "line");
            String lineText = lineFormat.replace("{command}", getUsage()).replace("{description}", getDescription());
            return lineText;
        }
    }
}

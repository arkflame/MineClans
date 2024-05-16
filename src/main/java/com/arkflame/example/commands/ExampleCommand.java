package com.arkflame.example.commands;

import org.bukkit.command.CommandSender;

import com.arkflame.example.ExamplePlugin;
import com.arkflame.modernlib.commands.ModernArguments;
import com.arkflame.modernlib.commands.ModernCommand;

public class ExampleCommand extends ModernCommand {
    public ExampleCommand() {
        super("example");
    }

    @Override
    public void onCommand(CommandSender sender, ModernArguments args) {
        String label = args.getLabel();
        String arg1 = args.getText(0);
        int arg2 = args.getNumber(1);

        if (args.hasArg(0)) {
            sender.sendMessage("You wrote text: " + arg1);
        } 

        if (args.hasArg(1)) {
            sender.sendMessage("You wrote a number: " + arg2);
        } 
        
        if (!args.hasArg()) {
            sender.sendMessage("You wrote no arguments");
            sender.sendMessage("Usage: /" + label + " [text] [number]");
        }

        sender.sendMessage(ExamplePlugin.getInstance().getMsg().getText("messages.from-command"));
    }
}

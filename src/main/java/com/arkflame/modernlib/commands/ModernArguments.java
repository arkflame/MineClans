package com.arkflame.modernlib.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ModernArguments {
    private String label;
    private String[] args;

    public ModernArguments(String label, String[] args) {
        this.label = label;
        this.args = args;
    }

    public String getLabel() {
        return label;
    }

    public String[] getArgs() {
        return args;
    }

    public String[] getArgs(int index) {
        int count = args.length - index;
        if (count <= 0) return new String[0];
        String[] newArgs = new String[count];
        for (int newIndex = 0; newIndex < count; newIndex++) {
            newArgs[newIndex] = getText(index++);
        }

        return newArgs;
    }

    public String getText(int index) {
        if (!hasArg(index)) return null;

        return args[index];
    }
    
    public int getNumber(int index) {
        String text = getText(index);

        if (text == null) return -1;

        try {
            return Integer.parseInt(args[index]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public Player getPlayer(int index) {
        String text = getText(index);

        if (text == null) return null;

        return Bukkit.getPlayer(text);
    }

    public boolean hasArg(int index) {
        return index < args.length;
    }

    public boolean hasArg() {
        return args.length > 0;
    }
}

package com.arkflame.modernlib.commands;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public abstract class ModernCommand extends Command {
    public ModernCommand(String name) {
        super(name);
    }

    public ModernCommand(String name, String ...aliases) {
        super(name);
        setAliases(Arrays.asList(aliases));
    }

    public void register(Plugin plugin) {
        unregisterBukkitCommand();
        try {
            // Get the command map to register the command
            Object commandMap = getCommandMap();
            // Get the method to register the command
            Method registerMethod = getRegisterMethod(commandMap);

            // Register the command
            registerMethod.invoke(commandMap, getName(), this);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException |IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            plugin.getLogger().severe("Exception while handling command register");
            e.printStackTrace();
        }
    }

    private Method getRegisterMethod(Object commandMap) throws NoSuchMethodException, SecurityException {
        return commandMap.getClass().getMethod("register", String.class, Command.class);
    }

    private Object getPrivateField(Object object, String field) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field objectField = clazz.getDeclaredField(field); // Line 283
        boolean accessible = objectField.isAccessible();
        if (!accessible) objectField.setAccessible(true);
        Object result = objectField.get(object);
        if (!accessible) objectField.setAccessible(false);
        return result;
    }

    private Object getCommandMap()  throws IllegalAccessException, NoSuchFieldException {
        Server server = Bukkit.getServer();

        return getPrivateField(server, "commandMap");
    }

    public void unregisterBukkitCommand() {
        try {
            Object commandMap = getCommandMap();
            Object map = null;
            try {
                // Try to use reflection to access the 'knownCommands' field
                map = getPrivateField(commandMap, "knownCommands");
            } catch (NoSuchFieldException ignored) {
                // If 'knownCommands' field doesn't exist, try to use the 'getKnownCommands' method
                Method getKnownCommandsMethod = commandMap.getClass().getMethod("getKnownCommands");
                map = getKnownCommandsMethod.invoke(commandMap);
            }
    
            @SuppressWarnings("unchecked")
            Map<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(getName());
            for (String alias : getAliases()) {
                if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(this.getName())) {
                    knownCommands.remove(alias);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        onCommand(sender, new ModernArguments(label, args));
        return true;
    }

    // Implement this method in your own class to define logic
    public abstract void onCommand(CommandSender sender, ModernArguments args);
}

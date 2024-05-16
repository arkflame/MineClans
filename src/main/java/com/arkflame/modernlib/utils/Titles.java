package com.arkflame.modernlib.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Titles {
    private static final Map<String, Class<?>> NMS_CLASS_CACHE = new HashMap<>();
    private static final Map<Class<?>, Method> CHAT_COMPONENT_METHOD_CACHE = new HashMap<>();
    private static final Map<Class<?>, Constructor<?>> CHAT_PACKET_CONSTRUCTOR_CACHE = new HashMap<>();
    
    private static Class<?> getNMSClass(String name) throws ClassNotFoundException {
        Class<?> cachedClass = NMS_CLASS_CACHE.get(name);
        if (cachedClass != null) {
            return cachedClass;
        }
        
        String fullName = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name;
        Class<?> clazz = Class.forName(fullName);
        NMS_CLASS_CACHE.put(name, clazz);
        return clazz;
    }
    
    private static void sendPacket(Player player, Object packet) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException, IllegalArgumentException, NoSuchFieldException, ClassNotFoundException {
        Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
        Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null || title == null) {
            return;
        }
    
        title = ChatColors.color(title);
        subtitle = subtitle != null ? ChatColors.color(subtitle) : null;
    
        try {
            Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\":\"" + title + "\"}");
            Object chatSubtitle = subtitle != null ? getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\":\"" + subtitle + "\"}") : null;
    
            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
    
            Object titlePacket = titleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
                    fadeIn, stay, fadeOut);
            Object subtitlePacket = subtitle != null ? titleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatSubtitle,
                    fadeIn, stay, fadeOut) : null;
    
            sendPacket(player, titlePacket);
            if (subtitlePacket != null) {
                sendPacket(player, subtitlePacket);
            }
        } catch (Exception e) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }
    
    private static Method getChatComponentMethod(Class<?> clazz) throws NoSuchMethodException {
        Method method = CHAT_COMPONENT_METHOD_CACHE.get(clazz);
        if (method == null) {
            method = clazz.getDeclaredClasses()[0].getMethod("a", String.class);
            CHAT_COMPONENT_METHOD_CACHE.put(clazz, method);
        }
        return method;
    }
    
    private static Constructor<?> getChatPacketConstructor(Class<?> clazz) throws NoSuchMethodException, SecurityException, ClassNotFoundException {
        Constructor<?> constructor = CHAT_PACKET_CONSTRUCTOR_CACHE.get(clazz);
        if (constructor == null) {
            constructor = clazz.getConstructor(getNMSClass("IChatBaseComponent"), byte.class);
            CHAT_PACKET_CONSTRUCTOR_CACHE.put(clazz, constructor);
        }
        return constructor;
    }
    
    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null) {
            return;
        }
    
        message = ChatColors.color(message);
    
        try {
            Class<?> chatBaseComponentClass = getNMSClass("IChatBaseComponent");
            Method chatComponentMethod = getChatComponentMethod(chatBaseComponentClass);
            Object chatComponent = chatComponentMethod.invoke(null, "{\"text\":\"" + message + "\"}");
    
            Class<?> packetClass = getNMSClass("PacketPlayOutChat");
            Constructor<?> actionBarConstructor = getChatPacketConstructor(packetClass);
            Object actionBarPacket = actionBarConstructor.newInstance(chatComponent, (byte) 2);
    
            sendPacket(player, actionBarPacket);
        } catch (Exception e) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }
}

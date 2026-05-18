package ru.lynceus.trollplayer.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class MessageUtils {

    private static final String PREFIX = "§8[§cTroll§8] §r";

    public static String prefix(String message) {
        return PREFIX + message;
    }

    public static String success(String message) {
        return PREFIX + "§a" + message;
    }

    public static String error(String message) {
        return PREFIX + "§c" + message;
    }

    public static String info(String message) {
        return PREFIX + "§7" + message;
    }

    // Adventure API variants
    public static Component successComponent(String message) {
        return Component.text("[Troll] ").color(NamedTextColor.DARK_RED)
                .append(Component.text(message).color(NamedTextColor.GREEN));
    }
    public static Component infoComponent(String message) {
        return Component.text("[Troll] ").color(NamedTextColor.DARK_RED)
                .append(Component.text(message).color(NamedTextColor.YELLOW));
    }
    public static Component errorComponent(String message) {
        return Component.text("[Troll] ").color(NamedTextColor.DARK_RED)
                .append(Component.text(message).color(NamedTextColor.RED));
    }
}


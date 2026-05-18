package ru.lynceus.trollplayer.utils;

import ru.lynceus.trollplayer.TrollPlayer;

import java.util.logging.Logger;

public class LogUtils {

    private static Logger getLogger() {
        return TrollPlayer.getInstance().getLogger();
    }

    public static void info(String message) {
        getLogger().info("[i] " + message);}
    public static void warn(String message) {
        getLogger().warning("[!] " + message);}
    public static void error(String message) {
        getLogger().severe("[ERROR] " + message);}
    public static void debug(String message) {
        if (TrollPlayer.getInstance().getConfig().getBoolean("debug", false)) {
            getLogger().info("[DEBUG] " + message);
        }
    }
}


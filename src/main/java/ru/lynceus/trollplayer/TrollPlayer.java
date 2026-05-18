package ru.lynceus.trollplayer;

import org.bukkit.plugin.java.JavaPlugin;
import ru.lynceus.trollplayer.commands.TrollCommand;
import ru.lynceus.trollplayer.listeners.TrollListener;
import ru.lynceus.trollplayer.managers.TrollManager;
import ru.lynceus.trollplayer.utils.LogUtils;

public final class TrollPlayer extends JavaPlugin {

    private static TrollPlayer instance;
    private TrollManager trollManager;
    private TrollCommand trollCommand;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        trollManager = new TrollManager(this);

        TrollListener l = new TrollListener(this);
        trollCommand = new TrollCommand(this);
        getCommand("troll").setExecutor(trollCommand);
        getServer().getPluginManager().registerEvents(l, this);

        LogUtils.debug("TrollPlayer " + getDescription().getVersion() + " by Lynceus");
        LogUtils.info("TrollPlayer enabled.");
    }

    @Override
    public void onDisable() {
        trollManager.clearAll();
        LogUtils.info("TrollPlayer disabled.");
    }

    public static TrollPlayer getInstance() { return instance; }
    public TrollManager getTrollManager() { return trollManager; }
    public TrollCommand getTrollCommand() { return trollCommand; }
}

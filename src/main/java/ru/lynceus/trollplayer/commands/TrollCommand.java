package ru.lynceus.trollplayer.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.lynceus.trollplayer.TrollPlayer;
import ru.lynceus.trollplayer.managers.TrollAction;
import ru.lynceus.trollplayer.utils.MessageUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrollCommand implements CommandExecutor, TabCompleter {

    private final TrollPlayer plugin;

    public TrollCommand(TrollPlayer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("trollplayer.use")) {
            sender.sendMessage(MessageUtils.error("No permission."));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(MessageUtils.info("Usage: /troll <player> <action>"));
            sender.sendMessage(MessageUtils.info("Actions: " + TrollAction.listAll()));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtils.error("Player not found: " + args[0]));
            return true;
        }
        TrollAction action;
        try {
            action = TrollAction.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(MessageUtils.error("Unknown action: " + args[1]));
            sender.sendMessage(MessageUtils.info("Actions: " + TrollAction.listAll()));
            return true;
        }
        plugin.getTrollManager().applyTroll(sender, target, action);
        return true;
    }
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return Arrays.stream(TrollAction.values())
                    .map(a -> a.name().toLowerCase())
                    .filter(a -> a.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}


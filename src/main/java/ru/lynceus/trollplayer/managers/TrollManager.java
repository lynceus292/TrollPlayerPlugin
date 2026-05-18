package ru.lynceus.trollplayer.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.lynceus.trollplayer.TrollPlayer;
import ru.lynceus.trollplayer.utils.LogUtils;
import ru.lynceus.trollplayer.utils.MessageUtils;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TrollManager {

    private final TrollPlayer plugin;
    private final Map<UUID, TrollAction> activeTrolls = new HashMap<>();
    private final Map<UUID, BukkitTask> fakeLagTasks = new HashMap<>();
    private final Map<UUID, Long> fakeBanned = new HashMap<>();
    private final Random random = new Random();

    public TrollManager(TrollPlayer plugin) {
        this.plugin = plugin;
    }

    public void applyTroll(CommandSender sender, Player target, TrollAction action) {
        LogUtils.info(sender.getName() + " trolled " + target.getName() + " with " + action.name());

        switch (action) {
            case CRASH -> {
                String leafText = plugin.getConfig().getString("crash.leaf", "w");
                String ten   = "%1$s".repeat(5);
                String seven = "%1$s".repeat(7);

                String node = "{\"translate\":\"" + ten + "\",\"with\":[\"" + leafText + "\"]}";
                for (int i = 0; i < 11; i++)
                    node = "{\"translate\":\"" + ten + "\",\"with\":[" + node + "]}";
                String json = "[{\"translate\":\"" + seven + "\",\"with\":[" + node + "]}]";

                plugin.getServer().dispatchCommand(
                        plugin.getServer().getConsoleSender(),
                        "tellraw " + target.getName() + " " + json
                );
                sender.sendMessage(MessageUtils.success("Sent crash to " + target.getName() + "."));
            }
            case FAKE_BAN -> {
                String reason = plugin.getConfig().getString("fake-ban.reason", "Griefing");
                String appeal = plugin.getConfig().getString("fake-ban.appeal-url", "mc.example.com");
                int blockSeconds = plugin.getConfig().getInt("fake-ban.block-reconnect-seconds", 15);
                Component banScreen = Component.text()
                        .append(Component.text("You have been banned from this server.", NamedTextColor.RED))
                        .appendNewline()
                        .append(Component.text("Reason: ", NamedTextColor.GRAY))
                        .append(Component.text(reason, NamedTextColor.WHITE))
                        .appendNewline()
                        .append(Component.text("Appeal at: ", NamedTextColor.GRAY))
                        .append(Component.text(appeal, NamedTextColor.WHITE))
                        .build();
                fakeBanned.put(target.getUniqueId(), System.currentTimeMillis() + (blockSeconds * 1000L));
                target.kick(banScreen);
                sender.sendMessage(MessageUtils.success("Sent fake ban to " + target.getName() + " (blocked for " + blockSeconds + "s)."));
            }
            case FAKE_LAG -> {
                if (hasFakeLag(target.getUniqueId())) {
                    stopFakeLag(target);
                    sender.sendMessage(MessageUtils.info("Stopped fake lag on " + target.getName() + "."));
                    return;
                }
                activeTrolls.put(target.getUniqueId(), TrollAction.FAKE_LAG);
                Deque<Location> positions = new ArrayDeque<>();

                int minInterval  = plugin.getConfig().getInt("fake-lag.setback-min-interval-ticks", 15);
                int maxInterval  = plugin.getConfig().getInt("fake-lag.setback-max-interval-ticks", 40);
                int minSteps     = plugin.getConfig().getInt("fake-lag.setback-min-steps-back", 5);
                int maxSteps     = plugin.getConfig().getInt("fake-lag.setback-max-steps-back", 12);
                int pingChance   = plugin.getConfig().getInt("fake-lag.ping-spike-chance", 40);
                int pingMin      = plugin.getConfig().getInt("fake-lag.ping-spike-min", 500);
                int pingMax      = plugin.getConfig().getInt("fake-lag.ping-spike-max", 2000);

                int[] ticksUntilSetback = {minInterval + random.nextInt(Math.max(1, maxInterval - minInterval))};
                int[] tickCounter = {0};

                BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                    if (!target.isOnline()) { stopFakeLag(target); return; }

                    positions.addLast(target.getLocation().clone());
                    if (positions.size() > 30) positions.pollFirst();

                    if (random.nextInt(100) < pingChance)
                        setFakePing(target, pingMin + random.nextInt(Math.max(1, pingMax - pingMin)));

                    tickCounter[0]++;
                    if (tickCounter[0] >= ticksUntilSetback[0] && positions.size() >= 6) {
                        int stepsBack = minSteps + random.nextInt(Math.max(1, maxSteps - minSteps));
                        Location[] arr = positions.toArray(new Location[0]);
                        Location setback = arr[Math.max(0, arr.length - stepsBack)];
                        target.teleport(setback);
                        tickCounter[0] = 0;
                        ticksUntilSetback[0] = minInterval + random.nextInt(Math.max(1, maxInterval - minInterval));
                    }
                }, 0L, 10L);

                fakeLagTasks.put(target.getUniqueId(), task);
                sender.sendMessage(MessageUtils.success("Fake lag applied to " + target.getName() + "."));
            }
            case FREEZE -> {
                activeTrolls.put(target.getUniqueId(), TrollAction.FREEZE);
                sender.sendMessage(MessageUtils.success("Froze " + target.getName() + "."));
            }
            case RANDOM_TP -> {
                int radius = plugin.getConfig().getInt("random-tp.radius", 1000);
                int[] count = {0};
                plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
                    if (!target.isOnline() || count[0] >= 5) { task.cancel(); return; }
                    Location loc = target.getWorld().getSpawnLocation().clone();
                    loc.add(random.nextInt(radius * 2) - radius, 0, random.nextInt(radius * 2) - radius);
                    loc.setY(target.getWorld().getHighestBlockYAt(loc) + 1);
                    target.teleport(loc);
                    count[0]++;
                }, 0L, 20L);
                sender.sendMessage(MessageUtils.success("Teleporting " + target.getName() + " 5 times!"));
            }
            case SPAM_SOUNDS -> {
                List<Sound> sounds = new ArrayList<>();
                Registry.SOUNDS.forEach(sounds::add);
                long interval = plugin.getConfig().getLong("spam-sounds.interval-ticks", 5L);
                plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
                    if (!target.isOnline()) { task.cancel(); return; }
                    target.playSound(target.getLocation(), sounds.get(random.nextInt(sounds.size())), 1f, random.nextFloat() * 2);
                }, 0L, interval);
                sender.sendMessage(MessageUtils.success("Spamming sounds to " + target.getName() + "."));
            }
        }
    }
    public void stopFakeLag(Player target) {
        activeTrolls.remove(target.getUniqueId());
        BukkitTask task = fakeLagTasks.remove(target.getUniqueId());
        if (task != null) task.cancel();
        setFakePing(target, target.getPing());}
    public boolean hasFakeLag(UUID uuid) {
        return activeTrolls.getOrDefault(uuid, null) == TrollAction.FAKE_LAG;}
    public void unfreeze(Player target) {
        activeTrolls.remove(target.getUniqueId());}
    public boolean isFrozen(UUID uuid) {
        return activeTrolls.getOrDefault(uuid, null) == TrollAction.FREEZE;}
    public boolean isFakeBanned(UUID uuid) {
        Long expiry = fakeBanned.get(uuid);
        if (expiry == null) return false;
        if (System.currentTimeMillis() > expiry) { fakeBanned.remove(uuid); return false; }
        return true;}
    public Component getFakeBanMessage() {
        String reason = plugin.getConfig().getString("fake-ban.reason", "Griefing");
        String appeal = plugin.getConfig().getString("fake-ban.appeal-url", "mc.example.com");
        return Component.text()
                .append(Component.text("You have been banned from this server.", NamedTextColor.RED))
                .appendNewline()
                .append(Component.text("Reason: ", NamedTextColor.GRAY))
                .append(Component.text(reason, NamedTextColor.WHITE))
                .appendNewline()
                .append(Component.text("Appeal at: ", NamedTextColor.GRAY))
                .append(Component.text(appeal, NamedTextColor.WHITE))
                .build();
    }
    public void clearAll() {
        fakeLagTasks.values().forEach(BukkitTask::cancel);
        fakeLagTasks.clear();
        activeTrolls.clear();
    }
    private void setFakePing(Player player, int ping) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Field f;
            try {
                f = handle.getClass().getField("latency");
            } catch (NoSuchFieldException e) {
                f = handle.getClass().getField("ping"); // fallback for older mappings
            }
            f.set(handle, ping);
        } catch (Exception e) {
            LogUtils.warn("setFakePing failed: " + e.getMessage());
        }
    }
}

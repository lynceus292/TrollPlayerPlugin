package ru.lynceus.trollplayer.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import ru.lynceus.trollplayer.TrollPlayer;

import java.util.Random;

public class TrollListener implements Listener {

    private final TrollPlayer plugin;
    private final Random random = new Random();

    public TrollListener(TrollPlayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.getTrollManager().isFakeBanned(event.getUniqueId())) return;
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                plugin.getTrollManager().getFakeBanMessage());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (plugin.getTrollManager().isFrozen(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (plugin.getTrollManager().isFrozen(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof org.bukkit.entity.Player player)) return;
        if (!plugin.getTrollManager().hasFakeLag(player.getUniqueId())) return;
        int chance = plugin.getConfig().getInt("fake-lag.attack-fail-chance", 55);
        if (random.nextInt(100) < chance) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getTrollManager().hasFakeLag(event.getPlayer().getUniqueId())) return;
        int chance = plugin.getConfig().getInt("fake-lag.block-break-fail-chance", 65);
        if (random.nextInt(100) < chance) {
            event.setCancelled(true);
            event.getPlayer().sendBlockChange(event.getBlock().getLocation(), event.getBlock().getBlockData());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.getTrollManager().isFrozen(event.getPlayer().getUniqueId())) {
            plugin.getTrollManager().unfreeze(event.getPlayer());
        }
        if (plugin.getTrollManager().hasFakeLag(event.getPlayer().getUniqueId())) {
            plugin.getTrollManager().stopFakeLag(event.getPlayer());
        }
    }
}

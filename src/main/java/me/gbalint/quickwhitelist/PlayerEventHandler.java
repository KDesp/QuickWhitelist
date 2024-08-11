package me.gbalint.quickwhitelist;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventHandler implements Listener {
    private QuickWhitelist plugin;

    PlayerEventHandler(QuickWhitelist plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getWLEnabled()) {
            return;
        }
        if (!(plugin.getWLCache().contains(p.getName()) || p.hasPermission("quickwhitelist.bypass"))) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            plugin.getConfig().getString("msgs.kick", "&9You are not whitelisted!")));
            plugin.getLogger().warning(plugin.getConfig().getString("msgs.console-log").replaceAll("%player%", p.getName()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (plugin.getManuallyAddedPlayers().contains(p.getName())) {
            plugin.removeFromWLCache(p.getName());
            plugin.getLogger().info("Player " + p.getName() + " removed from the whitelist.");
        }
    }
}

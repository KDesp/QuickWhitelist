package me.gbalint.quickwhitelist;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.HashSet;

public class LoginEvent implements Listener {
    private QuickWhitelist plugin;
    private HashSet<String> whitelistCache;
    LoginEvent(QuickWhitelist plugin) {
        this.plugin = plugin;
        whitelistCache = plugin.getWLCache();
    }

    @EventHandler
    public void PlayerLoginEvent(PlayerLoginEvent e){
        Player p = e.getPlayer();
        if (!plugin.isEnabled()){
            return;
        }
        if (!(whitelistCache.contains(p.getDisplayName()) || p.hasPermission("quickwhitelist.bypass"))) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            plugin.getConfig().getString("msgs.kick","&9You are not whitelisted!")));
            plugin.getLogger().warning(plugin.getConfig().getString("msgs.console-log"));
        }
    }
}

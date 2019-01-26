package me.gbalint.quickwhitelist;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class LoginEvent implements Listener {
    private QuickWhitelist plugin;
    LoginEvent(QuickWhitelist plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void PlayerLoginEvent(PlayerLoginEvent e){
        Player p = e.getPlayer();
        if (!plugin.getWLEnabled()){
            return;
        }
        if (!(plugin.getWLCache().contains(p.getName()) || p.hasPermission("quickwhitelist.bypass"))) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            plugin.getConfig().getString("msgs.kick","&9You are not whitelisted!")));
            plugin.getLogger().warning(plugin.getConfig().getString("msgs.console-log").replaceAll("%player%",p.getName()));
        }
    }
}

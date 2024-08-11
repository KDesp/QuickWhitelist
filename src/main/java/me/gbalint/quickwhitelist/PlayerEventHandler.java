package me.gbalint.quickwhitelist;

import org.bukkit.Bukkit;
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

        // Se a whitelist do plugin estiver desabilitada, permitir a entrada
        if (!plugin.getWLEnabled()) {
            return;
        }

        // Verificar se o jogador está na whitelist do plugin ou na whitelist padrão do Minecraft
        if (!(plugin.getWLCache().contains(p.getName()) || p.isWhitelisted() || p.hasPermission("quickwhitelist.bypass"))) {
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

        // Se o jogador foi adicionado manualmente pela whitelist do plugin
        if (plugin.getManuallyAddedPlayers().contains(p.getName())) {
            plugin.removeFromWLCache(p.getName());

            // Remover o jogador da whitelist do Minecraft
            p.setWhitelisted(false);

            plugin.getLogger().info("Player " + p.getName() + " removed from the whitelist.");
        }
    }
}

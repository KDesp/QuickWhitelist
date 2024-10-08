package me.gbalint.quickwhitelist;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WLCommand implements CommandExecutor {
    private QuickWhitelist plugin;

    WLCommand(QuickWhitelist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!sender.hasPermission("quickwhitelist.edit")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', plugin.getConfig().getString("msgs.noperm")));
        } else if (strings.length < 1) {
            sender.sendMessage(ChatColor.GREEN + "Usage:");
            sender.sendMessage(ChatColor.GREEN + "/qw <enable/disable>");
            sender.sendMessage(ChatColor.GREEN + "/qw add <player>");
            sender.sendMessage(ChatColor.GREEN + "/qw remove <player>");
            sender.sendMessage(ChatColor.GREEN + "/qw clearcache");
            sender.sendMessage(ChatColor.GREEN + "/qw clearall");
            sender.sendMessage(ChatColor.GREEN + "/qw reload");
            sender.sendMessage(ChatColor.GREEN + "/qw flush");
            sender.sendMessage(ChatColor.GREEN + "/qw status");
        } else if (strings.length < 2) {
            switch (strings[0].toLowerCase()) {
                case "enable":
                    plugin.getConfig().set("enabled", true);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes(
                            '&', plugin.getConfig().getString("msgs.enabled")));
                    plugin.refreshWLEnabled();
                    plugin.saveConfig();
                    break;
                case "disable":
                    plugin.getConfig().set("enabled", false);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes(
                            '&', plugin.getConfig().getString("msgs.disabled")));
                    plugin.refreshWLEnabled();
                    plugin.saveConfig();
                    break;
                case "clearcache":
                    plugin.clearWLCache();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes(
                            '&', plugin.getConfig().getString("msgs.cacheclear")));
                    break;
                case "clearall":
                    plugin.getConfig().set("whitelisted", "[]");
                    plugin.saveConfig();
                    plugin.clearWLCache();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes(
                            '&', plugin.getConfig().getString("msgs.wl-clear")));
                    break;
                case "reload":
                    if (!sender.hasPermission("quickwhitelist.reload")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                                '&', plugin.getConfig().getString("msgs.noperm")));
                    } else {
                        plugin.reloadConfig();
                        plugin.refreshWLEnabled();
                        plugin.refreshWLCache();
                        sender.sendMessage("Config Reloaded!");
                    }
                    break;
                case "flush":
                    plugin.getConfig().set("whitelisted", plugin.getWLCache().toArray());
                    plugin.saveConfig();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes(
                            '&', plugin.getConfig().getString("msgs.wl-flush")));
                    break;
                case "status":
                    String size = String.valueOf(plugin.getWLCache().size());
                    String enabledString = plugin.getWLEnabled()
                            ? plugin.getConfig().getString("msgs.enabled")
                            : plugin.getConfig().getString("msgs.disabled");
                    StringBuilder statusString = new StringBuilder(plugin.getConfig().getString("msgs.status")
                            .replaceAll("%status%", enabledString)
                            .replaceAll("%count%", size).concat("\n" + ChatColor.GREEN));
                    for (String name : plugin.getWLCache()) {
                        statusString.append(name).append(" ");
                    }
                    sender.sendMessage(ChatColor.translateAlternateColorCodes(
                            '&', statusString.toString()));
                    break;
                case "add":
                case "remove":
                    sender.sendMessage(ChatColor.translateAlternateColorCodes(
                            '&', plugin.getConfig().getString("msgs.argument-error")));
                    break;
            }
        } else if (strings[0].equalsIgnoreCase("add")) {
            // Verificar a permissão para o comando /qw add
            if (!sender.hasPermission("quickwhitelist.add")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }

            plugin.addToWLCache(strings[1]);
            plugin.addToManuallyAddedPlayers(strings[1]);

            // Adicionar o jogador à whitelist do Minecraft
            OfflinePlayer player = Bukkit.getOfflinePlayer(strings[1]);
            player.setWhitelisted(true);

            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', plugin.getConfig().getString("msgs.player-add")));
        } else if (strings[0].equalsIgnoreCase("remove")) {
            // Verificar a permissão para o comando /qw remove
            if (!sender.hasPermission("quickwhitelist.remove")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }

            plugin.removeFromWLCache(strings[1]);

            // Remover o jogador da whitelist do Minecraft
            OfflinePlayer player = Bukkit.getOfflinePlayer(strings[1]);
            player.setWhitelisted(false);

            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', plugin.getConfig().getString("msgs.player-remove")));
        }
        return true;
    }
}

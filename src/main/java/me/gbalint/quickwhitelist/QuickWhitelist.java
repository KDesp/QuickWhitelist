package me.gbalint.quickwhitelist;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class QuickWhitelist extends JavaPlugin {

    private File configFile;
    private FileConfiguration config;
    private LoginEvent le;
    private boolean isEnabled;
    private HashSet<String> whitelistCache;
    private QuickWhitelist plugin;
    public QuickWhitelist getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable(){
        plugin = this;
        createConfig();
        whitelistCache = new HashSet<>();
        refreshWLCache();
        le = new LoginEvent(plugin);
        Bukkit.getServer().getPluginManager().registerEvents(le,plugin);
        getCommand("quickwhitelist").setExecutor(new WLCommand(plugin));
    }
    @Override
    public void onDisable(){
        HandlerList.unregisterAll(le);
    }
    private void createConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    boolean getWLEnabled(){
        return isEnabled;
    }
    void refreshWLEnabled(){
        isEnabled = plugin.getConfig().getBoolean("enabled");
    }
    HashSet<String> getWLCache(){
        return whitelistCache;
    }
    void refreshWLCache(){
        clearWLCache();
        whitelistCache.addAll(plugin.getConfig().getStringList("whitelisted"));
    }
    void clearWLCache(){
        whitelistCache.clear();
    }
    void addToWLCache(String name){
        whitelistCache.add(name);
    }
    void removeFromWLCache(String name){
        whitelistCache.remove(name);
    }

}

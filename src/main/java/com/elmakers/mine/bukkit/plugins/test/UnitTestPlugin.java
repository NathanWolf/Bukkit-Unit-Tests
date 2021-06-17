package com.elmakers.mine.bukkit.plugins.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UnitTestPlugin extends JavaPlugin implements Listener, TabExecutor {
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    private Set<UUID> loadedEntities = new HashSet<>();

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        getCommand("checkentities").setExecutor(this);
        getCommand("checkentities").setTabCompleter(this);
        getCommand("spawnpersistent").setExecutor(this);
        getCommand("spawnpersistent").setTabCompleter(this);
    }

    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName();
        switch (commandName) {
            case "checkentities":
                return checkEntities(sender, args);
            case "spawnpersistent":
                return spawnPersistent(sender, args);
            default:
                return false;
        }
    }

    private boolean checkEntities(CommandSender sender, String[] args) {
        return true;
    }

    private boolean spawnPersistent(CommandSender sender, String[] args) {
        return true;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

    protected void sendMessage(CommandSender sender, String string) {
        sender.sendMessage(CHAT_PREFIX + string);
    }

    protected void sendError(CommandSender sender, String string) {
        sender.sendMessage(ERROR_PREFIX + string);
    }
}

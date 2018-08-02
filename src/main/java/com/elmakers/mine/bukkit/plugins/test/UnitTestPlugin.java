package com.elmakers.mine.bukkit.plugins.test;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UnitTestPlugin extends JavaPlugin implements Listener {
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;
    private CommandSender target;

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        target = Bukkit.getConsoleSender();
    }

    public void onDisable() {
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        String message = "Interact from " + event.getEntity().getType() + " on " + event.getBlock().getType();
        if (event.getEntity().getType() == EntityType.VILLAGER) {
            event.setCancelled(true);
            message += " DENIED!";
        }
        sendMessage(target, message);
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        target = event.getPlayer();
    }

    protected void sendMessage(CommandSender sender, String string) {
        sender.sendMessage(CHAT_PREFIX + string);
    }

    protected void sendError(CommandSender sender, String string) {
        sender.sendMessage(ERROR_PREFIX + string);
    }
}

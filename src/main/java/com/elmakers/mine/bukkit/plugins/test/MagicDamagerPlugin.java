package com.elmakers.mine.bukkit.plugins.test;

import com.elmakers.mine.bukkit.api.magic.Mage;
import com.elmakers.mine.bukkit.api.magic.MageController;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MagicDamagerPlugin extends JavaPlugin implements Listener {
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    private MageController controller;

    public void onEnable() {
        Plugin magic = getServer().getPluginManager().getPlugin("Magic");
        if (magic == null) {
            getLogger().severe("Could not find Magic plugin");
            return;
        }

        controller = ((MagicAPI)magic).getController();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
    }

    public void onDisable() {
    }

    protected void sendMessage(CommandSender sender, String string) {
        sender.sendMessage(CHAT_PREFIX + string);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        Entity damaged = event.getEntity();
        Mage mage = controller.getRegisteredMage(damaged);
        if (mage == null) {
            return;
        }

        String message = "Entity " + ChatColor.AQUA +
                damaged.getType() +
                ChatColor.WHITE + " damaged by " +
                ChatColor.GOLD + event.getCause() +
                ChatColor.WHITE + " last type:  " +
                ChatColor.GOLD + mage.getLastDamageType();
        Entity damager = mage.getLastDamager();
        if (damager == null) {
            message += ChatColor.RED + ", magic has unknown damager";
        } else {
            message += ChatColor.WHITE + ", magic damager: " + ChatColor.AQUA + damager.getType();
        }

        Entity eventDamager = null;
        if (event instanceof EntityDamageByEntityEvent) {
            eventDamager = ((EntityDamageByEntityEvent)event).getDamager();
        }
        if (eventDamager == null) {
            message += ChatColor.YELLOW + ", event has unknown damager";
        } else {
            message += ChatColor.WHITE + ", event damager: " + ChatColor.AQUA + eventDamager.getType();
        }
        for (Player player : getServer().getOnlinePlayers()) {
            sendMessage(player, message);
        }
    }

    protected void sendError(CommandSender sender, String string) {
        sender.sendMessage(ERROR_PREFIX + string);
    }
}

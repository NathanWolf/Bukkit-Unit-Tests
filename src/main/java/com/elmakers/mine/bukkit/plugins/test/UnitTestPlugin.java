package com.elmakers.mine.bukkit.plugins.test;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class UnitTestPlugin extends JavaPlugin implements Listener {
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void onDisable() {
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendMessage(event.getPlayer(), "Craft some sticks!");
        event.getPlayer().getInventory().addItem(new ItemStack(Material.BIRCH_PLANKS, 32));
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        for (HumanEntity entity : event.getInventory().getViewers()) {
            entity.sendMessage("Setting custom name");
        }
        ItemStack item = event.getCurrentItem();

        // This is the culprit here.
        item.setDurability(item.getDurability());
    }

    protected void sendMessage(CommandSender sender, String string) {
        sender.sendMessage(CHAT_PREFIX + string);
    }

    protected void sendError(CommandSender sender, String string) {
        sender.sendMessage(ERROR_PREFIX + string);
    }
}

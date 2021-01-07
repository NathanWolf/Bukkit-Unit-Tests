package com.elmakers.mine.bukkit.plugins.test;


import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.ImmutableSet;

public class LockablePlugin extends JavaPlugin implements CommandExecutor {
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;
    private static final int MAX_RANGE = 64;
    private static final Set<Material> transparent = ImmutableSet.of(Material.AIR, Material.CAVE_AIR, Material.TALL_GRASS);
    private static final Material KEY_MATERIAL = Material.TRIPWIRE_HOOK;

    public void onEnable() {
        getCommand("lock").setExecutor(this);
    }

    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sendError(sender, "You must be a player");
            return true;
        }
        Player player = (Player)sender;
        Block block = player.getTargetBlock(transparent, MAX_RANGE);
        if (block == null || block.getType() != Material.CHEST) {
            sendError(sender, "You must be looking at a chest");
            return true;
        }
        BlockState state = block.getState();
        if (!(state instanceof Chest)) {
            sendError(sender, "Was expecting block of type " + block.getType() + " to have a Chest BlockState, but it had " + state.getClass().getSimpleName());
            return true;
        }
        Chest chest = (Chest)state;
        if (chest.isLocked()) {
            sendError(sender, "That chest is already locked");
            return true;
        }
        String lock = ChatColor.RED + "KEY";
        chest.setLock(lock);
        chest.update();
        sendMessage(sender, "Container is now locked");

        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;
            if (!meta.hasDisplayName()) continue;
            if (meta.getDisplayName().equals(lock)) {
                sendMessage(sender, "   not giving another key, you already have one");
                return true;
            }
        }
        ItemStack keyItem = new ItemStack(KEY_MATERIAL);
        ItemMeta meta = keyItem.getItemMeta();
        meta.setDisplayName(lock);
        keyItem.setItemMeta(meta);
        player.getInventory().addItem(keyItem);
        return true;
    }

    protected void sendMessage(CommandSender sender, String string) {
        sender.sendMessage(CHAT_PREFIX + string);
    }

    protected void sendError(CommandSender sender, String string) {
        sender.sendMessage(ERROR_PREFIX + string);
    }
}

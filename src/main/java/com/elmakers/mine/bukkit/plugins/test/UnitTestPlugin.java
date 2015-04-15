package com.elmakers.mine.bukkit.plugins.test;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.PersistentMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;

public class UnitTestPlugin extends JavaPlugin implements Listener
{
    private String savedItem = null;
    private Map<String, LinkedList<MemoryConfiguration>> storedInventories = new HashMap<String, LinkedList<MemoryConfiguration>>();
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    public void onEnable()
    {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
    }

    public void onDisable()
    {
    }

    protected void sendMessage(CommandSender sender, String string)
    {
        sender.sendMessage(CHAT_PREFIX + string);
    }

    protected void sendError(CommandSender sender, String string)
    {
        sender.sendMessage(ERROR_PREFIX + string);
    }

    public void onInventoryPush(Player player)
    {
        Inventory inventory = player.getInventory();
        MemoryConfiguration stored = new MemoryConfiguration();
        stored.set("items", inventory.getContents());

        LinkedList<MemoryConfiguration> inventories = getStoredInventories(player, true);
        inventories.add(stored);

        inventory.clear();
        sendMessage(player, "Items stored. You have " + ChatColor.BLUE + inventories.size() + CHAT_PREFIX + " stored inventories on the stack.");
    }

    public void onInventoryPop(Player player)
    {
        LinkedList<MemoryConfiguration> inventories = getStoredInventories(player, false);
        if (inventories == null || inventories.size() == 0) {
            sendMessage(player, "No stored inventories for you");
            return;
        }

        World world = player.getWorld();
        Inventory inventory = player.getInventory();
        ItemStack[] contents = player.getInventory().getContents();
        int droppedItems = 0;
        for (ItemStack itemStack : contents)
        {
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;
            droppedItems++;
            world.dropItemNaturally(player.getLocation(), itemStack);
        }
        inventory.clear();
        MemoryConfiguration stored = inventories.removeFirst();
        ItemStack[] items = (ItemStack[])stored.get("items");
        inventory.setContents(items);

        sendMessage(player, "Dropped " + ChatColor.BLUE + droppedItems + CHAT_PREFIX + " items and restored inventory.");
        sendMessage(player, "You have " + ChatColor.BLUE + inventories.size() + CHAT_PREFIX + " stored inventories on the stack.");
    }

    protected LinkedList<MemoryConfiguration> getStoredInventories(Player player, boolean create)
    {
        String playerId = player.getUniqueId().toString();
        LinkedList<MemoryConfiguration> inventories = storedInventories.get(playerId);
        if (inventories == null && create) {
            inventories = new LinkedList<MemoryConfiguration>();
            storedInventories.put(playerId, inventories);
        }
        return inventories;
    }

    private void onItemClone(Player player, ItemStack heldItem)
    {
        ItemStack newItem = heldItem.clone();
        World world = player.getWorld();
        world.dropItemNaturally(player.getLocation(), newItem);
        sendMessage(player, "Cloned your " + heldItem.getType().name());
    }

    private void onItemUnEnchant(Player player, ItemStack heldItem)
    {
        ItemMeta meta = heldItem.getItemMeta();
        if (!meta.hasEnchants()) {
            sendError(player, "This item has no enchantments.");
            return;
        }
        ArrayList<Enchantment> enchants = new ArrayList<Enchantment>(meta.getEnchants().keySet());
        for (Enchantment enchantment : enchants) {
            meta.removeEnchant(enchantment);
        }
        heldItem.setItemMeta(meta);
        sendMessage(player, "Your item feels less special");
    }

    private void onItemCheck(Player player, ItemStack heldItem)
    {
        sendMessage(player, "Item Type: " + ChatColor.BLUE + heldItem.getType().name());

        if (!heldItem.hasItemMeta()) {
            sendMessage(player, "Has no ItemMeta");
        } else {
            sendMessage(player, "Has ItemMeta: ");

            ItemMeta meta = heldItem.getItemMeta();
            try {
                sendMessage(player, "Raw: " + ChatColor.GRAY + meta);
            } catch (Throwable ex) {
                sendError(player, "An error occurred serializing item data. See server logs.");
                ex.printStackTrace();;
            }
        }
    }

    private void onItemSave(Player player, ItemStack heldItem)
    {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("item", heldItem);
        savedItem = configuration.saveToString();
        sendMessage(player, "Saved your: " + ChatColor.BLUE + heldItem.getType().name());
    }

    private void onItemLoad(Player player)
    {
        if (savedItem == null) {
            sendMessage(player, ChatColor.RED + "No saved item");
            return;
        }
        YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.loadFromString(savedItem);
            if (!configuration.contains("item")) {
                sendMessage(player, ChatColor.RED + "Deserialized data was missing item section");
                return;
            }
            ItemStack newItem = configuration.getItemStack("item");
            sendMessage(player, "Loaded: " + ChatColor.BLUE + newItem.getType().name());
            World world = player.getWorld();
            world.dropItemNaturally(player.getLocation(), newItem);
        } catch (Exception ex) {
            getLogger().log(Level.WARNING, "Error loading item", ex);
            sendMessage(player, ChatColor.RED + "An error occurred loading your item");
        }
    }

    private void onItemDump(Player player) {
        if (savedItem == null) {
            sendMessage(player, ChatColor.RED + "No saved item");
            return;
        }
        player.sendMessage(savedItem);
        getLogger().info("SAVED ITEM:");
        getLogger().info(savedItem);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arg)
    {
        // All of these commands work with the Player's held item
        if (!(sender instanceof Player)) {
            sendMessage(sender, ERROR_PREFIX + "This command only works in-game");
            return true;
        }

        Player player = (Player)sender;

        if (label.equalsIgnoreCase("invpush")) {
            onInventoryPush(player);
            return true;
        } else if (label.equalsIgnoreCase("invpop")) {
            onInventoryPop(player);
            return true;
        } else if (label.equalsIgnoreCase("itemload")) {
            onItemLoad(player);
            return true;
        } else  if (label.equalsIgnoreCase("itemdump")) {
            onItemDump(player);
            return true;
        }

        ItemStack heldItem = player.getItemInHand();
        if (heldItem == null || heldItem.getType() == Material.AIR)
        {
            sendMessage(sender, ERROR_PREFIX + "You must hold an item first");
            return true;
        }

        if (label.equalsIgnoreCase("itemsave")) {
            onItemSave(player, heldItem);
            return true;
        } else if (label.equalsIgnoreCase("itemunenchant")) {
            onItemUnEnchant(player, heldItem);
            return true;
        } else  if (label.equalsIgnoreCase("itemcheck")) {
            onItemCheck(player, heldItem);
            return true;
        } else  if (label.equalsIgnoreCase("itemclone")) {
            onItemClone(player, heldItem);
            return true;
        }

        return false;
    }
}

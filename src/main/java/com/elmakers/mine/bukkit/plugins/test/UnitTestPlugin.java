package com.elmakers.mine.bukkit.plugins.test;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class UnitTestPlugin extends JavaPlugin implements Listener, CommandExecutor {
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    public void onEnable() {
        getCommand("givekit").setExecutor(this);
        saveDefaultConfig();
    }

    public void onDisable() {
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: givekit <kitname>");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command may only be used in-game");
            return true;
        }
        Configuration config = getConfig();
        if (!config.isSet("kits." + args[0])) {
            sender.sendMessage("No kit named " + args[0]);
            return true;
        }
        Object kitObject = config.get("kits." + args[0]);
        if (kitObject == null) {
            sender.sendMessage("Empty kit: " + args[0]);
            return true;
        }
        if (!(kitObject instanceof List)) {
            sender.sendMessage("Invalid kit: " + args[0]);
            return true;
        }
        List<ItemStack> kitItems = (List<ItemStack>)kitObject;
        Player player = (Player)sender;
        
        // This is assuming you want to actually reset their whole inventory, versus add the items ...
        for (int index = 0; index < kitItems.size(); index++) {
            ItemStack item = kitItems.get(index);
            if (item == null) item = new ItemStack(Material.AIR);
            player.getInventory().setItem(index, item);
        }
        
        
        // Otherwise, just add items like this- but in that case having null entries in the config doesn't make much sense.
        /*
        for (ItemStack item : kitItems) {
            if (item != null {
                player.getInventory().addItem(item);
            }
        }
        */
        return true;
    }
}
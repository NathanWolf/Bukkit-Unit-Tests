package com.elmakers.mine.bukkit.plugins.test;

import java.util.List;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UnitTestPlugin extends JavaPlugin implements Listener, TabExecutor {
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
    }

    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        YamlConfiguration configFile = new YamlConfiguration();
        YamlConfiguration outConfigFile = new YamlConfiguration();
        String inFilename = "/Users/nathan/Documents/Code/Bukkit/MagicPlugin/Magic/src/main/resources/examples/survival/items/economy.yml";
        String outFilename = "/Users/nathan/Documents/Code/Bukkit/MagicPlugin/Magic/src/main/resources/examples/survival/items/economy_pruned.yml";
        try {
            configFile.load(inFilename);
            for (String key : configFile.getKeys(false)) {
                try {
                    Material material = Material.valueOf(key.toUpperCase(Locale.ROOT));
                    new ItemStack(material);
                    outConfigFile.set(key, configFile.getConfigurationSection(key));
                } catch (Exception invalid) {
                    sender.sendMessage("Skipping: " + key + ": " + invalid.getMessage());
                }
            }
            try {
                outConfigFile.save(outFilename);
                sender.sendMessage("Wrote to: " + outFilename);
            } catch (Exception ex) {
                sender.sendMessage("Could not write to file: " + inFilename);
            }
        } catch (Exception ex) {
            sender.sendMessage("Could not load file: " + inFilename);
        }
        return true;
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

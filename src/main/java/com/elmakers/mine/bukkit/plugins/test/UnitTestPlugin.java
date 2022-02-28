package com.elmakers.mine.bukkit.plugins.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UnitTestPlugin extends JavaPlugin implements Listener, TabExecutor {
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        YamlConfiguration newConfig = new YamlConfiguration();
        List<ConfigurationSection> configList = new ArrayList<>();
        MemoryConfiguration newSection = new MemoryConfiguration();
        newSection.set("something", "value");
        configList.add(newSection);
        newConfig.set("list", configList);
        try {
            File outputFile = new File(getDataFolder(), "data.yml");
            newConfig.save(outputFile);
            getLogger().info("Wrote to: " + outputFile.getAbsolutePath());
        } catch (IOException ex) {
            getLogger().severe("Could not save file: " + ex.getMessage());
        }
    }

    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
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

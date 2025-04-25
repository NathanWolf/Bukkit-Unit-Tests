package com.elmakers.mine.bukkit.plugins.test;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;

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
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /check <player>");
            return true;
        }
        String userName = args[0];

        long startTime = System.currentTimeMillis();
        OfflinePlayer offline = Bukkit.getOfflinePlayer(userName);
        if (offline == null) {
            sender.sendMessage(ChatColor.RED + "Could not find player " + userName);
            return true;
        }

        PlayerProfile offlineProfile = offline.getPlayerProfile();
        URL skinURL = offlineProfile.getTextures().getSkin();
        if (skinURL == null) {
            CompletableFuture<PlayerProfile> update = offlineProfile.update();
            update.thenAccept(profile -> showMessage(sender, profile, startTime, true));
        } else {
            showMessage(sender, offlineProfile, startTime, false);
        }

        return true;
    }

    protected void showMessage(CommandSender sender, PlayerProfile profile, long startTime, boolean fetched) {
        long time = System.currentTimeMillis() - startTime;

        sender.sendMessage("User: " + profile.getName());
        sender.sendMessage("UUID: " + profile.getUniqueId().toString());
        sender.sendMessage("Skin: " + profile.getTextures().getSkin());
        sender.sendMessage("Time: " + time + "ms (" + (fetched ? "fetched" : "cached") + ")");
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

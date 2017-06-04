package com.elmakers.mine.bukkit.plugins.test;

import com.elmakers.mine.bukkit.api.event.CastEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UnitTestPlugin extends JavaPlugin implements Listener
{
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

    @EventHandler
    public void onCastEvent (CastEvent e) {

        Bukkit.getLogger().info("Tracking cast event");
        Bukkit.getLogger().info("Success is " + e.getSpellResult().isSuccess());
        Bukkit.getLogger().info("Spell key is " + e.getSpell().getSpellKey().getKey());
    }

    protected void sendError(CommandSender sender, String string)
    {
        sender.sendMessage(ERROR_PREFIX + string);
    }
}

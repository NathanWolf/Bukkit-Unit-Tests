package com.elmakers.mine.bukkit.plugins.test;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.elmakers.mine.bukkit.api.event.PreLoadEvent;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.protection.BlockBreakManager;

public class UnitTestPlugin extends JavaPlugin implements Listener, BlockBreakManager
{
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    public void onEnable()
	{
		PluginManager pm = getServer().getPluginManager();
		MagicAPI api = (MagicAPI)pm.getPlugin("Magic");
        api.getController().register(this);
		getLogger().info("Registered no-break handler");
	}

	public void onDisable()
    {
    }

    @EventHandler
    public void onMagicPreLoad(PreLoadEvent event) {
    }

    protected void sendMessage(CommandSender sender, String string)
    {
        sender.sendMessage(CHAT_PREFIX + string);
    }

    protected void sendError(CommandSender sender, String string)
    {
        sender.sendMessage(ERROR_PREFIX + string);
    }

    @Override
    public boolean hasBreakPermission(Player player, Block block) {
        return false;
    }
}

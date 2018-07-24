package com.elmakers.mine.bukkit.plugins.test;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UnitTestPlugin extends JavaPlugin implements Listener
{
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    public void onEnable()
	{
        getCommand("givemap").setExecutor(this);
        getCommand("createmap").setExecutor(this);

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
	}

	public void onDisable()
    {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendMessage(event.getPlayer(), "Use /givemap and /createmap to test map ids");
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("createmap")) {
            MapView newMap = Bukkit.createMap(Bukkit.getWorlds().get(0));
            sendMessage(sender, "Created map id " + newMap.getId());
            sendMessage(sender, "Now please use /save-all and check world/data to see if the data file is there (it won't be on 1.13)");
        }
        if (command.getName().equals("givemap")) {
            if (!(sender instanceof Player)) {
                sendError(sender, "You will need to use this one in-game");
                return true;
            }
            Player player = (Player)sender;
            MapView newMap = Bukkit.createMap(Bukkit.getWorlds().get(0));
            sendMessage(sender, "Created map id " + newMap.getId());

            ItemStack newMapItem = new ItemStack(Material.FILLED_MAP);

            // Yeah this is hacky, replace with MapMeta.setId when available.
            net.minecraft.server.v1_13_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(newMapItem);
            nmsStack.getOrCreateTag().setInt("map", newMap.getId());
            CraftItemStack craftStack = CraftItemStack.asCraftMirror(nmsStack);

            player.getInventory().addItem(craftStack);

            sendMessage(sender, "Now please use /save-all and check world/data to see if the data file is there (it should be)");
        }
        return true;
    }

    private void sendMessage(CommandSender sender, String string)
    {
        sender.sendMessage(CHAT_PREFIX + string);
    }

    private void sendError(CommandSender sender, String string)
    {
        sender.sendMessage(ERROR_PREFIX + string);
    }
}

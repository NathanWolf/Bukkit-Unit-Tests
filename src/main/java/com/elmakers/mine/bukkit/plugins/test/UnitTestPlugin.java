package com.elmakers.mine.bukkit.plugins.test;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;

public class UnitTestPlugin extends JavaPlugin implements Listener
{
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    private Vector upVector = new Vector(0.0, 0.1, 0.0);

    public void onEnable()
	{
        getCommand("setvelocity").setExecutor(this);

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                 for (Player player : getServer().getOnlinePlayers()) {
                     List<Entity> allNearby = player.getNearbyEntities(10, 10, 10);
                     for (Entity nearby : allNearby) {
                         if (nearby instanceof Item) {
                             nearby.getWorld().spawnParticle(Particle.SPELL_MOB, nearby.getLocation(), 10);
                             nearby.setVelocity(upVector);
                         }
                     }
                 }
            }
        }, 200, 40);
	}

	public void onDisable()
    {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendMessage(event.getPlayer(), "Use /setvelocity <velocity> to change y velocity");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sendError(sender,"Usage: setvelocity <velocity>");
            return true;
        }
        double velocity;
        try {
            velocity = Double.parseDouble(args[0]);
        } catch (Exception ex) {
            sendError(sender,"Velocity expected to be a single number");
            return true;
        }
        upVector = new Vector(0, velocity, 0);

        sendMessage(sender, "Y-Velocity set to " + velocity);
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

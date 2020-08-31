package com.elmakers.mine.bukkit.plugins.test;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.slikey.effectlib.util.VectorUtils;

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

    @EventHandler
    public void onPlayerAnimate(final PlayerAnimationEvent event) {
        if (event.getAnimationType() != PlayerAnimationType.ARM_SWING) return;

        new BukkitRunnable()
        {
            final Location baseLocation = event.getPlayer().getLocation().add(0, 1.25, 0);
            final Location targetLocation = baseLocation.clone();
            int t = 0;

            public void run()
            {
                // I would opt for putting this first to avoid a runaway task
                // Otherwise, probably should wrap everything else in a try/catch
                t++;
                if (t > 20)
                    this.cancel();
                for (double i = -0.5; i < 0.5; i += 0.2)
                {
                    // Just add to y and z here for each i, this represents a plane perpendicular to the
                    // direction the player is facing.
                    // So we want a diagonal line along that grid.
                    //
                    // To make the diagonal line animate out in front of the player, we want to
                    // increment x some amount for each iteration of the Runnable
                    Vector offset = new Vector(0.2 * t, i, i);

                    // We now have an offset vector that points to the direction we want to spawn a particle,
                    // assuming the player's yaw and pitch are 0.
                    // Now rotate the vector to align to the player's actual direction.
                    offset = VectorUtils.rotateVector(offset, baseLocation);

                    // Finally add the offset to the base.
                    // For efficiency we'll re-use the same Location object instead of using the
                    // handy add() method.
                    targetLocation.setX(baseLocation.getX() + offset.getX());
                    targetLocation.setY(baseLocation.getY() + offset.getY());
                    targetLocation.setZ(baseLocation.getZ() + offset.getZ());
                    event.getPlayer().spawnParticle(Particle.REDSTONE, targetLocation, 1, new Particle.DustOptions(new Random().nextDouble() > 0.5 ? org.bukkit.Color.ORANGE : org.bukkit.Color.YELLOW, 1));
                }
            }
        }.runTaskTimer(this, 0, 1);
    }

    protected void sendMessage(CommandSender sender, String string)
    {
        sender.sendMessage(CHAT_PREFIX + string);
    }

    protected void sendError(CommandSender sender, String string)
    {
        sender.sendMessage(ERROR_PREFIX + string);
    }
}

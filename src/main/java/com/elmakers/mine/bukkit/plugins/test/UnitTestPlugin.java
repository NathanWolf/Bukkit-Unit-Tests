package com.elmakers.mine.bukkit.plugins.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSteerVehicleEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

public class UnitTestPlugin extends JavaPlugin implements Listener {
    private final Collection<Entity> cleanupEntities = new ArrayList<Entity>();

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                Iterator<Entity> it = cleanupEntities.iterator();
                while (it.hasNext()) {
                    Entity entity = it.next();
                    if (entity.getPassengers().size() == 0) {
                        entity.remove();
                        it.remove();
                    }
                }
            }
        }, 10, 20);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(ChatColor.GREEN + "Use " + ChatColor.WHITE + "/broom"
                + ChatColor.GREEN + " to mount a flying broom");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("broom")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command may only be used from in-game");
                return true;
            }

            Player player = (Player) sender;
            ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class, new Consumer<ArmorStand>() {
                @Override
                public void accept(ArmorStand armorStand) {
                    armorStand.setVisible(false);
                    armorStand.setHelmet(new ItemStack(Material.WOODEN_SHOVEL));
                    armorStand.setPersistent(false);
                }
            });
            armorStand.addPassenger(player);
            armorStand.setVelocity(new Vector(0, 2, 0));
            cleanupEntities.add(armorStand);
            player.sendMessage(ChatColor.AQUA + "WASD + Space to move");

            return true;
        }
        return false;
    }

    @EventHandler
    public void onVehicleSteer(PlayerSteerVehicleEvent event) {
        Player player = event.getPlayer();
        Entity vehicle = player.getVehicle();
        if (vehicle == null || !(vehicle instanceof ArmorStand)) return;

        Vector playerDirection = player.getLocation().getDirection();
        Vector moveDirection = new Vector();

        // Use look direction for forward/backward movement
        if (event.getForwardMovement() < 0) {
            moveDirection = playerDirection.clone().multiply(-1);
        } else if (event.getForwardMovement() > 0) {
            moveDirection = playerDirection.clone();
        }

        // Add in left or right hand movement if strafing
        Vector strafeDirection = playerDirection.clone();
        strafeDirection.setY(0);
        if (event.getStrafeMovement() < 0) {
            // Negative strafe movement means to the right
            double strafeZ = strafeDirection.getZ();
            strafeDirection.setZ(strafeDirection.getX());
            strafeDirection.setX(-strafeZ);
            moveDirection.add(strafeDirection);

        } else if (event.getStrafeMovement() > 0) {
            // Positive strafe movement means to the right
            double strafeZ = strafeDirection.getZ();
            strafeDirection.setZ(-strafeDirection.getX());
            strafeDirection.setX(strafeZ);
            moveDirection.add(strafeDirection);
        }

        if (event.isJumping()) {
            moveDirection.setY(1);
        }

        vehicle.setVelocity(moveDirection);
    }
}

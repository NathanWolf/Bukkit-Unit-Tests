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

            return true;
        }
        return false;
    }

    @EventHandler
    public void onVehicleSteer(PlayerSteerVehicleEvent event) {
        Player player = event.getPlayer();
        Entity vehicle = player.getVehicle();
        if (vehicle == null || !(vehicle instanceof ArmorStand)) return;

        Vector direction = new Vector(event.getStrafeMovement(), 0, event.getForwardMovement());
        direction = rotateVector(direction, player.getLocation().getYaw(), player.getLocation().getPitch());
        getLogger().info("Direction: " + direction + " / " + event.isJumping());
        if (event.isJumping()) {
            direction.setY(1);
        }

        vehicle.setVelocity(direction);
    }

    // Convert a relative vector to world coordinates
    public static final Vector rotateVector(Vector v, double yawDegrees, double pitchDegrees) {
        double yaw = Math.toRadians(-yawDegrees);
        double pitch = Math.toRadians(-pitchDegrees);

        double cosYaw = Math.cos(yaw);
        double cosPitch = Math.cos(pitch);
        double sinYaw = Math.sin(yaw);
        double sinPitch = Math.sin(pitch);

        double initialX, initialY, initialZ;
        double x, y, z;

        // Y Axis rotation (Pitch)
        initialX = v.getX();
        initialY = v.getY();
        x = initialX * cosPitch - initialY * sinPitch;
        y = initialX * sinPitch + initialY * cosPitch;

        // X/Z Axis rotation (Yaw)
        initialZ = v.getZ();
        initialX = x;
        z = initialZ * cosYaw - initialX * sinYaw;
        x = initialZ * sinYaw + initialX * cosYaw;

        return new Vector(x, y, z);
    }
}

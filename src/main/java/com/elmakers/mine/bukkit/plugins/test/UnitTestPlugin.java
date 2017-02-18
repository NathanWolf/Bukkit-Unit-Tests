package com.elmakers.mine.bukkit.plugins.test;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class UnitTestPlugin extends JavaPlugin implements CommandExecutor, TabExecutor
{
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    public void onEnable()
	{
        getCommand("lockslot").setExecutor(this);
        getCommand("unlockslot").setExecutor(this);
        getCommand("spawnstand").setExecutor(this);
        getCommand("lockslot").setTabCompleter(this);
        getCommand("unlockslot").setTabCompleter(this);
	}

	public void onDisable()
    {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sendError(sender, "This command may only be used in-game");
            return true;
        }

        Player player = (Player)sender;
        if (command.getName().equals("spawnstand")) {
            Entity newArmorStand = player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
            if (newArmorStand instanceof ArmorStand) {
                ((ArmorStand)newArmorStand).setArms(true);
            }
            return true;
        }

        ArmorStand armorStand = null;
        double angleToArmorStand = 0;

        Location playerLocation = player.getEyeLocation();
        Vector playerDirection = playerLocation.getDirection();
        Collection<Entity> nearbyEntities = player.getNearbyEntities(5, 5, 5);
        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof ArmorStand)) continue;
            Location armorStandLocation = entity.getLocation();
            Vector directionTo = new Vector(armorStandLocation.getX() - playerLocation.getX(), armorStandLocation.getY() - playerLocation.getY(), armorStandLocation.getZ() - playerLocation.getZ());
            double angleTo = directionTo.angle(playerDirection);
            if (armorStand == null || angleTo < angleToArmorStand) {
                armorStand = (ArmorStand)entity;
            }
        }

        if (armorStand == null) {
            sendError(sender, "Please move closer to an armor stand");
            return true;
        }

        if (args.length < 1) {
            EnumSet<ArmorStand.Slot> allSlots = EnumSet.allOf(ArmorStand.Slot.class);
            if (command.getName().equals("lockslot")) {
                armorStand.setLocked(allSlots, true);
                sendMessage(sender, "Locked all slots");
            } else if (command.getName().equals("unlockslot")) {
                armorStand.setLocked(allSlots, false);
                sendMessage(sender, "Unlocked all slots");
            }
            return true;
        }

        ArmorStand.Slot slot;
        try {
            slot = ArmorStand.Slot.valueOf(args[0].toUpperCase());
        } catch (Exception ex) {
            sendError(sender, args[0] + " is not a valid slot name. Try tab completion!");
            return true;
        }
        if (command.getName().equals("lockslot")) {
            if (armorStand.isLocked(slot)) {
                sendError(sender, "That slot is already locked");
            } else {
                armorStand.setLocked(slot, true);
                sendMessage(sender, "Locked the slot");
            }
        } else if (command.getName().equals("unlockslot")) {
            if (!armorStand.isLocked(slot)) {
                sendError(sender, "That slot was not locked");
            } else {
                armorStand.setLocked(slot, false);
                sendMessage(sender, "Unlocked the slot");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new ArrayList<String>();
        String completeCommand = args.length > 0 ? args[args.length - 1] : "";
        completeCommand = completeCommand.toLowerCase();
        for (ArmorStand.Slot slot : ArmorStand.Slot.values()) {
            String slotName = slot.name().toLowerCase();
            if (slotName.startsWith(completeCommand)) {
                options.add(slotName);
            }
        }
        return options;
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

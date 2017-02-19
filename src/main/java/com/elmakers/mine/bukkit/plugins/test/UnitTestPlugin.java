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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class UnitTestPlugin extends JavaPlugin implements CommandExecutor, TabExecutor
{
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    private enum SlotAction {
        REMOVE,
        REPLACE,
        PLACE
    };

    private enum SlotState {
        ENABLED,
        DISABLED
    };

    public void onEnable()
	{
        getCommand("setslot").setExecutor(this);
        getCommand("spawnstand").setExecutor(this);
        getCommand("setslot").setTabCompleter(this);
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

        if (args.length < 3) {
            return false;
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

        EquipmentSlot slot;
        SlotAction action;
        SlotState state;
        try {
            slot = EquipmentSlot.valueOf(args[0].toUpperCase());
        } catch (Exception ex) {
            sendError(sender, args[0] + " is not a valid slot name. Try tab completion!");
            return true;
        }
        try {
            action = SlotAction.valueOf(args[1].toUpperCase());
        } catch (Exception ex) {
            sendError(sender, args[1] + " is not a valid action. Try tab completion!");
            return true;
        }
        try {
            state = SlotState.valueOf(args[2].toUpperCase());
        } catch (Exception ex) {
            sendError(sender, args[2] + " is not a valid state. Try tab completion!");
            return true;
        }

        boolean isDisable = (state == SlotState.DISABLED);
        String stateDescription = state.name().toLowerCase();
        String slotDescription = slot.name().toLowerCase();
        String actionDescription = action.name().toLowerCase();

        boolean success = false;
        switch (action) {
            case REPLACE:
                if (armorStand.isReplaceDisabled(slot) != isDisable) {
                    success = true;
                    armorStand.setReplaceDisabled(slot, isDisable);
                }
                break;
            case PLACE:
                if (armorStand.isPlaceDisabled(slot) != isDisable) {
                    success = true;
                    armorStand.setPlaceDisabled(slot, isDisable);
                }
                break;
            case REMOVE:
                if (armorStand.isRemoveDisabled(slot) != isDisable) {
                    success = true;
                    armorStand.setRemoveDisabled(slot, isDisable);
                }
                break;
        }
        if (success) {
            sendMessage(sender, "The " + slotDescription + " slot now has " + actionDescription + " " + stateDescription);
        } else {
            sendError(sender, "The " + slotDescription + " slot already has " + actionDescription + " " + stateDescription);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new LinkedList<String>();
        String partialParameter = args.length > 0 ? args[args.length - 1] : "";
        partialParameter = partialParameter.toLowerCase();

        if (args.length > 2) {
            // On enabled/disabled
            for (SlotState state : SlotState.values()) {
                options.add(state.name().toLowerCase());
            }
        } else if (args.length > 1) {
            // On action
            for (SlotAction action : SlotAction.values()) {
                options.add(action.name().toLowerCase());
            }
        } else {
            // On slot
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                options.add(slot.name().toLowerCase());
            }
        }

        Iterator<String> filterOptions = options.iterator();
        while (filterOptions.hasNext()) {
            String option = filterOptions.next();
            if (!option.toLowerCase().startsWith(partialParameter)) {
                filterOptions.remove();
            }
        }
        Collections.sort(options);
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

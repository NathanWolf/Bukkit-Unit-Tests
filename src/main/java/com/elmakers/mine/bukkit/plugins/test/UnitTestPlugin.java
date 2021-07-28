package com.elmakers.mine.bukkit.plugins.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.google.common.collect.ImmutableSet;

public class UnitTestPlugin extends JavaPlugin implements Listener, TabExecutor {
    enum Mode {
        ALLOW,
        JUMP,
        TELEPORT,
        PREVENT
    };
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;
    private static final Set<Material> TRANSPARENT = ImmutableSet.of(Material.AIR, Material.CAVE_AIR, Material.LIGHT, Material.VOID_AIR);
    private static final int MAX_RANGE = 16;

    private Mode currentMode = Mode.PREVENT;

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
    }

    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This plugin may only be used in-game");
            return true;
        }
        if (command.getName().equals("makeportal")) {
            onEndPortal((Player)sender);
        }
        if (command.getName().equals("newworld")) {
            onNewWorld((Player)sender);
        }
        if (command.getName().equals("setportal")) {
            onSetPortal((Player)sender, args);
        }
        return true;
    }

    public void onSetPortal(Player player, String[] args) {
        Mode newMode;
        if (args.length > 0) {
            try {
                newMode = Mode.valueOf(args[0].toUpperCase());
            } catch (Exception ex) {
                sendError(player, "Invalid mode: " + args[0]);
                return;
            }
        } else {
            newMode = Mode.values()[(currentMode.ordinal() + 1) % Mode.values().length];
        }
        currentMode = newMode;
        sendMessage(player, "Changed mode to " + ChatColor.DARK_AQUA + currentMode);
    }

    public void onEndPortal(Player player) {
        Block target = player.getTargetBlock(TRANSPARENT, MAX_RANGE);
        if (target == null) {
            sendError(player, "No target block");
            return;
        }
        target.setType(Material.END_PORTAL);
    }

    public void onNewWorld(Player player) {
        String worldName = "world_new";
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage(ChatColor.YELLOW + "World " + ChatColor.DARK_AQUA + worldName + ChatColor.YELLOW + " not found, creating/loading now, may lag a bit");
            world = Bukkit.createWorld(new WorldCreator(worldName));
            if (world == null) {
                sendError(player, "Could not create world!");
                return;
            }
        }
        int highest = world.getHighestBlockYAt(0, 0);
        Location targetLocation = new Location(world, 0, highest + 1, 0);
        sendMessage(player, "Teleporting you to " + ChatColor.DARK_AQUA + worldName);
        player.teleport(targetLocation);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            switch (currentMode) {
                case ALLOW:
                    break;
                case TELEPORT:
                    sendMessage(player, "Preventing end portal but teleporting you up 20 blocks");
                    loc.setY(loc.getY() + 20);
                    player.teleport(loc);
                    event.setCancelled(true);
                    break;
                case JUMP:
                    sendMessage(player, "Preventing end portal but applying upward velocity");
                    player.setVelocity(new Vector(0, 5, 0));
                    event.setCancelled(true);
                    break;
                case PREVENT:
                    sendMessage(player, "Preventing end portal, use " + ChatColor.GRAY + "/setportal" + CHAT_PREFIX + " to change this behavior");
                    event.setCancelled(true);
                    break;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> options = new ArrayList<>();
        if (command.getName().equals("setportal")) {
            for (Mode mode : Mode.values()) {
                options.add(mode.name().toLowerCase());
            }
        }
        return options;
    }

    protected void sendMessage(CommandSender sender, String string) {
        sender.sendMessage(CHAT_PREFIX + string);
    }

    protected void sendError(CommandSender sender, String string) {
        sender.sendMessage(ERROR_PREFIX + string);
    }
}

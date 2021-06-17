package com.elmakers.mine.bukkit.plugins.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UnitTestPlugin extends JavaPlugin implements Listener, TabExecutor {

    private Map<EntityType, Integer> loadedEntities = new HashMap<>();
    private Map<EntityType, Integer> spawnedEntities = new HashMap<>();
    private int chunkLoads = 0;
    private int emptyChunkLoads = 0;
    private int persistentEntityLoads = 0;
    private int entityLoads = 0;
    private int persistentEntitySpawns = 0;
    private int entitySpawns = 0;

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        getCommand("checkentities").setExecutor(this);
        getCommand("checkentities").setTabCompleter(this);
        getCommand("spawnpersistent").setExecutor(this);
        getCommand("spawnpersistent").setTabCompleter(this);
    }

    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName();
        switch (commandName) {
            case "checkentities":
                return checkEntities(sender, args);
            case "spawnpersistent":
                return spawnPersistent(sender, args);
            default:
                return false;
        }
    }

    private boolean checkEntities(CommandSender sender, String[] args) {
        if (chunkLoads > 0) {
            sender.sendMessage(ChatColor.AQUA + "Mobs spawned in "
                + ChatColor.YELLOW + chunkLoads + ChatColor.AQUA + " chunk loads ("
                + ChatColor.RED + emptyChunkLoads + ChatColor.AQUA + " empty)");
            printEntities(sender);
        }
        reset();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                trackEntity(entity);
            }
        }
        sender.sendMessage(ChatColor.AQUA + "Current entities:");
        printEntities(sender);
        reset();

        return true;
    }

    private void printEntities(CommandSender sender) {
        sender.sendMessage(" " + ChatColor.AQUA + "Persistent"
                + ChatColor.WHITE + ": " + ChatColor.BLUE + persistentEntityLoads
                + ChatColor.WHITE + " / " + ChatColor.BLUE + entityLoads);
        for (Map.Entry<EntityType, Integer> entry : loadedEntities.entrySet()) {
            sender.sendMessage(" " + ChatColor.GREEN + entry.getKey().name()
                    + ChatColor.WHITE + ": " + ChatColor.BLUE + entry.getValue());
        }
    }

    private void reset() {
        chunkLoads = 0;
        persistentEntityLoads = 0;
        emptyChunkLoads = 0;
        loadedEntities.clear();
    }

    private boolean spawnPersistent(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command may only be used in-game");
            return true;
        }
        Location location = ((Player)sender).getLocation();
        Entity villager = location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setPersistent(true);
        sender.sendMessage(ChatColor.DARK_AQUA + "Spawned a persistent villager at your location");
        return true;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        chunkLoads++;
        Entity[] entities = event.getChunk().getEntities();
        if (entities.length == 0) {
            emptyChunkLoads++;
        } else {
            for (Entity entity : entities) {
                trackEntity(entity);
            }
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {

    }

    private void trackEntity(Entity entity) {
        entityLoads++;
        Integer current = loadedEntities.get(entity.getType());
        if (current == null) {
            current = 0;
        }
        loadedEntities.put(entity.getType(), current + 1);
        if (!(entity instanceof Player) && entity.isPersistent()) {
            persistentEntityLoads++;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}

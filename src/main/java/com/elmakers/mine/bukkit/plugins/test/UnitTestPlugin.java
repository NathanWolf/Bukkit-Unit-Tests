package com.elmakers.mine.bukkit.plugins.test;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

import java.util.*;

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

    protected void sendMessage(CommandSender sender, String string)
    {
        sender.sendMessage(CHAT_PREFIX + string);
    }

    protected void sendError(CommandSender sender, String string)
    {
        sender.sendMessage(ERROR_PREFIX + string);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        sendMessage(player, "Swing a special item to test " + ChatColor.GOLD + "Location.getNearbyEntities");
        PlayerInventory inventory = player.getInventory();
        if (!inventory.contains(Material.STICK)) {
            sendMessage(player, " A stick will check within 8 blocks of your target");
            inventory.addItem(new ItemStack(Material.STICK, 1));
        }
        if (!inventory.contains(Material.BLAZE_ROD)) {
            sendMessage(player, " A blaze rod will check within 64 blocks of your target");
            inventory.addItem(new ItemStack(Material.BLAZE_ROD, 1));
        }
        if (!inventory.contains(Material.WOOD_SWORD)) {
            sendMessage(player, " A wood sword will check for LivingEntity classes only");
            inventory.addItem(new ItemStack(Material.WOOD_SWORD, 1));
        }
        if (!inventory.contains(Material.WOOD_HOE)) {
            sendMessage(player, " A wood hoe will check for Item and Player classes only");
            inventory.addItem(new ItemStack(Material.WOOD_HOE, 1));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack inHand = player.getItemInHand();
        if (inHand == null) {
            return;
        }
        if (inHand.getType() != Material.STICK && inHand.getType() != Material.BLAZE_ROD
         && inHand.getType() != Material.WOOD_SWORD && inHand.getType() != Material.WOOD_HOE) {
            return;
        }

        int range = inHand.getType() == Material.STICK ? 8 : 64;

        Block targetBlock = null;
        BlockIterator targeting = new BlockIterator(player);
        while (targeting.hasNext()) {
            Block block = targeting.next();
            if (block != null && block.getType() != Material.AIR) {
                targetBlock = block;
                break;
            }
        }

        if (targetBlock == null) {
            sendError(player, "No target!");
        } else {
            World world = targetBlock.getWorld();
            world.playEffect(targetBlock.getRelative(BlockFace.UP).getLocation(), Effect.ENDER_SIGNAL, 0);

            Location target = targetBlock.getLocation();
            Collection<Entity> entities = null;

            if (inHand.getType() == Material.WOOD_SWORD) {
                entities = new ArrayList<Entity>();
                Collection<LivingEntity> li = world.getEntitiesByClass(target, range, range, range, LivingEntity.class);
                entities.addAll(li);
            } else if (inHand.getType() == Material.WOOD_HOE) {
                entities = world.getEntitiesByClasses(target, range, range, range, Player.class, Item.class);
            } else {
                entities = target.getNearbyEntities(range, range, range);
            }

            Map<EntityType, Integer> entityCounts = new HashMap<EntityType, Integer>();

            for (Entity entity : entities) {
                EntityType entityType = entity.getType();
                if (entityCounts.containsKey(entityType)) {
                    entityCounts.put(entityType, entityCounts.get(entityType) + 1);
                } else {
                    entityCounts.put(entityType, 1);
                }
            }

            String message = "Entities within " + ChatColor.GOLD + range + CHAT_PREFIX +
                    " blocks of " + ChatColor.GOLD + target.toVector() + ":";

            sendMessage(player, message);
            if (entityCounts.size() == 0) {
                sendMessage(player, "None.");
            } else {
                for (Map.Entry<EntityType, Integer> typeCount : entityCounts.entrySet()) {
                    sendMessage(player, typeCount.getKey().name() + ChatColor.GRAY + " : " + ChatColor.YELLOW + typeCount.getValue());
                }
            }
        }
    }
}

package com.elmakers.mine.bukkit.plugins.test;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class UnitTestPlugin extends JavaPlugin implements Listener
{
    @Override
    public void onEnable()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
	}

	@Override
	public void onDisable()
    {

    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        event.getPlayer().sendMessage(ChatColor.GREEN + "Use " + ChatColor.WHITE + "/soup"
        + ChatColor.GREEN + " to get some delicious stick soup. Right-click to eat.");
        event.getPlayer().sendMessage("If you eat it while in creative mode and looking at air, you will see an error in the console");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("soup")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command may only be used from in-game");
                return true;
            }

            Player player = (Player)sender;
            player.getInventory().addItem(new ItemStack(Material.STICK));
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() != Material.STICK) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60, 2));
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    }
}

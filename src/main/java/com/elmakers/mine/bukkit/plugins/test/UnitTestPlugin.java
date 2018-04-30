package com.elmakers.mine.bukkit.plugins.test;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;

public class UnitTestPlugin extends JavaPlugin implements Listener
{
    private static final NumberFormat[] formatters = {
        new DecimalFormat("#0"),
        new DecimalFormat("#0.0"),
        new DecimalFormat("#0.00"),
        new DecimalFormat("#0.000")
    };

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
        event.getPlayer().sendMessage(ChatColor.GREEN + "Use " + ChatColor.WHITE + "/bow"
        + ChatColor.GREEN + " to get a bow and arrows");
        event.getPlayer().sendMessage(ChatColor.GREEN + "Use " + ChatColor.WHITE + "/launch <projectile>"
        + ChatColor.GREEN + " to use the launchProjectile API");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("launch")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command may only be used from in-game");
                return true;
            }

            Player player = (Player)sender;
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.WHITE + "/launch <projectile>");
                return true;
            }
            try {
                Class<? extends Projectile> projectileClass = (Class<? extends Projectile>)Class.forName("org.bukkit.entity." + args[0]);
                player.launchProjectile(projectileClass);
            } catch (Exception ex) {
                player.sendMessage(ChatColor.RED + "Invalid projectile type: " + ChatColor.WHITE + args[0]);
                player.sendMessage("  Try: TippedArrow, LargeFireball, SmallFireball, WitherSkull");
                getLogger().log(Level.WARNING, "Error launching projectile", ex);
            }

            return true;
        } else if (command.getName().equals("bow")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command may only be used from in-game");
                return true;
            }

            Player player = (Player)sender;
            player.getInventory().addItem(new ItemStack(Material.BOW));
            player.getInventory().addItem(new ItemStack(Material.ARROW, 64));

            return true;
        }
        return false;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource source = projectile.getShooter();
        if (!(source instanceof Player)) return;

        Player player = (Player)source;
        player.sendMessage(ChatColor.AQUA + "Projectile (" + ChatColor.DARK_AQUA +
            projectile.getType() + ChatColor.AQUA + ")");
        player.sendMessage(ChatColor.AQUA + "shot from   " +
            printVector(player.getLocation().getDirection()));
        player.sendMessage(ChatColor.AQUA + " hit facing " +
            printVector(projectile.getLocation().getDirection()));
        player.sendMessage(ChatColor.AQUA + " at speed   " +
            printVector(projectile.getVelocity().normalize()));
    }


    public static String printVector(Vector vector) {
        return printVector(vector, 3);
    }

    public static String printVector(Vector vector, int digits) {
        NumberFormat formatter = formatters[Math.min(Math.max(0, digits), formatters.length - 1)];
        return "" + ChatColor.BLUE + formatter.format(vector.getX()) + ChatColor.GRAY + "," +
            ChatColor.BLUE + formatter.format(vector.getY()) + ChatColor.GRAY + "," +
            ChatColor.BLUE + formatter.format(vector.getZ());
    }
}

package com.elmakers.mine.bukkit.plugins.test;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UnitTestPlugin extends JavaPlugin implements Listener {
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    public void onEnable() {
        getCommand("cloud").setExecutor(this);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
    }

    public void onDisable() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendMessage(event.getPlayer(), "Use /cloud to spawn an AOE Cloud with redstone particles");
        sendMessage(event.getPlayer(), "Use /cloud <particle> to test different particles");
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("cloud")) {
            if (!(sender instanceof Player)) {
                sendError(sender, "This command may only be used in-game");
                return true;
            }
            Player player = (Player)sender;
            Particle particle = Particle.REDSTONE;
            if (args.length > 0) {
                try {
                    particle = Particle.valueOf(args[0].toUpperCase());
                } catch (Exception ex) {
                    sendError(sender, "Unknown particle type: " + args[0]);
                }
            }

            Entity entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.AREA_EFFECT_CLOUD);
            if (entity == null || !(entity instanceof AreaEffectCloud)) {
                sendError(sender, "Something went wrong trying to spawn the cloud.");
                return false;
            }
            AreaEffectCloud cloud = (AreaEffectCloud)entity;
            try {
                cloud.setParticle(particle);
                sendMessage(sender, "Spawned AreaEffectCloud with particle " + particle.name());
            } catch (Exception ex) {
                sendError(sender, ex.getMessage());
                sendMessage(sender, "See logs for full stack trace");
                getLogger().log(Level.SEVERE, "An error occurred setting AreaEffectCloud particle type", ex);
            }
        }
        return true;
    }

    private void sendMessage(CommandSender sender, String string) {
        sender.sendMessage(CHAT_PREFIX + string);
    }

    private void sendError(CommandSender sender, String string) {
        sender.sendMessage(ERROR_PREFIX + string);
    }
}

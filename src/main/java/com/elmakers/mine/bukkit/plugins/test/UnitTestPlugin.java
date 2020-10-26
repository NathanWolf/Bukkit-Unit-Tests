package com.elmakers.mine.bukkit.plugins.test;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public class UnitTestPlugin extends JavaPlugin implements Listener
{
    private static final ChatColor CHAT_PREFIX = ChatColor.AQUA;
    private static final ChatColor ERROR_PREFIX = ChatColor.RED;

    public void onEnable()
    {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        // Register a crafting recipe that lets us turn leather
        // armor into diamond armor
        NamespacedKey recipeKey = new NamespacedKey(this, "irondiamondhelmet");
        ShapedRecipe shapedRecipe = new ShapedRecipe(recipeKey, new ItemStack(Material.DIAMOND_HELMET));
        shapedRecipe.shape("o", "i");
        shapedRecipe.setIngredient('o', Material.DIAMOND);
        List<ItemStack> variants = new ArrayList<>();
        for (short damage = 0; damage < Material.LEATHER_HELMET.getMaxDurability(); damage++) {
            ItemStack variant = new ItemStack(Material.LEATHER_HELMET, 1, damage);
            variants.add(variant);
        }
        RecipeChoice.ExactChoice choice = new RecipeChoice.ExactChoice(variants);
        shapedRecipe.setIngredient('i', choice);
        getServer().addRecipe(shapedRecipe);
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
}

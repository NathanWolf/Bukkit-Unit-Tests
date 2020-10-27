package com.elmakers.mine.bukkit.plugins.test.crafting;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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

    private ItemStack loadedItem;

    public void onEnable()
    {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        // Load a legacy item from a yaml file. If this is done prior to registering the recipe,
        // The recipe ingredients go through the CraftMagicNumbers legacy item translation, which
        // loses the durability values assigned to the ingredients.
        getLogger().info("Loading legacy saved item from yaml");
        getLogger().info("This should trigger the 'Initializing Legacy Material Support' message if it has not appeared already");
        InputStream itemFile = getResource("item.yml");
        YamlConfiguration configuration = new YamlConfiguration();
        String ingredient = null;
        try {
            configuration.load(new InputStreamReader(itemFile, "UTF-8"));
            loadedItem = configuration.getItemStack("totem.item");
            ingredient = configuration.getString("ingredient");
            itemFile.close();
        } catch (Exception exception) {
            loadedItem = null;
            getLogger().log(Level.WARNING, "Could not load item from yaml", exception);
        }
        if (loadedItem != null) {
            getLogger().info("Loaded item from yaml file");
        }

        // Register a crafting recipe that lets us turn leather
        // armor into diamond armor
        Material armorIngredient = ingredient == null ? Material.LEATHER_LEGGINGS : Material.valueOf(ingredient.toUpperCase());
        NamespacedKey recipeKey = new NamespacedKey(this, "irondiamondhelmet");
        ShapedRecipe shapedRecipe = new ShapedRecipe(recipeKey, new ItemStack(Material.DIAMOND_HELMET));
        shapedRecipe.shape("o", "i");
        shapedRecipe.setIngredient('o', Material.DIAMOND);
        List<ItemStack> variants = new ArrayList<>();
        for (short damage = 0; damage < armorIngredient.getMaxDurability(); damage++) {
            ItemStack variant = new ItemStack(armorIngredient, 1, damage);
            variants.add(variant);
        }
        RecipeChoice.ExactChoice choice = new RecipeChoice.ExactChoice(variants);
        shapedRecipe.setIngredient('i', choice);
        getServer().addRecipe(shapedRecipe);
    }

    public void onDisable()
    {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (loadedItem == null) {
            player.sendMessage("Couldn't load item.yml resource");
            return;
        }
        player.getInventory().addItem(loadedItem);
        player.getInventory().addItem(new ItemStack(Material.DIAMOND));
        player.getInventory().addItem(new ItemStack(Material.LEATHER_HELMET, 1));
        player.getInventory().addItem(new ItemStack(Material.LEATHER_HELMET, 1, (short)30));

        player.sendMessage("Note that you should've seen the 'Initializing Legacy Material Support' message in server logs");
        player.sendMessage("Place the diamond above the helmet to craft a diamond helmet");
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

package com.elmakers.mine.bukkit.plugins.test;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.elmakers.mine.bukkit.api.event.PreLoadEvent;

public class MagicAttributesPlugin extends JavaPlugin implements Listener
{
    public void onEnable()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
	}

	public void onDisable()
    {
    }

    @EventHandler
    public void onMagicLoad(PreLoadEvent event) {
        event.getAttributeProviders().add(new TestProvider(this));
    }
}

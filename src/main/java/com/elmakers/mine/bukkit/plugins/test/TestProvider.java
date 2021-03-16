package com.elmakers.mine.bukkit.plugins.test;

import com.elmakers.mine.bukkit.api.attributes.AttributeProvider;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class TestProvider implements AttributeProvider {
    private final Plugin plugin;

    public TestProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Set<String> getAllAttributes() {
        Set<String> attributes = new HashSet<>();
        attributes.add("intelligence");
        attributes.add("strength");
        attributes.add("power");

        plugin.getLogger().info("Registered attributes: " + attributes);
        return attributes;
    }

    @Override
    public Double getAttributeValue(String attribute, Player player) {
        switch(attribute) {
            case "intelligence":
                return 20.0;
            case "strength":
                return 10.0;
            case "power":
                return 5.0;
        }
        return null;
    }
}

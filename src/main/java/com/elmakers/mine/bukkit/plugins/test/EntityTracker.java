package com.elmakers.mine.bukkit.plugins.test;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class EntityTracker {
    private Map<EntityType, Integer> entityCounts = new HashMap<>();
    private int persistentEntityCount = 0;
    private int entityCount = 0;

    public void track(Entity entity) {

    }

    public void reset() {

    }

}

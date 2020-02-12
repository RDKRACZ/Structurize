package com.ldtteam.structurize.pipeline.scan;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Scans entity from world. Entities are gathered and then send to appropriate scanners which can modify them if needed.
 * YOU ARE GIVEN THE WORLD ENTITY.
 * IF YOU WANT TO MODIFY IT CREATE A COPY BEFORE OR ELSE YOU WILL MODIFY THE ORIGINAL ENTITY IN WORLD
 *
 * @param <T> entity type
 */
public abstract class EntityScanner<T extends Entity> extends ForgeRegistryEntry<EntityScanner<T>>
{
    /**
     * Can modify given entity.
     *
     * @param entity world entity, CREATE A COPY BEFORE MODIFYING
     * @param world  world from which was entity taken
     * @return modified or the same entity or null (null means don't scan this entity)
     */
    public abstract T scan(T entity, World world);
}

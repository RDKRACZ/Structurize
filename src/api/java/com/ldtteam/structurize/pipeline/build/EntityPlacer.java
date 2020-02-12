package com.ldtteam.structurize.pipeline.build;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Used for placing entity into world.
 *
 * @param <T> entity type
 */
public abstract class EntityPlacer<T extends Entity> extends ForgeRegistryEntry<EntityPlacer<T>>
{
    /**
     * Places entity into given world at given pos.
     *
     * @param entity entity to place
     * @param world  world to place into
     */
    public abstract void place(T entity, World world);

    /**
     * Getter for all needed items to place this entity.
     * Returning null means there are no required items.
     *
     * @param entity entity to place
     * @param world  world to place into
     * @return a list of required items or null if there are no required items
     */
    public abstract List<ItemStack> getRequirements(T entity, World world);
}

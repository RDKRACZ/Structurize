package com.ldtteam.structurize.world.schematic;

import java.util.function.BiFunction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ModDimension;

/**
 * Adds dimension factory to dimension manager
 */
public class SchematicModDimension extends ModDimension
{
    public SchematicModDimension()
    {
        setRegistryName("schematic_dim_type");
    }

    @Override
    public BiFunction<World, DimensionType, ? extends Dimension> getFactory()
    {
        return SchematicDimension::new;
    }
}

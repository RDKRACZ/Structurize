package com.ldtteam.structurize.world;

import com.ldtteam.structurize.util.Utils;
import com.ldtteam.structurize.world.schematic.SchematicModDimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.registries.IForgeRegistry;

public class ModDimensions
{
    public static final SchematicModDimension SCHEMATIC_MOD_DIMENSION = new SchematicModDimension();
    public static DimensionType SCHEMATIC_DIMENSION_TYPE;

    public static void registerDimensionTypes()
    {
        if (SCHEMATIC_DIMENSION_TYPE == null)
        {
            SCHEMATIC_DIMENSION_TYPE = DimensionManager.registerDimension(Utils.createLocationFor("schematic_dim"), SCHEMATIC_MOD_DIMENSION, null, true);
        }
    }

    public static void registerModDimensions(final IForgeRegistry<ModDimension> registry)
    {
        registry.registerAll(SCHEMATIC_MOD_DIMENSION);
    }
}

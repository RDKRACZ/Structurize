package com.ldtteam.structurize.world.schematic;

import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.world.ModDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameType;
import net.minecraft.world.dimension.DimensionType;

/**
 * Class for handling events created by player actions
 */
public class PlayerEvents
{
    public static void onPlayerJoinedSchematicWorldType(final PlayerEntity player)
    {
        if (!canEntityTravelToDimension(player, ModDimensions.SCHEMATIC_DIMENSION_TYPE))
        {
            return;
        }

        player.setGameType(GameType.CREATIVE);
        player.changeDimension(ModDimensions.SCHEMATIC_DIMENSION_TYPE);
    }

    public static boolean canEntityTravelToDimension(final Entity entity, final DimensionType targetDimension)
    {
        return (!isSchematicWorldType(entity) && !isSchematiDimensionType(targetDimension)) ||
            (isSchematiDimensionType(targetDimension) && canEntityJoinSchematicDimension(entity));
    }

    public static boolean canEntityJoinSchematicDimension(final Entity entity)
    {
        return entity instanceof PlayerEntity && isSchematicWorldType(entity) && canEntityTeleport(entity);
    }

    private static boolean isSchematiDimensionType(final DimensionType dimensionType)
    {
        return dimensionType == ModDimensions.SCHEMATIC_DIMENSION_TYPE;
    }

    private static boolean isSchematicWorldType(final Entity entity)
    {
        return entity.getEntityWorld().getWorldType() == Instances.getSchematicWorldType();
    }

    private static boolean canEntityTeleport(final Entity entity)
    {
        // use vanilla portal logic from BlockPortal#onEntityCollision
        return !entity.isPassenger() && !entity.isBeingRidden() && entity.isNonBoss();
    }
}
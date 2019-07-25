package com.ldtteam.structurize.client.gui;

import com.ldtteam.structurize.client.render.StructureRenderer;
import com.ldtteam.structurize.item.BuildTool;
import com.ldtteam.structurize.structure.PlaceEventInfoHolder;
import com.ldtteam.structurize.structure.providers.BlueprintStructureProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Window for {@link BuildTool}
 */
public class WindowBuildTool
{
    private static PlaceEventInfoHolder<BlueprintStructureProvider> eventReference;

    public static void open(final PlayerEntity player)
    {
        open(player.getPosition(), player);
    }

    public static void open(final BlockPos targetedPos, final PlayerEntity player)
    {
        eventReference = PlaceEventInfoHolder.createBlueprintEvent(targetedPos, player.getEntityWorld());
        StructureRenderer.setActiveBuildToolEvent(eventReference);
    }

    public static void close()
    {
        eventReference = null;
        StructureRenderer.setActiveBuildToolEvent(null);
    }

    public static PlaceEventInfoHolder<BlueprintStructureProvider> getEvent()
    {
        return eventReference;
    }
}

package com.ldtteam.structurize.client.gui;

import com.ldtteam.structurize.client.render.EventRenderer;
import com.ldtteam.structurize.item.BuildTool;
import com.ldtteam.structurize.pipeline.PlaceEventInfoHolder;
import com.ldtteam.structurize.structure.providers.BlueprintStructureProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Window for {@link BuildTool}
 */
public class WindowBuildTool
{
    private static PlaceEventInfoHolder<BlueprintStructureProvider> eventReference;

    public static void open(final BlockPos targetedPos, final PlayerEntity player)
    {
        eventReference = PlaceEventInfoHolder.createBlueprintEvent(targetedPos, player.getEntityWorld());
        EventRenderer.addActiveEvent(eventReference);
        // TODO: allow multi events (required gui for maintaining), notify player if max events reached
    }

    public static void close()
    {
        eventReference.cancel();
        eventReference = null;
    }

    public static PlaceEventInfoHolder<BlueprintStructureProvider> getEvent()
    {
        eventReference.setRedraw(); // called here cuz action origin is not gui but commands
        return eventReference;
    }
}

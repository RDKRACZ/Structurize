package com.ldtteam.structurize.client.gui;

import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.item.BuildTool;
import com.ldtteam.structurize.pipeline.build.EventInfoHolder;
import com.ldtteam.structurize.structure.providers.BlueprintStructureDataProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Window for {@link BuildTool}
 */
public class WindowBuildTool
{
    private static EventInfoHolder<BlueprintStructureDataProvider> eventReference;

    public static void open(final BlockPos targetedPos, final PlayerEntity player)
    {
        eventReference = EventInfoHolder.createBlueprintEvent(targetedPos, player.getEntityWorld());
        Instances.getEventRenderer().addActiveEvent(eventReference);
        // TODO: allow multi events (required gui for maintaining), notify player if max events reached
    }

    public static void closeAndCancel()
    {
        eventReference.cancel();
        eventReference = null;
    }

    public static void close()
    {
        eventReference = null;
    }

    public static EventInfoHolder<BlueprintStructureDataProvider> getEvent()
    {
        return eventReference;
    }
}

package com.ldtteam.structurize.event;

import com.ldtteam.structurize.client.render.EventRenderer;
import com.ldtteam.structurize.structure.StructureBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class CaliperEvent implements ICaliperEvent
{
    private EventRenderer eventRenderer;
    private final StructureBB structureBB;

    public CaliperEvent(final StructureBB structureBBin)
    {
        structureBB = structureBBin;
    }

    @Override
    public void render(final RenderWorldLastEvent context)
    {
        if (eventRenderer == null)
        {
            eventRenderer = EventRenderer.builder().absolutePos(structureBB::getAnchor, EventRenderer.builder().box(structureBB)).build();
        }
        eventRenderer.render(context);
    }
}

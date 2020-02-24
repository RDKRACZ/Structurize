package com.ldtteam.structurize.event;

import com.ldtteam.structurize.client.render.EventRenderer;
import com.ldtteam.structurize.structure.StructureBB;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.WorldRenderer;

public class CaliperEvent implements ICaliperEvent
{
    private EventRenderer eventRenderer;
    private final StructureBB structureBB;

    public CaliperEvent(final StructureBB structureBBin)
    {
        structureBB = structureBBin;
    }

    @Override
    public void render(final WorldRenderer context, final MatrixStack matrixStack, final float partialTicks)
    {
        if (eventRenderer == null)
        {
            eventRenderer = EventRenderer.builder().absolutePos(structureBB::getAnchor).box(structureBB).absolutePosRestore().build();
        }
        eventRenderer.render(context, matrixStack, partialTicks);
    }
}

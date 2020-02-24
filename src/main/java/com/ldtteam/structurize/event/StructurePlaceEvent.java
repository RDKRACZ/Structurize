package com.ldtteam.structurize.event;

import com.ldtteam.structurize.client.render.EventRenderer;
import com.ldtteam.structurize.structure.Structure;
import com.ldtteam.structurize.structure.StructureBB;
import com.ldtteam.structurize.util.PlacementSettings;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class StructurePlaceEvent implements IStructurePlaceEvent
{
    private final Structure structure;
    private final StructureBB structureBB;
    private PlacementSettings placementSettings;
    private EventRenderer eventRenderer;

    public StructurePlaceEvent(final BlockPos where, final Structure structureIn)
    {
        structure = structureIn;
        structureBB = structureIn.createBoundingBox(where);
        placementSettings = PlacementSettings.A;
    }

    @Override
    public void relativeMove(final BlockPos vector)
    {
        structureBB.moveBy(vector);
    }

    @Override
    public void absoluteMove(final BlockPos pos)
    {
        structureBB.moveBy(pos.subtract(structureBB.getAnchor()));
    }

    @Override
    public void rotate(final Rotation rotation)
    {
        if (rotation == Rotation.NONE)
        {
            return;
        }

        placementSettings = placementSettings.rotate(rotation);
        structureBB.rotate(null, rotation);
        // TODO Auto-generated method stub
    }

    @Override
    public void mirror(final Mirror mirror)
    {
        if (mirror == Mirror.NONE)
        {
            return;
        }

        placementSettings = placementSettings.mirror(mirror);
        structureBB.mirror(null, mirror);
        // TODO Auto-generated method stub
    }

    @Override
    public void render(final WorldRenderer context, final MatrixStack matrixStack, final float partialTicks)
    {
        if (eventRenderer == null)
        {
            eventRenderer = EventRenderer.builder()
                .absolutePos(structureBB::getAnchor)
                .structure(structure, structureBB)
                .box(structureBB)
                .absolutePosRestore()
                .build();
        }
        eventRenderer.render(context, matrixStack, partialTicks);
    }
}

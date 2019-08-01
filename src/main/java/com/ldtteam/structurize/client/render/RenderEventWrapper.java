package com.ldtteam.structurize.client.render;

import com.ldtteam.structurize.pipeline.PlaceEventInfoHolder;
import com.ldtteam.structurize.structure.providers.IStructureDataProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public class RenderEventWrapper<T extends IStructureDataProvider, U extends PlaceEventInfoHolder<T>>
{
    private boolean shouldRedraw = false;
    private final U event;

    public RenderEventWrapper(final U event)
    {
        this.event = event;
    }

    /**
     * Rotates structure clockwise.
     */
    public void rotateClockwise()
    {
        event.getPosition().rotateCW(event.getStructure().getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
        event.getStructure().applyMirrorRotationOnStructure(Rotation.CLOCKWISE_90, Mirror.NONE);
        setRedraw();
    }

    /**
     * Rotates structure counterclockwise.
     */
    public void rotateCounterClockwise()
    {
        event.getPosition().rotateCCW(event.getStructure().getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
        event.getStructure().applyMirrorRotationOnStructure(Rotation.COUNTERCLOCKWISE_90, Mirror.NONE);
        setRedraw();
    }

    /**
     * Mirrors structure through YZ plane according to current rotation.
     * Applied before rotation.
     */
    public void mirrorX()
    {
        event.getPosition().mirrorX(event.getStructure().getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
        event.getStructure().applyMirrorRotationOnStructure(Rotation.NONE, Mirror.FRONT_BACK);
        setRedraw();
    }

    /**
     * Mirrors structure through XY plane according to current rotation.
     * Applied before rotation.
     */
    public void mirrorZ()
    {
        event.getPosition().mirrorZ(event.getStructure().getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
        event.getStructure().applyMirrorRotationOnStructure(Rotation.NONE, Mirror.LEFT_RIGHT);
        setRedraw();
    }

    public void setRedraw()
    {
        shouldRedraw = true;
    }

    public boolean shouldRedraw()
    {
        final boolean result = shouldRedraw;
        shouldRedraw = false;
        return result;
    }

    public U getEvent()
    {
        return event;
    }
}
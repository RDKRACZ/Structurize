package com.ldtteam.structurize.client.render;

import com.ldtteam.structurize.pipeline.PlaceEventInfoHolder;
import com.ldtteam.structurize.structure.providers.IStructureDataProvider;
import net.minecraft.util.Rotation;

public class RenderEventWrapper<T extends IStructureDataProvider, U extends PlaceEventInfoHolder<T>>
{
    private Rotation rotation = Rotation.NONE;
    private boolean mirror = false;
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
        rotation = rotation.add(Rotation.CLOCKWISE_90);
        event.getPosition().rotateCW(event.getStructure().getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
    }

    /**
     * Rotates structure counterclockwise.
     */
    public void rotateCounterClockwise()
    {
        rotation = rotation.add(Rotation.COUNTERCLOCKWISE_90);
        event.getPosition().rotateCCW(event.getStructure().getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
    }

    /**
     * Getter for unapplied rotation.
     *
     * @return current rotation
     * @see #rotateClockwise()
     * @see #rotateCounterClockwise()
     * @see #applyMirrorRotationOnStructure()
     */
    public Rotation getRotation()
    {
        return rotation;
    }

    /**
     * Mirrors structure through XY|YZ plane according to current rotation.
     * Applied before rotation.
     */
    public void mirror()
    {
        mirror = !mirror;
        if (rotation.equals(Rotation.NONE) || rotation.equals(Rotation.CLOCKWISE_180))
        {
            event.getPosition().mirrorX(event.getStructure().getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
        }
        else
        {
            event.getPosition().mirrorZ(event.getStructure().getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
        }
    }

    /**
     * Getter for unapplied mirror.
     *
     * @return current mirror
     * @see #mirror()
     * @see #applyMirrorRotationOnStructure()
     */
    public boolean isMirrored()
    {
        return mirror;
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
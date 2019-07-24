package com.ldtteam.structurize.structure.providers;

import net.minecraft.world.World;

/**
 * Interface for providing structure for {@link PlaceEventInfoHolder}
 */
public interface IStructureDataProvider
{
    /**
     * Rotates structure clockwise.
     */
    void rotateClockwise();

    /**
     * Rotates structure counterclockwise.
     */
    void rotateCounterClockwise();

    /**
     * Mirrors structure through X axis.
     */
    void mirrorX();

    /**
     * Mirrors structure through Z axis.
     */
    void mirrorZ();

    /**
     * Getter for X block size.
     *
     * @return X block size
     */
    int getXsize();

    /**
     * Getter for Y block size.
     *
     * @return Y block size
     */
    int getYsize();

    /**
     * Getter for Z block size.
     *
     * @return Z block size
     */
    int getZsize();
}

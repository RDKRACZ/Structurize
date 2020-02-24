package com.ldtteam.structurize.structure;

import com.ldtteam.structurize.util.CubePosIterator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * Structure bounding box.
 * Anchor has every coord smaller or same than peek blockpos.
 */
interface IStructureBB
{
    /**
     * Anchor has every coord smaller or same than peek.
     * Smallest possible blockpos of BB.
     *
     * @return anchor blockpos
     */
    BlockPos getAnchor();

    /**
     * Peek has every coord bigger or same than anchor.
     * Biggest possible blockpos of BB.
     *
     * @return peek blockpos
     */
    BlockPos getPeek();

    /**
     * Zero based peek is blockpos made from sizes.
     *
     * @return zero based peek blockpos
     */
    default BlockPos getZeroBasedPeek()
    {
        return new BlockPos(getXSize() - 1, getYSize() - 1, getZSize() - 1);
    }

    /**
     * Always positive or zero x size.
     *
     * @return X width
     */
    int getXSize();

    /**
     * Always positive or zero y size.
     *
     * @return Y height
     */
    int getYSize();

    /**
     * Always positive or zero z size.
     *
     * @return Z width
     */
    int getZSize();

    /**
     * Getter for structure volume.
     *
     * @return structure volume
     */
    default int getVolume()
    {
        return getXSize() * getYSize() * getZSize();
    }

    /**
     * Converts to MC AABB.
     *
     * @return AABB
     */
    AxisAlignedBB toAABB();

    /**
     * Converts to MC AABB using current sizes.
     *
     * @return AABB
     */
    default AxisAlignedBB toZeroBasedAABB()
    {
        return new AxisAlignedBB(0, 0, 0, getXSize() - 1, getYSize() - 1, getZSize() - 1);
    }

    /**
     * Creates new pos iterator from anchor to peek using y|z|x iteration order (x iterates first).
     *
     * @return pos iterator
     */
    default CubePosIterator getPosIterator()
    {
        return new CubePosIterator(getAnchor(), getPeek());
    }

    /**
     * Creates new pos iterator from 0,0,0 to zero based peek using y|z|x iteration order (x iterates first).
     *
     * @return pos iterator
     */
    default CubePosIterator getZeroBasedPosIterator()
    {
        return new CubePosIterator(BlockPos.ZERO, getZeroBasedPeek());
    }

    /**
     * Adds anchor to given zerobased pos so it becomes real world pos.
     *
     * @param zeroBasedPos pos to transform
     * @return real world pos
     */
    default BlockPos transformZeroBasedToReal(final BlockPos zeroBasedPos)
    {
        return getAnchor().add(zeroBasedPos);
    }
}

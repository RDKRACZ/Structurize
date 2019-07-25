package com.ldtteam.structurize.structure;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * Structure bounding box.
 * Anchor has every coord smaller or same than peek blockpos.
 */
public class StructureBB
{
    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;

    /**
     * Creates new structureBB.
     *
     * @param start any corner
     * @param end   opposite corner against start
     */
    public StructureBB(final BlockPos start, final BlockPos end)
    {
        minX = Math.min(start.getX(), end.getX());
        minY = Math.min(start.getY(), end.getY());
        minZ = Math.min(start.getZ(), end.getZ());
        maxX = Math.max(start.getX(), end.getX());
        maxY = Math.max(start.getY(), end.getY());
        maxZ = Math.max(start.getZ(), end.getZ());
    }

    /**
     * Anchor has every coord smaller or same than peek.
     * Smallest possible blockpos of BB.
     *
     * @return anchor blockpos
     */
    public BlockPos getAnchor()
    {
        return new BlockPos(minX, minY, minZ);
    }

    /**
     * Peek has every coord bigger or same than anchor.
     * Biggest possible blockpos of BB.
     *
     * @return peek blockpos
     */
    public BlockPos getPeek()
    {
        return new BlockPos(maxX, maxY, maxZ);
    }

    /**
     * Always possitive or zero x size.
     *
     * @return X width
     */
    public int getXSize()
    {
        return maxX - minX + 1;
    }

    /**
     * Always possitive or zero y size.
     *
     * @return Y height
     */
    public int getYSize()
    {
        return maxY - minY + 1;
    }

    /**
     * Always possitive or zero z size.
     *
     * @return Z width
     */
    public int getZSize()
    {
        return maxZ - minZ + 1;
    }

    /**
     * Converts to MC AABB.
     *
     * @return AABB
     */
    public AxisAlignedBB toAABB()
    {
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Returns pos iterator from anchor to peek using MC default behaviour.
     *
     * @return pos iterator
     * @see link to MC default behaviour {@link BlockPos#getAllInBoxMutable(int, int, int, int, int, int)
     *      BlockPos.getAllInBoxMutable()}
     */
    public Iterable<BlockPos> getPosIterator()
    {
        return BlockPos.getAllInBoxMutable(minX, minY, minZ, maxX, maxY, maxZ);
        // TODO: needs replacement as it iterates x|y|z while we do y|z|x
    }

    /**
     * Moves BB by given vector.
     *
     * @param vector move vector
     */
    public void moveBy(final BlockPos vector)
    {
        minX += vector.getX();
        minY += vector.getY();
        minZ += vector.getZ();
        maxX += vector.getX();
        maxY += vector.getY();
        maxZ += vector.getZ();
    }
}

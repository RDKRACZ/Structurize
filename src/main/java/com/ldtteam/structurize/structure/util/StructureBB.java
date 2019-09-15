package com.ldtteam.structurize.structure.util;

import com.ldtteam.structurize.util.CubePosIterator;
import org.jetbrains.annotations.NotNull;
import net.minecraft.util.Rotation;
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
     * Zero based peek is blockpos made from sizes.
     *
     * @return zero based peek blockpos
     */
    public BlockPos getZeroBasedPeek()
    {
        return new BlockPos(getXSize() - 1, getYSize() - 1, getZSize() - 1);
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
     * Converts to MC AABB using current sizes.
     *
     * @return AABB
     */
    public AxisAlignedBB toZeroBasedAABB()
    {
        return new AxisAlignedBB(0, 0, 0, getXSize(), getYSize(), getZSize());
    }

    /**
     * Creates new pos iterator from anchor to peek using y|z|x iteration order (x iterates first).
     *
     * @return pos iterator
     */
    public Iterable<BlockPos> getPosIterator()
    {
        return new CubePosIterator(getAnchor(), getPeek());
    }

    /**
     * Creates new pos iterator from 0,0,0 to zero based peek using y|z|x iteration order (x iterates first).
     *
     * @return pos iterator
     */
    public Iterable<BlockPos> getZeroBasedPosIterator()
    {
        return new CubePosIterator(BlockPos.ZERO, getZeroBasedPeek());
    }

    /**
     * Moves BB by given vector.
     *
     * @param vector move vector
     */
    public void moveBy(@NotNull final BlockPos vector)
    {
        minX += vector.getX();
        minY += vector.getY();
        minZ += vector.getZ();
        maxX += vector.getX();
        maxY += vector.getY();
        maxZ += vector.getZ();
    }

    /**
     * Recalculates peek to match given size.
     *
     * @param newXSize new X axis size
     * @param newYSize new Y axis size
     * @param newZSize new Z axis size
     */
    public void resize(final int newXSize, final int newYSize, final int newZSize)
    {
        maxX = minX + newXSize - 1;
        maxY = minY + newYSize - 1;
        maxZ = minZ + newZSize - 1;
    }

    /**
     * Rotates clockwise around given center.
     *
     * @param center real world pos to rotate around
     */
    public void rotateCW(@NotNull final BlockPos center)
    {
        rotate(center, Rotation.CLOCKWISE_90);
    }

    /**
     * Rotates counterclockwise around given center.
     *
     * @param center real world pos to rotate around
     */
    public void rotateCCW(@NotNull final BlockPos center)
    {
        rotate(center, Rotation.COUNTERCLOCKWISE_90);
    }

    private void rotate(@NotNull final BlockPos center, final Rotation rotation)
    {
        BlockPos min = getAnchor();
        BlockPos max = getPeek();

        // translate
        min = min.subtract(center);
        max = max.subtract(center);

        // rotate
        min = min.rotate(rotation);
        max = max.rotate(rotation);

        // translate
        min = min.add(center);
        max = max.add(center);

        minX = Math.min(min.getX(), max.getX());
        minZ = Math.min(min.getZ(), max.getZ());
        maxX = Math.max(min.getX(), max.getX());
        maxZ = Math.max(min.getZ(), max.getZ());
    }

    /**
     * Mirrors through YZ plane using given center.
     *
     * @param center real world pos to mirror over
     */
    public void mirrorX(@NotNull final BlockPos center)
    {
        final int oldMin = minX;
        minX = -maxX + 2 * center.getX();
        maxX = -oldMin + 2 * center.getX();
    }

    /**
     * Mirrors through XY plane using given center.
     *
     * @param center real world pos to mirror over
     */
    public void mirrorZ(@NotNull final BlockPos center)
    {
        final int oldMin = minZ;
        minZ = -maxZ + 2 * center.getZ();
        maxZ = -oldMin + 2 * center.getZ();
    }

    /**
     * Adds anchor to given zerobased pos so it becomes real world pos.
     *
     * @param zeroBasedPos pos to transform
     * @return real world pos
     */
    public BlockPos transformZeroBasedToReal(final BlockPos zeroBasedPos)
    {
        return getAnchor().add(zeroBasedPos);
    }
}

package com.ldtteam.structurize.structure;

import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * Structure bounding box.
 * Anchor has every coord smaller or same than peek blockpos.
 */
public class StructureBB implements IStructureBB
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
     * Creates a copy from given instance.
     *
     * @param original instance to be copied
     */
    public StructureBB(final StructureBB original)
    {
        minX = original.minX;
        minY = original.minY;
        minZ = original.minZ;
        maxX = original.maxX;
        maxY = original.maxY;
        maxZ = original.maxZ;
    }

    @Override
    public BlockPos getAnchor()
    {
        return new BlockPos(minX, minY, minZ);
    }

    @Override
    public BlockPos getPeek()
    {
        return new BlockPos(maxX, maxY, maxZ);
    }

    @Override
    public int getXSize()
    {
        return maxX - minX + 1;
    }

    @Override
    public int getYSize()
    {
        return maxY - minY + 1;
    }

    @Override
    public int getZSize()
    {
        return maxZ - minZ + 1;
    }

    @Override
    public AxisAlignedBB toAABB()
    {
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
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

    /**
     * Rotates using given rotation around given center.
     *
     * @param center   real world pos to rotate around
     * @param rotation rotation type
     */
    public void rotate(final BlockPos center, final Rotation rotation)
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
     * Mirrors using given mirror through given center.
     *
     * @param center real world pos to mirror through
     * @param mirror mirror type
     */
    public void mirror(final BlockPos center, final Mirror mirror)
    {
        BlockPos min = getAnchor();
        BlockPos max = getPeek();

        // translate
        min = min.subtract(center);
        max = max.subtract(center);

        // mirror
        if (mirror == Mirror.FRONT_BACK)
        {
            min = new BlockPos(-1 * min.getX(), min.getY(), min.getZ());
            max = new BlockPos(-1 * max.getX(), max.getY(), max.getZ());
        }
        else if (mirror == Mirror.LEFT_RIGHT)
        {
            min = new BlockPos(min.getX(), min.getY(), -1 * min.getZ());
            max = new BlockPos(max.getX(), max.getY(), -1 * max.getZ());
        }

        // translate
        min = min.add(center);
        max = max.add(center);

        minX = Math.min(min.getX(), max.getX());
        minZ = Math.min(min.getZ(), max.getZ());
        maxX = Math.max(min.getX(), max.getX());
        maxZ = Math.max(min.getZ(), max.getZ());
    }
}

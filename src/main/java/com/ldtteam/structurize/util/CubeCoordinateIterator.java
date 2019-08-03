package com.ldtteam.structurize.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.util.math.BlockPos;

/**
 * Iterates x|z|y through cube.
 */
public class CubeCoordinateIterator implements Iterable<BlockPos>
{
    private final int startX;
    private final int startY;
    private final int startZ;
    private final int endX;
    private final int endY;
    private final int endZ;
    private int x;
    private int y;
    private int z;

    /**
     * Constructs new iterator.
     *
     * @param start start pos, must have every coord lower or same as end
     * @param end   end pos
     */
    public CubeCoordinateIterator(final BlockPos start, final BlockPos end)
    {
        this.startX = start.getX();
        this.startY = start.getY();
        this.startZ = start.getZ();
        this.endX = end.getX();
        this.endY = end.getY();
        this.endZ = end.getZ();
        this.x = startX - 1;
        this.y = startY;
        this.z = startZ;
    }

    @Override
    public Iterator<BlockPos> iterator()
    {
        return new Iterator<BlockPos>()
        {
            @Override
            public boolean hasNext()
            {
                return !(x == endX && y == endY && z == endZ);
            }

            @Override
            public BlockPos next()
            {
                if (x < endX)
                {
                    x++;
                }
                else if (z < endZ)
                {
                    x = startX;
                    z++;
                }
                else if (y < endY)
                {
                    y++;
                    if (y <= endY)
                    {
                        x = startX;
                        z = startZ;
                    }
                }
                else
                {
                    throw new NoSuchElementException("End of cube reached!");
                }
                return new BlockPos(x, y, z);
            }
        };
    }
}

package com.ldtteam.structurize.structure;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

/**
 * Interface for immutable structure data holder.
 */
public interface IStructure
{
    /**
     * Getter for blockinfo for blockPos relative to structure anchor.
     *
     * @param pos relative structure blockPos
     * @return {@link BlockInfo}
     */
    default BlockInfo getBlockInfo(final BlockPos pos)
    {
        return getBlockInfo(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Getter for blockinfo for blockPos relative to structure anchor.
     *
     * @param x relative structure X pos
     * @param y relative structure Y pos
     * @param z relative structure Z pos
     * @return {@link BlockInfo}
     */
    BlockInfo getBlockInfo(int x, int y, int z);

    /**
     * Getter for all structure entities.
     *
     * @return list of structure entities
     */
    List<Entity> getEntities();

    /**
     * Creates new structure bounding box. Contains things like anchor and peek poses, sizes, volume...
     *
     * @param where anchor for new bounding box
     * @return {@link StructureBB}
     */
    IStructureBB createBoundingBox(BlockPos where);

    /**
     * @return {@link IAttributeMap}
     */
    IAttributeMap getAttributeMap();
}

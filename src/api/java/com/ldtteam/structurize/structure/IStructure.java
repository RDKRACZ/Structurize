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
    BlockInfo getBlockInfo(BlockPos pos);

    /**
     * Getter for all entities in chunk given by pos.
     *
     * @param pos pos in targeted chunk
     * @return list of structure entities
     */
    List<Entity> getEntitiesInChunk(BlockPos pos);

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

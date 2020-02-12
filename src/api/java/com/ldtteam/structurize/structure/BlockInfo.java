package com.ldtteam.structurize.structure;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;

/**
 * Holds blockState and its blockEntity if it has any. Both belong to the same blockPos.
 */
public class BlockInfo
{
    private BlockState blockState;
    private TileEntity blockEntity;

    public BlockInfo(final BlockState blockState, final TileEntity blockEntity)
    {
        this.blockState = blockState;
        this.blockEntity = blockEntity;
    }

    public BlockState getBlockState()
    {
        return blockState;
    }

    @Nullable
    public TileEntity getBlockEntity()
    {
        return blockEntity;
    }

    /**
     * @return false if {@link #getBlockEntity()} will return <code>null</code>, true if not
     */
    public boolean hasBlockEntity()
    {
        return blockEntity != null;
    }
}

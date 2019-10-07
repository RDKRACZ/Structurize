package com.ldtteam.structurize.util;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;

public class BlockStateWithTileEntity
{
    private final BlockState blockState;
    private final TileEntity tileEntity;

    /**
     * @param blockState
     * @param tileEntity
     */
    public BlockStateWithTileEntity(BlockState blockState, TileEntity tileEntity)
    {
        this.blockState = blockState;
        this.tileEntity = tileEntity;
    }

    /**
     * @return the blockState
     */
    public BlockState getBlockState()
    {
        return blockState;
    }

    /**
     * @return the tileEntity
     */
    public TileEntity getTileEntity()
    {
        return tileEntity;
    }
}
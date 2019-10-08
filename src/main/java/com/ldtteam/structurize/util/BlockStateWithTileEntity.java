package com.ldtteam.structurize.util;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;

/**
 * Something like Tuple<BlockState, TileEntity>
 */
public class BlockStateWithTileEntity
{
    private final BlockState blockState;
    private final TileEntity tileEntity;

    public BlockStateWithTileEntity(BlockState blockState, TileEntity tileEntity)
    {
        this.blockState = blockState;
        this.tileEntity = tileEntity;
    }

    public BlockState getBlockState()
    {
        return blockState;
    }

    public TileEntity getTileEntity()
    {
        return tileEntity;
    }
}
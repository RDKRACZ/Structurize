package com.ldtteam.structurize.pipeline.defaults.scan;

import com.ldtteam.structurize.pipeline.scan.ComponentScanner.BlockStateComponentScanner;
import com.ldtteam.structurize.pipeline.scan.ComponentScanner.Builder;
import com.ldtteam.structurize.pipeline.scan.ComponentScanner.EntityComponentScanner;
import com.ldtteam.structurize.pipeline.scan.ComponentScanner.TileEntityComponentScanner;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class DefaultScanners
{
    public static Builder<BlockState, BlockStateComponentScanner> getDefaultBlockStateScanner()
    {
        return BlockStateComponentScanner.newBuilder().setScanner((t, w, bp) -> t);
    }

    public static Builder<TileEntity, TileEntityComponentScanner> getDefaultTileEntityScanner()
    {
        return TileEntityComponentScanner.newBuilder().setScanner((t, w, bp) -> t);
    }

    public static Builder<Entity, EntityComponentScanner> getDefaultEntityScanner()
    {
        return EntityComponentScanner.newBuilder().setScanner((t, w, bp) -> t);
    }
}
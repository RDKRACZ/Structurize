package com.ldtteam.structurize.pipeline.defaults.scan;

import com.ldtteam.structurize.pipeline.scan.BlockInfoScanner;
import com.ldtteam.structurize.pipeline.scan.EntityScanner;
import com.ldtteam.structurize.structure.BlockInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DefaultScanners
{
    public static BlockInfoScanner getDefaultBlockInfoScanner()
    {
        return new BlockInfoScanner()
        {
            @Override
            public BlockInfo scan(final World world, final BlockPos blockPos)
            {
                return new BlockInfo(world.getBlockState(blockPos), world.getTileEntity(blockPos));
            }
        };
    }

    public static EntityScanner<Entity> getDefaultEntityScanner()
    {
        return new EntityScanner<Entity>()
        {
            @Override
            public Entity scan(final Entity entity, final World world)
            {
                return entity;
            }
        };
    }
}

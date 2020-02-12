package com.ldtteam.structurize.pipeline.defaults.build;

import java.util.List;
import com.ldtteam.structurize.pipeline.build.BlockInfoPlacer;
import com.ldtteam.structurize.pipeline.build.EntityPlacer;
import com.ldtteam.structurize.structure.BlockInfo;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DefaultPlacers
{
    public static BlockInfoPlacer getDefaultBlockInfoPlacer()
    {
        return new BlockInfoPlacer()
        {
            @Override
            public void place(final BlockInfo blockInfo, final BlockPos blockPos, final World world)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public List<ItemStack> getRequirements(final BlockInfo blockInfo, final BlockPos blockPos, final World world)
            {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    public static EntityPlacer<Entity> getDefaultEntityPlacer()
    {
        return new EntityPlacer<Entity>()
        {
            @Override
            public void place(final Entity entity, final World world)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public List<ItemStack> getRequirements(final Entity entity, final World world)
            {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }
}

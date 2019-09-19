package com.ldtteam.structurize.pipeline.defaults.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.BlockStateComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.Builder;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.EntityComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.FluidStateComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.TileEntityComponentPlacer;
import com.ldtteam.structurize.util.constants.MinecraftConstants;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.tileentity.TileEntity;

public class DefaultPlacers
{
    public static Builder<BlockState, BlockStateComponentPlacer> getDefaultBlockStatePlacer()
    {
        return BlockStateComponentPlacer.newBuilder().setPlacer((thing, world, pos) -> {
            if (world.getBlockState(pos).equals(thing) || world.setBlockState(pos, thing, MinecraftConstants.BLOCK_PLACEMENT_UPDATE_FLAGS))
            {
                return true;
            }
            return false;
        }).setRequirements((thing, world, pos) -> Arrays.asList(new ItemStack(thing.getBlock().asItem()))).setSubstitutions((t, w, bp) -> new ArrayList<>());
    }

    public static Builder<IFluidState, FluidStateComponentPlacer> getDefaultFluidStatePlacer()
    {
        return FluidStateComponentPlacer.newBuilder().setPlacer((thing, world, pos) -> {
            if (!thing.isSource())
            {
                return true;
            }
            final BlockState blockState = thing.getBlockState();
            if (world.getBlockState(pos).equals(blockState) || world.setBlockState(pos, blockState, MinecraftConstants.BLOCK_PLACEMENT_UPDATE_FLAGS))
            {
                return true;
            }
            return false;
        })
            .setRequirements((thing, world, pos) -> thing.isSource() ? Arrays.asList(new ItemStack(thing.getFluid().getFilledBucket())) : new ArrayList<>())
            .setSubstitutions((t, w, bp) -> new ArrayList<>());
    }

    public static Builder<TileEntity, TileEntityComponentPlacer> getDefaultTileEntityPlacer()
    {
        return TileEntityComponentPlacer.newBuilder().setPlacer((thing, world, pos) -> {
            if (!world.getTileEntity(pos).equals(thing))
            {
                world.setTileEntity(pos, thing);
            }
            return true;
        })
            .setRequirements((thing, world, pos) -> Arrays.asList(new ItemStack(thing.getBlockState().getBlock())))
            .setSubstitutions((t, w, bp) -> new ArrayList<>());
    }

    public static Builder<Entity, EntityComponentPlacer> getDefaultEntityPlacer()
    {
        return EntityComponentPlacer.newBuilder().setPlacer((thing, world, pos) -> {
            thing.setUniqueId(UUID.randomUUID());
            return world.addEntity(thing);
        })
            .setRequirements((thing, world, pos) -> Arrays.asList(new ItemStack(SpawnEggItem.getEgg(thing.getType()))))
            .setSubstitutions((t, w, bp) -> new ArrayList<>());
    }
}
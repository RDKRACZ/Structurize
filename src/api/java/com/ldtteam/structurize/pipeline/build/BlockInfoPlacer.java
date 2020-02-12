package com.ldtteam.structurize.pipeline.build;

import java.util.List;
import com.ldtteam.structurize.structure.BlockInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Used for placing blockState and its (if present) blockEntity into world.
 */
public abstract class BlockInfoPlacer extends ForgeRegistryEntry<BlockInfoPlacer>
{
    /**
     * Places blockState, blockEntity and fluidState into given world at given pos.
     *
     * @param blockInfo blockState, blockEntity and fluidState to place
     * @param blockPos  pos to place at
     * @param world     world to place into
     */
    public abstract void place(BlockInfo blockInfo, BlockPos blockPos, World world);

    /**
     * Getter for all needed items to place this blockState, blockEntity and fluidState.
     * Returning null means there are no required items.
     *
     * @param blockInfo blockState, blockEntity and fluidState to place
     * @param blockPos  pos to place at
     * @param world     world to place into
     * @return a list of required items or null if there are no required items
     */
    public abstract List<ItemStack> getRequirements(BlockInfo blockInfo, BlockPos blockPos, World world);
}

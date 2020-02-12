package com.ldtteam.structurize.pipeline.scan;

import com.ldtteam.structurize.structure.BlockInfo;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Scans blockstate, its (if present) blockEntity and fluidState from world.
 */
public abstract class BlockInfoScanner extends ForgeRegistryEntry<BlockInfoScanner>
{
    /**
     * Creates new blockInfo of given pos from given world.
     * Scans blockState, blockEntity and fluidState
     *
     * @param world    world to scan from
     * @param blockPos pos to scan at
     * @return new blockInfo or null (null means use {@link Blocks#AIR} default state)
     */
    public abstract BlockInfo scan(World world, BlockPos blockPos);
}

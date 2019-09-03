package com.ldtteam.structurize.pipeline.build;

import java.util.List;
import java.util.Map;
import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.BlockStateComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.EntityComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.TileEntityComponentPlacer;
import com.ldtteam.structurize.structure.util.StructureBB;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RawPlacer
{
    protected final List<BlockState> blockPalette;
    protected final short[][][] structureBlocks;
    protected final List<CompoundNBT> structureEntities;
    protected final Map<BlockPos, CompoundNBT> structureTileEntities;
    protected final StructureBB structurePosition;
    protected final World structureWorld;

    protected RawPlacer(final RawPlacer placer)
    {
        this.blockPalette = placer.blockPalette;
        this.structureBlocks = placer.structureBlocks;
        this.structureEntities = placer.structureEntities;
        this.structureTileEntities = placer.structureTileEntities;
        this.structurePosition = placer.structurePosition;
        this.structureWorld = placer.structureWorld;
    }

    public RawPlacer(final EventInfoHolder<?> event)
    {
        blockPalette = event.getStructure().getBlockPalette();
        structureBlocks = event.getStructure().getBlocks();
        structureEntities = event.getStructure().getEntities();
        structureTileEntities = event.getStructure().getTileEntities();
        structurePosition = event.getPosition();
        structureWorld = event.getWorld();
    }

    public static BlockStateComponentPlacer getBlockStatePlacer(final BlockState blockState)
    {
        return Instances.getComponentRegistries().getBlockStatePlacerRegistry().getValue(blockState.getBlock().getRegistryName());
    }

    public static TileEntityComponentPlacer getTileEntityPlacer(final TileEntity tileEntity)
    {
        return Instances.getComponentRegistries().getTileEntityPlacerRegistry().getValue(tileEntity.getType().getRegistryName());
    }

    public static EntityComponentPlacer getEntityPlacer(final Entity entity)
    {
        return Instances.getComponentRegistries().getEntityPlacerRegistry().getValue(entity.getType().getRegistryName());
    }
}
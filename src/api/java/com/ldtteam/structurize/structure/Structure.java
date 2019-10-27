package com.ldtteam.structurize.structure;

import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class Structure
{
    protected short[][][] blocks;
    protected List<BlockState> blockPalette;
    protected List<CompoundNBT> entities;
    protected Map<BlockPos, CompoundNBT> tileEntities;

    public Structure(final short[][][] blocks,
        final List<BlockState> blockPalette,
        final List<CompoundNBT> entities,
        final Map<BlockPos, CompoundNBT> tileEntities)
    {
        this.blocks = blocks;
        this.blockPalette = blockPalette;
        this.entities = entities;
        this.tileEntities = tileEntities;
    }

    public short[][][] getBlocks()
    {
        return blocks;
    }

    public List<BlockState> getBlockPalette()
    {
        return blockPalette;
    }

    public Map<BlockPos, CompoundNBT> getTileEntities()
    {
        return tileEntities;
    }

    public List<CompoundNBT> getEntities()
    {
        return entities;
    }

    public static class MutableStructure extends Structure
    {
        public MutableStructure(final short[][][] blocks,
            final List<BlockState> blockPalette,
            final List<CompoundNBT> entities,
            final Map<BlockPos, CompoundNBT> tileEntities)
        {
            super(blocks, blockPalette, entities, tileEntities);
        }

        public void setBlocks(final short[][][] blocks)
        {
            super.blocks = blocks;
        }

        public void setBlockPalette(final List<BlockState> blockPalette)
        {
            super.blockPalette = blockPalette;
        }

        public void setTileEntities(final Map<BlockPos, CompoundNBT> tileEntities)
        {
            super.tileEntities = tileEntities;
        }

        public void setEntities(final List<CompoundNBT> entities)
        {
            super.entities = entities;
        }

        public Structure toImmutable()
        {
            return new Structure(blocks, blockPalette, entities, tileEntities);
        }
    }
}

package com.ldtteam.structurize.structure.data;

import com.ldtteam.structurize.util.constant.NBTConstants;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.BitArray;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class ChunkSection
{
    private static final int POS_MASK = 0xff;
    private final int yPos;
    private final int[][][] blocks = new int[16][16][16];
    private final BlockStatePalette palette;

    public ChunkSection(final int yPos, final BlockStatePalette palette)
    {
        this.yPos = yPos;
        this.palette = palette;
    }

    public ChunkSection(final CompoundNBT nbt, final BlockStatePalette palette)
    {
        this.yPos = nbt.getByte(NBTConstants.SECTION_Y_POS);
        this.palette = palette;
        deserializeBlocks(nbt.getLongArray(NBTConstants.SECTION_BLOCKS));
    }

    public CompoundNBT serializeNBT()
    {
        final CompoundNBT nbt = new CompoundNBT();
        nbt.putByte(NBTConstants.SECTION_Y_POS, (byte) yPos);
        nbt.putLongArray(NBTConstants.SECTION_BLOCKS, serializeBlocks());
        return nbt;
    }

    public void setBlockState(final BlockPos pos, final BlockState blockState)
    {
        blocks[pos.getY() & POS_MASK][pos.getZ() & POS_MASK][pos.getX() & POS_MASK] = palette.getIndex(blockState);
    }

    public BlockState getBlockState(final BlockPos pos)
    {
        return palette.getBlockState(blocks[pos.getY() & POS_MASK][pos.getZ() & POS_MASK][pos.getX() & POS_MASK]);
    }

    private long[] serializeBlocks()
    {
        final BitArray data = new BitArray(palette.getSizeBits(), 4096);
        int pos = 0;
        for (int y = 0; y < 16; y++)
        {
            for (int z = 0; z < 16; z++)
            {
                for (int x = 0; x < 16; x++)
                {
                    data.setAt(pos++, blocks[y][z][x]);
                }
            }
        }
        return data.getBackingLongArray();
    }

    private void deserializeBlocks(final long[] dataArray)
    {
        final BitArray data = new BitArray(palette.getSizeBits(), 4096, dataArray);
        int pos = 0;
        for (int y = 0; y < 16; y++)
        {
            for (int z = 0; z < 16; z++)
            {
                for (int x = 0; x < 16; x++)
                {
                    blocks[y][z][x] = data.getAt(pos++);
                }
            }
        }
    }

    public int getYPos()
    {
        return yPos;
    }

    public void rotate(final Rotation rotation)
    {
    }

    public void mirror(final Mirror mirror)
    {
    }
}

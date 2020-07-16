package com.ldtteam.structurize.structure.data;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public class BlockStatePalette
{
    private final ObjectList<BlockState> palette = new ObjectArrayList<>();

    public BlockStatePalette()
    {
        // Noop
    }

    public BlockStatePalette(final ListNBT nbt)
    {
        for (final INBT blockStateNbt : nbt)
        {
            palette.add(NBTUtil.readBlockState((CompoundNBT) blockStateNbt));
        }
    }

    // index 0 is nothing -> thus shift one left
    public BlockState getBlockState(final int index)
    {
        return palette.get(index - 1);
    }

    // index 0 is nothing -> thus shift one right
    public int getIndex(final BlockState blockState)
    {
        int index = palette.indexOf(blockState);
        if (index == -1)
        {
            index = palette.size();
            palette.add(blockState);
        }
        return index + 1;
    }

    public int size()
    {
        return palette.size();
    }

    public int getSizeBits()
    {
        return Integer.SIZE - Integer.numberOfLeadingZeros(size());
    }

    public ListNBT serializeNBT()
    {
        final ListNBT nbt = new ListNBT();
        for (final BlockState blockState : palette)
        {
            nbt.add(NBTUtil.writeBlockState(blockState));
        }
        return nbt;
    }

    public void rotate(final Rotation rotation)
    {
        for (int i = 0; i < palette.size(); i++)
        {
            palette.set(i, palette.get(i).rotate(rotation));
        }
    }

    public void mirror(final Mirror mirror)
    {
        for (int i = 0; i < palette.size(); i++)
        {
            palette.set(i, palette.get(i).mirror(mirror));
        }
    }
}

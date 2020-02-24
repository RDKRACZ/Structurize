package com.ldtteam.structurize.structure;

import java.util.List;
import java.util.Map;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class Structure implements IStructure
{
    private StructureBB structureBB;
    private Int2ObjectArrayMap attributeMap;

    @Override
    public BlockInfo getBlockInfo(final int x, final int y, final int z)
    {
        return null;
    }

    @Override
    public List<Entity> getEntities()
    {
        return null;
    }

    @Override
    public StructureBB createBoundingBox(final BlockPos where)
    {
        return new StructureBB(null, null);
    }

    @Override
    public IAttributeMap getAttributeMap()
    {
        return null;
    }
}

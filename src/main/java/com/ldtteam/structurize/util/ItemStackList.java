package com.ldtteam.structurize.util;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang3.NotImplementedException;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemStackList extends ArrayList<ItemStack>
{
    @Override
    public boolean add(final ItemStack itemStack)
    {
        if (itemStack.isEmpty())
        {
            return true;
        }

        for (int i = 0; i < super.size(); i++)
        {
            final ItemStack candidate = super.get(i);

            if (candidate == null || candidate.isEmpty())
            {
                super.set(i, itemStack);
                return true;
            }

            if (ItemHandlerHelper.canItemStacksStackRelaxed(itemStack, candidate))
            {
                final int space = Math.min(candidate.getMaxStackSize() - candidate.getCount(), itemStack.getCount());
                candidate.grow(space);
                itemStack.shrink(space);
            }

            if (itemStack.isEmpty())
            {
                return true;
            }
        }
        return super.add(itemStack);
    }

    /**
     * @return always true, does not care about unsuccessful additions
     */
    @Override
    public boolean addAll(final Collection<? extends ItemStack> c)
    {
        c.forEach(this::add);
        return true;
    }

    @Override
    @Deprecated
    public void add(final int index, final ItemStack element)
    {
        throw new NotImplementedException("Can't insert with index specified");
    }

    @Override
    @Deprecated
    public boolean addAll(final int index, final Collection<? extends ItemStack> c)
    {
        throw new NotImplementedException("Can't insert with index specified");
    }
}

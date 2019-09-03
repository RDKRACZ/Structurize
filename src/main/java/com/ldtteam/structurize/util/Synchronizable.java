package com.ldtteam.structurize.util;

import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface Synchronizable<T extends INBT> extends INBTSerializable<T>
{
    boolean shouldSynchronize();

    void markForSynchronization();

    default void synchronize()
    {
        if (shouldSynchronize())
        {
            processSynchronizationData(serializeNBT());
        }
    }

    // should reset sync flag
    void processSynchronizationData(T data);

    void receiveSynchronization(T data);
}
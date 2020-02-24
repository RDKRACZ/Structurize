package com.ldtteam.structurize.event;

import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public interface IStructurePlaceEvent extends IRenderableEvent
{
    void relativeMove(BlockPos vector);

    void absoluteMove(BlockPos pos);

    void rotate(Rotation rotation);

    void mirror(Mirror mirror);
}

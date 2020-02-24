package com.ldtteam.structurize.event;

import java.util.Optional;
import com.ldtteam.structurize.structure.StructureId;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ServerEventManager implements IEventManager
{
    @Override
    public IStructurePlaceEvent openPlacingEventGuiForPlayer(final BlockPos where,
        final PlayerEntity player,
        final StructureId structurePath)
    {
        // TODO Auto-generated method stub, pass to client
        return null;
    }

    @Override
    public Optional<IStructurePlaceEvent> askPlayerForPlacingEvent(final BlockPos where,
        final PlayerEntity player,
        final StructureId structurePath)
    {
        // TODO Auto-generated method stub, pass to client
        return null;
    }

    @Override
    public IStructurePlaceEvent placeStructureIntoWorld(final BlockPos where, final World world, final StructureId structurePath)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IStructureScanEvent scanStructureFromWorld(final BlockPos from,
        final BlockPos to,
        final World world,
        final StructureId structurePath)
    {
        // TODO Auto-generated method stub
        return null;
    }
}

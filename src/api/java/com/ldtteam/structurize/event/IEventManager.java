package com.ldtteam.structurize.event;

import java.util.Optional;
import com.ldtteam.structurize.structure.StructureId;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IEventManager
{
    IStructurePlaceEvent openPlacingEventGuiForPlayer(BlockPos where, PlayerEntity player, StructureId structurePath);

    Optional<IStructurePlaceEvent> askPlayerForPlacingEvent(BlockPos where, PlayerEntity player, StructureId structurePath);

    IStructurePlaceEvent placeStructureIntoWorld(BlockPos where, World world, StructureId structurePath);

    IStructureScanEvent scanStructureFromWorld(BlockPos from, BlockPos to, World world, StructureId structurePath);
}

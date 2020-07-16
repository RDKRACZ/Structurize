package com.ldtteam.structurize.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.ldtteam.structurize.client.render.util.RenderUtils;
import com.ldtteam.structurize.structure.StructureBB;
import com.ldtteam.structurize.structure.StructureId;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ClientEventManager implements IEventManager
{
    private CaliperEvent activeCaliperEvent = new CaliperEvent(new StructureBB(BlockPos.ZERO.up(16), new BlockPos(16, 32, 16)));
    private StructureScanEvent activeScanEvent;
    private List<StructurePlaceEvent> activePlaceEvents = new ArrayList<>();
    public int lastSelectedPlaceEvent = -1;

    @Override
    public IStructurePlaceEvent openPlacingEventGuiForPlayer(final BlockPos where,
        final PlayerEntity player,
        final StructureId structurePath)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<IStructurePlaceEvent> askPlayerForPlacingEvent(final BlockPos where,
        final PlayerEntity player,
        final StructureId structurePath)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IStructurePlaceEvent placeStructureIntoWorld(final BlockPos where, final World world, final StructureId structurePath)
    {
        // TODO Auto-generated method stub, pass to server
        return null;
    }

    @Override
    public IStructureScanEvent scanStructureFromWorld(final BlockPos from,
        final BlockPos to,
        final World world,
        final StructureId structurePath)
    {
        // TODO Auto-generated method stub, pass to server
        return null;
    }

    public void render(final RenderWorldLastEvent context)
    {
        RenderUtils.saveVanillaState();
        RenderUtils.latestProjectionMatrix = context.getProjectionMatrix();
        for (final StructurePlaceEvent structurePlaceEvent : activePlaceEvents)
        {
            structurePlaceEvent.render(context);
        }
        if (activeScanEvent != null)
        {
            activeScanEvent.render(context);
        }
        if (activeCaliperEvent != null)
        {
            activeCaliperEvent.render(context);
        }
        RenderUtils.loadVanillaState();
    }
}

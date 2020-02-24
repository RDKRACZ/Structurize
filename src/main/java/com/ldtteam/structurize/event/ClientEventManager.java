package com.ldtteam.structurize.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.ldtteam.structurize.client.render.RenderUtils;
import com.ldtteam.structurize.structure.StructureBB;
import com.ldtteam.structurize.structure.StructureId;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClientEventManager implements IEventManager
{
    private CaliperEvent activeCaliperEvent = new CaliperEvent(new StructureBB(BlockPos.ZERO.up(16), new BlockPos(16, 32, 16)));
    private StructureScanEvent activeScanEvent;
    private List<StructurePlaceEvent> activePlaceEvents = new ArrayList<>();

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

    public void render(final WorldRenderer context, final MatrixStack matrixStack, final float partialTicks)
    {
        RenderUtils.saveVanillaState(context, matrixStack);
        for (final StructurePlaceEvent structurePlaceEvent : activePlaceEvents)
        {
            structurePlaceEvent.render(context, matrixStack, partialTicks);
        }
        if (activeScanEvent != null)
        {
            activeScanEvent.render(context, matrixStack, partialTicks);
        }
        if (activeCaliperEvent != null)
        {
            activeCaliperEvent.render(context, matrixStack, partialTicks);
        }
        RenderUtils.loadVanillaState(context, matrixStack);
    }
}

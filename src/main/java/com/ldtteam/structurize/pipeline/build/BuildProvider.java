package com.ldtteam.structurize.pipeline.build;

import java.util.Iterator;
import java.util.Optional;
import java.util.Map.Entry;
import com.ldtteam.structurize.Instances;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BuildProvider
{
    public static void place(final EventInfoHolder<?> event)
    {
        final Iterator<BlockPos> structPosIterator = event.getPosition().getZeroBasedPosIterator().iterator();
        for (final BlockPos worldPos : event.getPosition().getPosIterator())
        {
            final BlockPos structPos = structPosIterator.next();
            final BlockState blockStateToPlace =
                event.getStructure().getBlockPalette().get(event.getStructure().getBlocks()[structPos.getY()][structPos.getZ()][structPos.getX()]);
            Instances.getComponentRegistries()
                .getBlockStatePlacerRegistry()
                .getValue(blockStateToPlace.getBlock().getRegistryName())
                .place(blockStateToPlace, event.getWorld(), worldPos);
        }
        for (final Entry<BlockPos, CompoundNBT> entry : event.getStructure().getTileEntities().entrySet())
        {
            final CompoundNBT teCompound = entry.getValue();
            final BlockPos worldPos = event.getPosition().getAnchor().add(entry.getKey());
            final TileEntity te = TileEntity.create(teCompound);
            te.setPos(worldPos);
            Instances.getComponentRegistries().getTileEntityPlacerRegistry().getValue(te.getType().getRegistryName()).place(te, event.getWorld(), worldPos);
        }
        for (final CompoundNBT entityData : event.getStructure().getEntities())
        {
            final Optional<EntityType<?>> type = EntityType.readEntityType(entityData);
            if (!type.isPresent())
            {
                Instances.getLogger().warn("Can't find entity type.");
            }
            final Entity entity = type.get().create(event.getWorld());
            entity.deserializeNBT(entityData);
            final Vec3d newPos = entity.getPositionVector().add(new Vec3d(event.getPosition().getAnchor()));
            entity.setPosition(newPos.getX(), newPos.getY(), newPos.getZ());
            Instances.getComponentRegistries()
                .getEntityPlacerRegistry()
                .getValue(type.get().getRegistryName())
                .place(entity, event.getWorld(), new BlockPos(newPos));
        }
        event.cancel();
    }
}

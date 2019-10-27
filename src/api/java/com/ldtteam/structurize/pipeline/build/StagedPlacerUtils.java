package com.ldtteam.structurize.pipeline.build;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.BlockStateComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.TileEntityComponentPlacer;
import com.ldtteam.structurize.util.BlockStateWithTileEntity;
import com.ldtteam.structurize.util.FastIterator;
import com.ldtteam.structurize.util.ItemStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Various classes used in StagedPlacer.
 */
public class StagedPlacerUtils
{
    public static class BsWithTePlaceAction extends PlaceAction<BlockStateWithTileEntity>
    {
        public BsWithTePlaceAction(final BlockStateWithTileEntity blockStateWithTileEntity,
            final BlockStateComponentPlacer bsp,
            final TileEntityComponentPlacer tep,
            final World world,
            final BlockPos pos,
            final boolean triggerPlayerActions)
        {
            this(blockStateWithTileEntity, bsp, tep, world, pos, triggerPlayerActions, new DummyPlaceAction());
        }

        public BsWithTePlaceAction(final BlockStateWithTileEntity blockStateWithTileEntity,
            final BlockStateComponentPlacer bsp,
            final TileEntityComponentPlacer tep,
            final World world,
            final BlockPos pos,
            final boolean triggerPlayerActions,
            final PlaceAction<?> additionalAction)
        {
            // reduce dupes? but don't remove compile null checks
            super(blockStateWithTileEntity.getTileEntity() == null ? () -> {
                final ItemStackList result = new ItemStackList();
                result.addAll(bsp.getRequirements(blockStateWithTileEntity.getBlockState(), world, pos, triggerPlayerActions));
                result.addAll(additionalAction.getRequirements().get());
                return (ArrayList<ItemStack>) result;
            } : () -> {
                final ItemStackList result = new ItemStackList();
                result.addAll(bsp.getRequirements(blockStateWithTileEntity.getBlockState(), world, pos, triggerPlayerActions));
                result.addAll(tep.getRequirements(blockStateWithTileEntity.getTileEntity(), world, pos, triggerPlayerActions));
                result.addAll(additionalAction.getRequirements().get());
                return (ArrayList<ItemStack>) result;
            }, blockStateWithTileEntity.getTileEntity() == null ? () -> {
                bsp.place(blockStateWithTileEntity.getBlockState(), world, pos, triggerPlayerActions);
                additionalAction.perform();
            } : () -> {
                bsp.place(blockStateWithTileEntity.getBlockState(), world, pos, triggerPlayerActions);
                tep.place(blockStateWithTileEntity.getTileEntity(), world, pos, triggerPlayerActions);
                additionalAction.perform();
            }, blockStateWithTileEntity, null, world, pos);
        }
    }

    public static class DummyPlaceAction extends PlaceAction<Object>
    {
        public DummyPlaceAction()
        {
            super(() -> new ArrayList<>(), () -> {}, null, null, null, null);
        }
    }

    public static class PlaceAction<T>
    {
        private final Supplier<List<ItemStack>> requirements;
        private final Runnable                  action;
        private final T                         objectToPlace;
        private final ResourceLocation          objectRegistryName;
        private final World                     world;
        private final BlockPos                  pos;

        public PlaceAction(final Supplier<List<ItemStack>> requirements,
            final Runnable action,
            final T objectToPlace,
            final ResourceLocation objectRegistryName,
            final World world,
            final BlockPos pos)
        {
            this.requirements = requirements;
            this.action = action;
            this.objectToPlace = objectToPlace;
            this.objectRegistryName = objectRegistryName;
            this.world = world;
            this.pos = pos;
        }

        /**
         * List of requirements that would normal player have to pay for placing given placeable.
         */
        public Supplier<List<ItemStack>> getRequirements()
        {
            return requirements;
        }

        /**
         * Perform world IO placing.
         */
        public void perform()
        {
            action.run();
        }

        /**
         * Returns BlockState,FluidState,TileEntity,Entity, modifying this modifies result of requirements and perform methods
         */
        public T getObjectToPlace()
        {
            return objectToPlace;
        }

        /**
         * objectToPlace registry name
         */
        public ResourceLocation getObjectRegistryName()
        {
            return objectRegistryName;
        }

        /**
         * target world reference
         */
        public World getWorld()
        {
            return world;
        }

        /**
         * target world block pos reference
         */
        public BlockPos getPos()
        {
            return pos;
        }
    }

    public static class ActionIterator<I, T> implements FastIterator<PlaceAction<T>>
    {
        private final Iterator<I>                 backingIterator;
        private final Function<I, PlaceAction<T>> iteratorTransformer;

        public ActionIterator(final Iterator<I> backingIterator, final Function<I, PlaceAction<T>> iteratorTransformer)
        {
            this.backingIterator = backingIterator;
            this.iteratorTransformer = iteratorTransformer;
        }

        @Override
        public boolean hasNext()
        {
            return backingIterator.hasNext();
        }

        @Override
        public PlaceAction<T> next()
        {
            PlaceAction<T> result = null;
            while (result == null && hasNext())
            {
                result = iteratorTransformer.apply(backingIterator.next());
            }
            return result;
        }

        @Override
        public void fastConsume(final PlaceAction<T> placeAction)
        {
            placeAction.perform();
        }

        public ActionIterator<?, T> successor()
        {
            return null;
        }
    }
}

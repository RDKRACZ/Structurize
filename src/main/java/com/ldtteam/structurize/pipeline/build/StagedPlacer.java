package com.ldtteam.structurize.pipeline.build;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.TileEntityComponentPlacer;
import com.ldtteam.structurize.util.Stage;
import com.ldtteam.structurize.util.Stage.StageData;
import org.apache.commons.lang3.ObjectUtils.Null;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;

public class StagedPlacer extends RawPlacer implements Iterator<StagedPlacer.PlaceAction>
{
    /*
     * Building stages, recommended order is the same as follows.
     */

    /**
     * Fixes holes using given block in a layer under the lowest structure layer.
     */
    public static final Stage<Block, StagedPlacer> FIX_FLOOR_WITH = Stages.BlockStages.FIX_FLOOR_WITH;
    /**
     * Fixes holes using given block in a layer above the highest structure layer.
     */
    public static final Stage<Block, StagedPlacer> FIX_CEILING_WITH = Stages.BlockStages.FIX_CEILING_WITH;
    /**
     * Replaces fluids using given block in ceiling and every wall.
     */
    public static final Stage<Block, StagedPlacer> FIX_FLUIDS_WITH = Stages.BlockStages.FIX_FLUIDS_WITH;
    /**
     * Clears structureBB with given block.
     */
    public static final Stage<Block, StagedPlacer> CLEAR_WITH = Stages.BlockStages.CLEAR_WITH;
    /**
     * Places all solid blocks.
     */
    public static final Stage<Boolean, StagedPlacer> PLACE_SOLID = Stages.BooleanStages.PLACE_SOLID;
    /**
     * Places all falling blocks.
     */
    public static final Stage<Tuple<Boolean, Boolean>, StagedPlacer> PLACE_FALLING = Stages.DoubleBooleanStages.PLACE_FALLING;
    /**
     * Places all non-solid blocks.
     */
    public static final Stage<Boolean, StagedPlacer> PLACE_NON_SOLID = Stages.BooleanStages.PLACE_NON_SOLID;
    /**
     * Places all fluids.
     */
    public static final Stage<Boolean, StagedPlacer> PLACE_FLUIDS = Stages.BooleanStages.PLACE_FLUIDS;
    /**
     * Places all tile entities.
     */
    public static final Stage<Boolean, StagedPlacer> PLACE_TILE_ENTITIES = Stages.BooleanStages.PLACE_TILE_ENTITIES;
    /**
     * Clears remaining falling block supports from PLACE_FALLING stage.
     */
    public static final Stage<Null, StagedPlacer> CLEAR_FALLING_SUPPORT = Stages.NoDataStages.CLEAR_FALLING_SUPPORT;
    /**
     * Places all entities.
     */
    public static final Stage<Boolean, StagedPlacer> PLACE_ENTITIES = Stages.BooleanStages.PLACE_ENTITIES;
    /**
     * Marks placer as ready for removal. No further actions can be executed after this stage.
     */
    public static final Stage<Null, StagedPlacer> END_STAGE = Stages.NoDataStages.END_STAGE;
    /**
     * Dummy stage with no action and no then action.
     */
    public static final Stage<Null, StagedPlacer> DUMMY_STAGE = Stages.NoDataStages.DUMMY_STAGE;
    /**
     * Dummy stage to trigger onStageChange listener, something like pause stage.
     */
    public static final Stage<Supplier<Runnable>, StagedPlacer> CUSTOM_STAGE = Stages.CustomStage.CUSTOM_STAGE;

    private final Iterator<StageData<?, StagedPlacer>> stages;
    private final BiConsumer<Stage<?, StagedPlacer>, Stage<?, StagedPlacer>> onStageChangeListener;
    private boolean destroyed = false;
    private Iterator<PlaceAction> runningIterator;

    public StagedPlacer(final RawPlacer placer)
    {
        this(placer, null);
    }

    public StagedPlacer(final RawPlacer placer, final LinkedList<StageData<?, StagedPlacer>> stages)
    {
        this(placer, stages, null);
    }

    public StagedPlacer(
        final RawPlacer placer,
        final LinkedList<StageData<?, StagedPlacer>> stages,
        final BiConsumer<Stage<?, StagedPlacer>, Stage<?, StagedPlacer>> onStageChangeListener)
    {
        super(placer);
        this.stages = stages == null ? null : stages.iterator();
        this.onStageChangeListener = onStageChangeListener;
    }

    public static LinkedList<StageData<?, StagedPlacer>> createDefaultStages(final boolean requirements, final boolean supportFallingIfBottom)
    {
        final LinkedList<StageData<?, StagedPlacer>> defStages = new LinkedList<>();
        defStages.add(CLEAR_WITH.createData(Blocks.AIR));
        defStages.add(PLACE_SOLID.createData(requirements));
        defStages.add(PLACE_FALLING.createData(new Tuple<>(requirements, supportFallingIfBottom)));
        defStages.add(PLACE_NON_SOLID.createData(requirements));
        defStages.add(PLACE_FLUIDS.createData(requirements));
        defStages.add(PLACE_TILE_ENTITIES.createData(requirements));
        defStages.add(CLEAR_FALLING_SUPPORT.createEmptyData());
        defStages.add(PLACE_ENTITIES.createData(requirements));
        defStages.add(END_STAGE.createEmptyData());
        return defStages;
    }

    public void nextStage(final Stage<?, StagedPlacer> previous)
    {
        final StageData<?, StagedPlacer> next = stages.next();
        onStageChangeListener.accept(previous, next.getStage());
        next.runStage(this);
    }

    /**
     * Fixes holes using given block in a layer under the lowest structure layer.
     */
    public void fixFloorWith(final StageData<Block, StagedPlacer> stageData)
    {
        stageData.endStage(this);
    }

    /**
     * Fixes holes using given block in a layer above the highest structure layer.
     */
    public void fixCeilingWith(final StageData<Block, StagedPlacer> stageData)
    {
        stageData.endStage(this);
    }

    /**
     * Replaces fluids using given block in ceiling and every wall.
     */
    public void fixFluidsWith(final StageData<Block, StagedPlacer> stageData)
    {
        stageData.endStage(this);
    }

    /**
     * Clears structureBB with given block.
     */
    public void clearWith(final StageData<Block, StagedPlacer> stageData)
    {
        stageData.endStage(this);
    }

    /**
     * Places all solid blocks.
     */
    public void placeSolid(final StageData<Boolean, StagedPlacer> stageData)
    {
        stageData.endStage(this);
    }

    /**
     * Places all falling blocks. first requirements second support if bottom
     */
    public void placeFalling(final StageData<Tuple<Boolean, Boolean>, StagedPlacer> stageData)
    {
        stageData.endStage(this);
    }

    /**
     * Places all non-solid blocks.
     */
    public void placeNonSolid(final StageData<Boolean, StagedPlacer> stageData)
    {
        stageData.endStage(this);
    }

    /**
     * Places all fluids.
     */
    public void placeFluids(final StageData<Boolean, StagedPlacer> stageData)
    {
        stageData.endStage(this);
    }

    /**
     * Places all tile entities.
     */
    public void placeTileEntities(final StageData<Boolean, StagedPlacer> stageData)
    {
        // create fast iterator to perform whole stage without requirements
        runningIterator =
            new ActionIterator<Map.Entry<BlockPos, CompoundNBT>, StagedPlacer>(this, stageData, structureTileEntities.entrySet().iterator(), entry -> {
                final CompoundNBT teCompound = entry.getValue();
                final BlockPos worldPos = structurePosition.getAnchor().add(entry.getKey());
                final TileEntity te = TileEntity.create(teCompound);
                te.setPos(worldPos);
                te.setWorld(structureWorld);
                final TileEntityComponentPlacer tePlacer = getTileEntityPlacer(te);

                return new PlaceAction(() -> tePlacer.getRequirements(te, structureWorld, worldPos), () -> tePlacer.place(te, structureWorld, worldPos));
            });
    }

    /**
     * Clears remaining falling block supports from PLACE_FALLING stage.
     */
    public void clearFallingSupport(final StageData<Null, StagedPlacer> stageData)
    {
        stageData.endStage(this);
    }

    /**
     * Places all entities.
     */
    public void placeEntities(final StageData<Boolean, StagedPlacer> stageData)
    {
        stageData.endStage(this);
    }

    /**
     * Dummy stage to trigger onStageChange listener, something like pause stage.
     */
    public void customStage(final StageData<Supplier<Runnable>, StagedPlacer> stageData)
    {
        stageData.getData().get().run();
        stageData.endStage(this);
    }

    /**
     * Marks placer as ready for removal. No further actions can be executed after this stage.
     */
    public void endStage(final StageData<Null, StagedPlacer> stageData)
    {
    }

    @Override
    public boolean hasNext()
    {
        return runningIterator != null && runningIterator.hasNext();
    }

    @Override
    public PlaceAction next()
    {
        return runningIterator.next();
    }

    public static class PlaceAction
    {
        private final Supplier<List<ItemStack>> requirements;
        private final Runnable action;

        private PlaceAction(final Supplier<List<ItemStack>> requirements, final Runnable action)
        {
            this.requirements = requirements;
            this.action = action;
        }

        public List<ItemStack> getRequirement()
        {
            return requirements.get();
        }

        public void perform()
        {
            action.run();
        }
    }

    private static class ActionIterator<I, B> implements Iterator<PlaceAction>
    {
        private final Iterator<I> backingIterator;
        private final Function<I, PlaceAction> iteratorTransformer;
        private final StageData<?, B> currentStage;
        private final B base;

        private ActionIterator(
            final B base,
            final StageData<?, B> currentStage,
            final Iterator<I> backingIterator,
            final Function<I, PlaceAction> iteratorTransformer)
        {
            this.backingIterator = backingIterator;
            this.iteratorTransformer = iteratorTransformer;
            this.currentStage = currentStage;
            this.base = base;
        }

        @Override
        public boolean hasNext()
        {
            if (!backingIterator.hasNext())
            {
                currentStage.endStage(base);
            }
            return true;
        }

        @Override
        public PlaceAction next()
        {
            if (!backingIterator.hasNext())
            {
                return null;
            }

            return iteratorTransformer.apply(backingIterator.next());
        }
    }
}
package com.ldtteam.structurize.pipeline.build;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.BlockStateComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.EntityComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.TileEntityComponentPlacer;
import com.ldtteam.structurize.structure.util.StructureBB;
import com.ldtteam.structurize.util.FastIterator;
import com.ldtteam.structurize.util.Stage;
import com.ldtteam.structurize.util.Stage.StageData;
import org.apache.commons.lang3.ObjectUtils.Null;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class StagedPlacer extends RawPlacer implements FastIterator<StagedPlacer.PlaceAction<?>>
{
    /*
     * Building stages, recommended order is the same as follows.
     */

    /**
     * Fixes holes using given block in a layer under the lowest structure layer.
     */
    public static final Stage<BlockState, StagedPlacer> FIX_FLOOR_WITH = Stages.BlockStateStages.FIX_FLOOR_WITH;
    /**
     * Fixes holes using given block in a layer above the highest structure layer.
     */
    public static final Stage<BlockState, StagedPlacer> FIX_CEILING_WITH = Stages.BlockStateStages.FIX_CEILING_WITH;
    /**
     * Replaces fluids using given block in ceiling and every wall.
     */
    public static final Stage<BlockState, StagedPlacer> FIX_FLUIDS_WITH = Stages.BlockStateStages.FIX_FLUIDS_WITH;
    /**
     * Clears structureBB with given block.
     */
    public static final Stage<BlockState, StagedPlacer> CLEAR_WITH = Stages.BlockStateStages.CLEAR_WITH;
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
    private StageData<?, StagedPlacer> currentStage; // because linkedList node is not exposed...
    private final BiConsumer<Stage<?, StagedPlacer>, Stage<?, StagedPlacer>> onStageChangeListener;
    private FastIterator<PlaceAction<?>> runningIterator;

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
        currentStage = this.stages.next();
        currentStage.runStage(this);
    }

    public static LinkedList<StageData<?, StagedPlacer>> createDefaultStages(final boolean requirements, final boolean supportFallingIfBottom)
    {
        final LinkedList<StageData<?, StagedPlacer>> defStages = new LinkedList<>();
        defStages.add(FIX_FLOOR_WITH.createData(Blocks.DIRT.getDefaultState()));
        defStages.add(FIX_CEILING_WITH.createData(Blocks.STONE.getDefaultState()));
        defStages.add(FIX_FLUIDS_WITH.createData(Blocks.GLASS.getDefaultState()));
        defStages.add(CLEAR_WITH.createData(Blocks.AIR.getDefaultState()));
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
        currentStage = stages.next();
        onStageChangeListener.accept(previous, currentStage.getStage());
        currentStage.runStage(this);
    }

    private Set<BlockPos> getNonSolidBlockPosIn(final StructureBB structureBB)
    {
        final Set<BlockPos> result = new HashSet<>();
        for (final BlockPos pos : structureBB.getPosIterator())
        {
            if (!structureWorld.getBlockState(pos).isSolid())
            {
                result.add(pos);
            }
        }
        return result;
    }

    /**
     * Fixes holes using given block in a layer under the lowest structure layer.
     */
    public void fixFloorWith(final StageData<BlockState, StagedPlacer> stageData)
    {
        final StructureBB toCheck = new StructureBB(structurePosition.getAnchor().down(), structurePosition.getPeek().down(structurePosition.getYSize()));
        final Set<BlockPos> nonSolidPosList = getNonSolidBlockPosIn(toCheck);

        if (nonSolidPosList.isEmpty())
        {
            stageData.endStage(this);
            return;
        }

        final BlockState fillBlock = stageData.getData();
        final BlockStateComponentPlacer blockStatePlacer = getBlockStatePlacer(fillBlock);
        runningIterator = new ActionIterator<BlockPos>(nonSolidPosList.iterator(), pos -> {
            return new PlaceAction<BlockState>(
                () -> blockStatePlacer.getRequirements(fillBlock, structureWorld, pos),
                () -> blockStatePlacer.place(fillBlock, structureWorld, pos),
                fillBlock,
                fillBlock.getBlock().getRegistryName(),
                structureWorld,
                pos);
        });
    }

    /**
     * Fixes holes using given block in a layer above the highest structure layer.
     */
    public void fixCeilingWith(final StageData<BlockState, StagedPlacer> stageData)
    {
        final StructureBB toCheck = new StructureBB(structurePosition.getAnchor().up(structurePosition.getYSize()), structurePosition.getPeek().up());
        final Set<BlockPos> nonSolidPosList = getNonSolidBlockPosIn(toCheck);

        if (nonSolidPosList.isEmpty())
        {
            stageData.endStage(this);
            return;
        }

        final BlockState fillBlock = stageData.getData();
        final BlockStateComponentPlacer blockStatePlacer = getBlockStatePlacer(fillBlock);
        runningIterator = new ActionIterator<BlockPos>(nonSolidPosList.iterator(), pos -> {
            return new PlaceAction<BlockState>(
                () -> blockStatePlacer.getRequirements(fillBlock, structureWorld, pos),
                () -> blockStatePlacer.place(fillBlock, structureWorld, pos),
                fillBlock,
                fillBlock.getBlock().getRegistryName(),
                structureWorld,
                pos);
        });
    }

    private void addFluidBlockPosFromInto(final StructureBB structureBB, final Set<BlockPos> fluidPosList)
    {
        for (final BlockPos pos : structureBB.getPosIterator())
        {
            if (!structureWorld.getBlockState(pos).getFluidState().isEmpty())
            {
                fluidPosList.add(pos);
            }
        }
    }

    /**
     * Replaces fluids using given block in ceiling and every wall.
     */
    public void fixFluidsWith(final StageData<BlockState, StagedPlacer> stageData)
    {
        final Set<BlockPos> fluidPosList = new HashSet<>();
        addFluidBlockPosFromInto(
            new StructureBB(structurePosition.getAnchor().west(), structurePosition.getPeek().west(structurePosition.getXSize())),
            fluidPosList);
        addFluidBlockPosFromInto(
            new StructureBB(structurePosition.getAnchor().north(), structurePosition.getPeek().north(structurePosition.getZSize())),
            fluidPosList);
        addFluidBlockPosFromInto(
            new StructureBB(structurePosition.getAnchor().east(structurePosition.getXSize()), structurePosition.getPeek().east()),
            fluidPosList);
        addFluidBlockPosFromInto(
            new StructureBB(structurePosition.getAnchor().south(structurePosition.getZSize()), structurePosition.getPeek().south()),
            fluidPosList);
        addFluidBlockPosFromInto(
            new StructureBB(structurePosition.getAnchor().up(structurePosition.getYSize()), structurePosition.getPeek().up()),
            fluidPosList);

        if (fluidPosList.isEmpty())
        {
            stageData.endStage(this);
            return;
        }

        final BlockState fillBlock = stageData.getData();
        final BlockStateComponentPlacer blockStatePlacer = getBlockStatePlacer(fillBlock);
        runningIterator = new ActionIterator<BlockPos>(fluidPosList.iterator(), pos -> {
            return new PlaceAction<BlockState>(
                () -> blockStatePlacer.getRequirements(fillBlock, structureWorld, pos),
                () -> blockStatePlacer.place(fillBlock, structureWorld, pos),
                fillBlock,
                fillBlock.getBlock().getRegistryName(),
                structureWorld,
                pos);
        });
    }

    /**
     * Clears structureBB with given block.
     */
    public void clearWith(final StageData<BlockState, StagedPlacer> stageData)
    {
        final BlockState fillBlock = stageData.getData();

        final List<List<BlockPos>> rowsToClear = new ArrayList<>();
        List<BlockPos> tempList = new ArrayList<>();
        final MutableBlockPos tempPos = new MutableBlockPos();

        for (int y = 0; y < structurePosition.getYSize(); y++)
        {
            tempPos.setY(y);
            for (int x = 0; x < structurePosition.getXSize(); x++)
            {
                tempPos.setX(x);
                for (int z = 0; z < structurePosition.getZSize(); z++)
                {
                    tempPos.setZ(z);
                    if (!structureWorld.getBlockState(tempPos).equals(fillBlock))
                    {
                        tempList.add(tempPos.toImmutable());
                    }
                }

                if (!tempList.isEmpty())
                {
                    rowsToClear.add(tempList);
                    tempList = new ArrayList<>();
                }
            }
        }

        if (rowsToClear.isEmpty())
        {
            stageData.endStage(this);
            return;
        }

        final BlockStateComponentPlacer blockStatePlacer = getBlockStatePlacer(fillBlock);
        runningIterator = new ActionIterator<List<BlockPos>>(rowsToClear.iterator(), posList -> {
            return new PlaceAction<BlockState>(() -> {
                final List<ItemStack> result = new ArrayList<>();
                for (final BlockPos pos : posList)
                {
                    blockStatePlacer.getRequirements(fillBlock, structureWorld, pos);
                }
                return result;
            }, () -> {
                for (final BlockPos pos : posList)
                {
                    blockStatePlacer.place(fillBlock, structureWorld, pos);
                }
            }, fillBlock, fillBlock.getBlock().getRegistryName(), structureWorld, null);
        });
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
        if (structureTileEntities.isEmpty())
        {
            stageData.endStage(this);
            return;
        }

        runningIterator = new ActionIterator<Map.Entry<BlockPos, CompoundNBT>>(structureTileEntities.entrySet().iterator(), entry -> {
            final Tuple<TileEntity, BlockPos> transformedTe = transformDataToTileEntity(entry.getValue(), entry.getKey());
            final TileEntity te = transformedTe.getA();
            final TileEntityComponentPlacer tePlacer = getTileEntityPlacer(te);

            return new PlaceAction<TileEntity>(
                () -> tePlacer.getRequirements(te, structureWorld, transformedTe.getB()),
                () -> tePlacer.place(te, structureWorld, transformedTe.getB()),
                te,
                te.getType().getRegistryName(),
                structureWorld,
                transformedTe.getB());
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
        if (structureEntities.isEmpty())
        {
            stageData.endStage(this);
            return;
        }

        runningIterator = new ActionIterator<CompoundNBT>(structureEntities.iterator(), entityNBT -> {
            final Tuple<Entity, Vec3d> transformedTe = transformDataToEntity(entityNBT);
            final Entity entity = transformedTe.getA();
            final BlockPos entityPos = new BlockPos(transformedTe.getB());
            final EntityComponentPlacer entityPlacer = getEntityPlacer(entity);

            return new PlaceAction<Entity>(
                () -> entityPlacer.getRequirements(entity, structureWorld, entityPos),
                () -> entityPlacer.place(entity, structureWorld, entityPos),
                entity,
                entity.getType().getRegistryName(),
                structureWorld,
                entityPos);
        });
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
    public PlaceAction<?> next()
    {
        final PlaceAction<?> result = runningIterator.next();

        if (!runningIterator.hasNext())
        {
            currentStage.endStage(this);
        }

        return result;
    }

    @Override
    public void fastConsume(final PlaceAction<?> placeAction)
    {
        runningIterator.fastConsume(placeAction);
    }

    public static class PlaceAction<T>
    {
        private final Supplier<List<ItemStack>> requirements;
        private final Runnable action;
        private final T objectToPlace;
        private final ResourceLocation objectRegistryName;
        private final World world;
        private final BlockPos pos;

        protected PlaceAction(
            final Supplier<List<ItemStack>> requirements,
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

        public Supplier<List<ItemStack>> getRequirements()
        {
            return requirements;
        }

        public void perform()
        {
            action.run();
        }

        public T getObjectToPlace()
        {
            return objectToPlace;
        }

        public ResourceLocation getObjectRegistryName()
        {
            return objectRegistryName;
        }

        public World getWorld()
        {
            return world;
        }

        public BlockPos getPos()
        {
            return pos;
        }
    }

    protected static class ActionIterator<I> implements FastIterator<PlaceAction<?>>
    {
        private final Iterator<I> backingIterator;
        private final Function<I, PlaceAction<?>> iteratorTransformer;

        protected ActionIterator(final Iterator<I> backingIterator, final Function<I, PlaceAction<?>> iteratorTransformer)
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
        public PlaceAction<?> next()
        {
            if (!hasNext())
            {
                return null;
            }

            return iteratorTransformer.apply(backingIterator.next());
        }

        @Override
        public void fastConsume(final PlaceAction<?> placeAction)
        {
            placeAction.perform();
        }
    }
}

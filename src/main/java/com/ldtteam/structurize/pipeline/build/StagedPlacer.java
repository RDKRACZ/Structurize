package com.ldtteam.structurize.pipeline.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import com.ldtteam.structurize.block.ModBlocks;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.BlockStateComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.EntityComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.FluidStateComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.TileEntityComponentPlacer;
import com.ldtteam.structurize.pipeline.build.StagedPlacerUtils.ActionIterator;
import com.ldtteam.structurize.pipeline.build.StagedPlacerUtils.BsWithTePlaceAction;
import com.ldtteam.structurize.pipeline.build.StagedPlacerUtils.DummyPlaceAction;
import com.ldtteam.structurize.pipeline.build.StagedPlacerUtils.PlaceAction;
import com.ldtteam.structurize.structure.util.StructureBB;
import com.ldtteam.structurize.util.BlockStateWithTileEntity;
import com.ldtteam.structurize.util.FastIterator;
import com.ldtteam.structurize.util.ItemStackList;
import com.ldtteam.structurize.util.Stage;
import com.ldtteam.structurize.util.Stage.StageData;
import org.apache.commons.lang3.ObjectUtils.Null;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

public class StagedPlacer extends RawPlacer implements FastIterator<PlaceAction<?>>
{
    /*
     * Building stages, recommended order is the same as follows.
     */

    /**
     * Fixes holes using given block in a layer under the lowest structure layer.
     */
    public static final Stage<Tuple<BlockState, Boolean>, StagedPlacer> FIX_FLOOR_WITH = Stages.BlockStateBooleanStages.FIX_FLOOR_WITH;
    /**
     * Fixes holes using given block in a layer above the highest structure layer.
     */
    public static final Stage<Tuple<BlockState, Boolean>, StagedPlacer> FIX_CEILING_WITH = Stages.BlockStateBooleanStages.FIX_CEILING_WITH;
    /**
     * Replaces fluids using given block in ceiling and every wall.
     */
    public static final Stage<Tuple<BlockState, Boolean>, StagedPlacer> FIX_FLUIDS_WITH = Stages.BlockStateBooleanStages.FIX_FLUIDS_WITH;
    /**
     * Clears structureBB with given block.
     */
    public static final Stage<Tuple<BlockState, Boolean>, StagedPlacer> CLEAR_WITH = Stages.BlockStateBooleanStages.CLEAR_WITH;
    /**
     * Places all solid blocks.
     */
    public static final Stage<Boolean, StagedPlacer> PLACE_SOLID = Stages.BooleanStages.PLACE_SOLID;
    /**
     * Places all falling blocks.
     */
    public static final Stage<Boolean, StagedPlacer> PLACE_FALLING = Stages.BooleanStages.PLACE_FALLING;
    /**
     * Places all non-solid blocks.
     */
    public static final Stage<Boolean, StagedPlacer> PLACE_NON_SOLID = Stages.BooleanStages.PLACE_NON_SOLID;
    /**
     * Places all fluids.
     */
    public static final Stage<Boolean, StagedPlacer> PLACE_FLUIDS = Stages.BooleanStages.PLACE_FLUIDS;
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
    private ActionIterator<?, ?> runningIterator;
    // TODO: current mc version does not support anything else than water
    private final Map<BlockPos, Tuple<BlockState, Boolean>> fluidloggedCache = new HashMap<>();
    private final Map<BlockPos, BlockState> fallingSupports = new HashMap<>();

    public StagedPlacer(final RawPlacer placer)
    {
        this(placer, null);
    }

    public StagedPlacer(final RawPlacer placer, final LinkedList<StageData<?, StagedPlacer>> stages)
    {
        this(placer, stages, (s, t) -> {
            // Dummy onStageChangeListener
        });
    }

    public StagedPlacer(
        final RawPlacer placer,
        final LinkedList<StageData<?, StagedPlacer>> stages,
        final BiConsumer<Stage<?, StagedPlacer>, Stage<?, StagedPlacer>> onStageChangeListener)
    {
        super(placer);
        this.stages = stages == null ? null : stages.iterator();
        this.onStageChangeListener = onStageChangeListener;
        nextStage(null);
    }

    public static LinkedList<StageData<?, StagedPlacer>> createDefaultStages(final boolean triggerPlayerActions)
    {
        final LinkedList<StageData<?, StagedPlacer>> defStages = new LinkedList<>();
        defStages.add(PLACE_SOLID.createData(triggerPlayerActions));
        defStages.add(PLACE_FALLING.createData(triggerPlayerActions));
        defStages.add(PLACE_NON_SOLID.createData(triggerPlayerActions));
        defStages.add(PLACE_FLUIDS.createData(triggerPlayerActions));
        defStages.add(PLACE_ENTITIES.createData(triggerPlayerActions));
        defStages.add(END_STAGE.createEmptyData());
        return defStages;
    }

    public void nextStage(final Stage<?, StagedPlacer> previous)
    {
        if (stages != null)
        {
            currentStage = stages.next();
            onStageChangeListener.accept(previous, currentStage.getStage());
            currentStage.runStage(this);
        }
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
    public void fixFloorWith(final StageData<Tuple<BlockState, Boolean>, StagedPlacer> stageData)
    {
        final StructureBB toCheck =
            new StructureBB(structurePosition.getAnchor().down(), structurePosition.getPeek().down(structurePosition.getYSize()));
        final Set<BlockPos> nonSolidPosList = getNonSolidBlockPosIn(toCheck);

        if (nonSolidPosList.isEmpty())
        {
            stageData.endStage(this);
            return;
        }

        final BlockState fillBlock = stageData.getData().getA();
        final BlockStateComponentPlacer blockStatePlacer = getBlockStatePlacer(fillBlock);
        runningIterator = new ActionIterator<BlockPos, BlockState>(nonSolidPosList.iterator(), pos -> {
            return new PlaceAction<>(
                () -> blockStatePlacer.getRequirements(fillBlock, structureWorld, pos, stageData.getData().getB()),
                () -> blockStatePlacer.place(fillBlock, structureWorld, pos, stageData.getData().getB()),
                fillBlock,
                fillBlock.getBlock().getRegistryName(),
                structureWorld,
                pos);
        });
    }

    /**
     * Fixes holes using given block in a layer above the highest structure layer.
     */
    public void fixCeilingWith(final StageData<Tuple<BlockState, Boolean>, StagedPlacer> stageData)
    {
        final StructureBB toCheck = new StructureBB(structurePosition.getAnchor().up(structurePosition.getYSize()), structurePosition.getPeek().up());
        final Set<BlockPos> nonSolidPosList = getNonSolidBlockPosIn(toCheck);

        if (nonSolidPosList.isEmpty())
        {
            stageData.endStage(this);
            return;
        }

        final BlockState fillBlock = stageData.getData().getA();
        final BlockStateComponentPlacer blockStatePlacer = getBlockStatePlacer(fillBlock);
        runningIterator = new ActionIterator<BlockPos, BlockState>(nonSolidPosList.iterator(), pos -> {
            return new PlaceAction<>(
                () -> blockStatePlacer.getRequirements(fillBlock, structureWorld, pos, stageData.getData().getB()),
                () -> blockStatePlacer.place(fillBlock, structureWorld, pos, stageData.getData().getB()),
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
    public void fixFluidsWith(final StageData<Tuple<BlockState, Boolean>, StagedPlacer> stageData)
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

        final BlockState fillBlock = stageData.getData().getA();
        final BlockStateComponentPlacer blockStatePlacer = getBlockStatePlacer(fillBlock);
        runningIterator = new ActionIterator<BlockPos, BlockState>(fluidPosList.iterator(), pos -> {
            return new PlaceAction<>(
                () -> blockStatePlacer.getRequirements(fillBlock, structureWorld, pos, stageData.getData().getB()),
                () -> blockStatePlacer.place(fillBlock, structureWorld, pos, stageData.getData().getB()),
                fillBlock,
                fillBlock.getBlock().getRegistryName(),
                structureWorld,
                pos);
        });
    }

    /**
     * Clears structureBB with given block.
     */
    public void clearWith(final StageData<Tuple<BlockState, Boolean>, StagedPlacer> stageData)
    {
        // solid blocks should use single action instead of multi action? fluids are ... nice to see ...
        final BlockState fillBlock = stageData.getData().getA();

        final List<List<BlockPos>> rowsToClear = new ArrayList<>();
        List<BlockPos> tempList = new ArrayList<>();
        final MutableBlockPos tempPos = new MutableBlockPos();
        final BlockPos start = structurePosition.getAnchor();
        final BlockPos end = structurePosition.getPeek();

        for (int y = start.getY(); y <= end.getY(); y++)
        {
            tempPos.setY(y);
            for (int z = start.getZ(); z <= end.getZ(); z++)
            {
                tempPos.setZ(z);
                for (int x = start.getX(); x <= end.getX(); x++)
                {
                    tempPos.setX(x);
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
        runningIterator = new ActionIterator<List<BlockPos>, BlockState>(rowsToClear.iterator(), posList -> {
            return new PlaceAction<>(() -> {
                final ItemStackList result = new ItemStackList();
                for (final BlockPos pos : posList)
                {
                    result.addAll(blockStatePlacer.getRequirements(fillBlock, structureWorld, pos, stageData.getData().getB()));
                }
                return (ArrayList<ItemStack>) result;
            }, () -> {
                for (final BlockPos pos : posList)
                {
                    blockStatePlacer.place(fillBlock, structureWorld, pos, stageData.getData().getB());
                }
            }, fillBlock, fillBlock.getBlock().getRegistryName(), structureWorld, null);
        });
    }

    private List<Tuple<BlockPos, BlockState>> searchStructureBlocksForIDs(final List<Short> blockStatesIDs)
    {
        final List<Tuple<BlockPos, BlockState>> result = new ArrayList<>();

        if (blockStatesIDs.isEmpty())
        {
            return result;
        }

        for (int y = 0; y < structurePosition.getYSize(); y++)
        {
            for (int z = 0; z < structurePosition.getZSize(); z++)
            {
                for (int x = 0; x < structurePosition.getXSize(); x++)
                {
                    final short id = structureBlocks[y][z][x];
                    if (blockStatesIDs.contains(id))
                    {
                        result.add(new Tuple<>(new BlockPos(x, y, z), structureBlockPalette.get(id)));
                    }
                }
            }
        }
        return result;
    }

    private boolean isBlockstateSolid(final BlockState blockState)
    {
        return blockState.getMaterial().isSolid() &&
            !(blockState.getBlock() instanceof WallSignBlock || blockState.getBlock() instanceof WallBannerBlock);
    }

    /**
     * Places all solid blocks.
     */
    public void placeSolid(final StageData<Boolean, StagedPlacer> stageData)
    {
        final List<Short> blockStatesIDs = new ArrayList<>();
        for (short i = 0; i < structureBlockPalette.size(); i++)
        {
            if (isBlockstateSolid(structureBlockPalette.get(i)) && !(structureBlockPalette.get(i).getBlock() instanceof FallingBlock))
            {
                blockStatesIDs.add(i);
            }
        }

        if (blockStatesIDs.isEmpty())
        {
            stageData.endStage(this);
            return;
        }

        final List<Tuple<BlockPos, BlockState>> solidBlocks = searchStructureBlocksForIDs(blockStatesIDs);

        runningIterator = new ActionIterator<Tuple<BlockPos, BlockState>, BlockStateWithTileEntity>(solidBlocks.iterator(), entry -> {
            final BlockState bs = entry.getB();
            final BlockStateComponentPlacer blockStatePlacer = getBlockStatePlacer(bs);
            final Boolean wasWaterlogged = bs.has(BlockStateProperties.WATERLOGGED) ? bs.get(BlockStateProperties.WATERLOGGED) : false;
            final BlockPos realWorldPos = structurePosition.transformZeroBasedToReal(entry.getA());

            final CompoundNBT teNBT = structureTileEntities.get(entry.getA());
            final Tuple<TileEntity, BlockPos> transformedTe = teNBT == null ? null : transformDataToTileEntity(teNBT, entry.getA());
            final TileEntity te = transformedTe == null ? null : transformedTe.getA();
            final TileEntityComponentPlacer tePlacer = te == null ? null : getTileEntityPlacer(te);

            final BlockState modifiedBlockState;
            if (wasWaterlogged)
            {
                fluidloggedCache.put(entry.getA(), new Tuple<>(bs, wasWaterlogged));
                modifiedBlockState = bs.with(BlockStateProperties.WATERLOGGED, false);
            }
            else
            {
                modifiedBlockState = bs;
            }

            return new BsWithTePlaceAction(
                new BlockStateWithTileEntity(modifiedBlockState, te),
                blockStatePlacer,
                tePlacer,
                structureWorld,
                realWorldPos,
                stageData.getData());
        });
    }

    /**
     * Places all falling blocks.
     */
    public void placeFalling(final StageData<Boolean, StagedPlacer> stageData)
    {
        final List<Short> blockStatesIDs = new ArrayList<>();
        for (short i = 0; i < structureBlockPalette.size(); i++)
        {
            if (isBlockstateSolid(structureBlockPalette.get(i)) && structureBlockPalette.get(i).getBlock() instanceof FallingBlock)
            {
                blockStatesIDs.add(i);
            }
        }

        if (blockStatesIDs.isEmpty())
        {
            stageData.endStage(this);
            return;
        }

        final List<Tuple<BlockPos, BlockState>> fallingBlocks = searchStructureBlocksForIDs(blockStatesIDs);

        runningIterator = new ActionIterator<Tuple<BlockPos, BlockState>, BlockStateWithTileEntity>(fallingBlocks.iterator(), entry -> {
            final BlockState bs = entry.getB();
            final BlockStateComponentPlacer blockStatePlacer = getBlockStatePlacer(bs);
            final Boolean wasWaterlogged = bs.has(BlockStateProperties.WATERLOGGED) ? bs.get(BlockStateProperties.WATERLOGGED) : false;
            final BlockPos realWorldPos = structurePosition.transformZeroBasedToReal(entry.getA());

            final CompoundNBT teNBT = structureTileEntities.get(entry.getA());
            final Tuple<TileEntity, BlockPos> transformedTe = teNBT == null ? null : transformDataToTileEntity(teNBT, entry.getA());
            final TileEntity te = transformedTe == null ? null : transformedTe.getA();
            final TileEntityComponentPlacer tePlacer = te == null ? null : getTileEntityPlacer(te);

            final BlockState modifiedBlockState;
            if (wasWaterlogged)
            {
                fluidloggedCache.put(entry.getA(), new Tuple<>(bs, wasWaterlogged));
                modifiedBlockState = bs.with(BlockStateProperties.WATERLOGGED, false);
            }
            else
            {
                modifiedBlockState = bs;
            }

            final PlaceAction<?> support;
            final BlockPos supportPos = realWorldPos.down();
            final BlockState currentSupportBlockState = structureWorld.getBlockState(supportPos);
            if (currentSupportBlockState.getBlock() instanceof AirBlock && entry.getA().getY() > 0)
            {
                final BlockState supportBlockState = ModBlocks.FALLING_SUPPORT.getDefaultState();
                fallingSupports.put(supportPos, currentSupportBlockState);
                support = new PlaceAction<BlockState>(
                    () -> new ArrayList<>(),
                    () -> getBlockStatePlacer(supportBlockState).place(supportBlockState, structureWorld, supportPos, false),
                    supportBlockState,
                    null,
                    structureWorld,
                    supportPos);
            }
            else
            {
                support = new DummyPlaceAction();
            }

            return new BsWithTePlaceAction(
                new BlockStateWithTileEntity(modifiedBlockState, te),
                blockStatePlacer,
                tePlacer,
                structureWorld,
                realWorldPos,
                stageData.getData(),
                support);
        });
    }

    /**
     * Places all non-solid blocks.
     */
    public void placeNonSolid(final StageData<Boolean, StagedPlacer> stageData)
    {
        final List<Short> blockStatesIDs = new ArrayList<>();
        for (short i = 0; i < structureBlockPalette.size(); i++)
        {
            if (!isBlockstateSolid(structureBlockPalette.get(i)))
            {
                blockStatesIDs.add(i);
            }
        }

        if (blockStatesIDs.isEmpty())
        {
            stageData.endStage(this);
            return;
        }

        final List<Tuple<BlockPos, BlockState>> nonSolidBlocks = searchStructureBlocksForIDs(blockStatesIDs);

        runningIterator = new ActionIterator<Tuple<BlockPos, BlockState>, BlockStateWithTileEntity>(nonSolidBlocks.iterator(), entry -> {
            final BlockState bs = entry.getB();
            final BlockStateComponentPlacer blockStatePlacer = getBlockStatePlacer(bs);
            final Boolean wasWaterlogged = bs.has(BlockStateProperties.WATERLOGGED) ? bs.get(BlockStateProperties.WATERLOGGED) : false;
            final BlockPos realWorldPos = structurePosition.transformZeroBasedToReal(entry.getA());

            final CompoundNBT teNBT = structureTileEntities.get(entry.getA());
            final Tuple<TileEntity, BlockPos> transformedTe = teNBT == null ? null : transformDataToTileEntity(teNBT, entry.getA());
            final TileEntity te = transformedTe == null ? null : transformedTe.getA();
            final TileEntityComponentPlacer tePlacer = te == null ? null : getTileEntityPlacer(te);

            final BlockState modifiedBlockState;
            if (wasWaterlogged)
            {
                fluidloggedCache.put(entry.getA(), new Tuple<>(bs, wasWaterlogged));
                modifiedBlockState = bs.with(BlockStateProperties.WATERLOGGED, false);
            }
            else
            {
                modifiedBlockState = bs;
            }

            return new BsWithTePlaceAction(
                new BlockStateWithTileEntity(modifiedBlockState, te),
                blockStatePlacer,
                tePlacer,
                structureWorld,
                realWorldPos,
                stageData.getData());
        });
    }

    /**
     * Places all fluids.
     */
    public void placeFluids(final StageData<Boolean, StagedPlacer> stageData)
    {
        final List<Short> blockStatesIDs = new ArrayList<>();
        for (short i = 0; i < structureBlockPalette.size(); i++)
        {
            final BlockState bs = structureBlockPalette.get(i);
            if (bs.getBlock() instanceof FlowingFluidBlock && !bs.getFluidState().isEmpty())
            {
                blockStatesIDs.add(i);
            }
        }

        if (blockStatesIDs.isEmpty() && fluidloggedCache.isEmpty())
        {
            stageData.endStage(this);
            return;
        }

        final ActionIterator<?, IFluidState> fluidloggedIterator =
            new ActionIterator<Map.Entry<BlockPos, Tuple<BlockState, Boolean>>, IFluidState>(fluidloggedCache.entrySet().iterator(), entry -> {
                final BlockState blockState = entry.getValue().getA();

                if (!(blockState instanceof IWaterLoggable))
                {
                    return null;
                }

                final IFluidState fluidState = blockState.getFluidState();
                final FluidStateComponentPlacer fluidStatePlacer = getFluidStatePlacer(fluidState);
                final BlockPos realWorldPos = structurePosition.transformZeroBasedToReal(entry.getKey());

                return new PlaceAction<>(
                    () -> fluidStatePlacer.getRequirements(fluidState, structureWorld, realWorldPos, stageData.getData()),
                    () -> new IWaterLoggable() {
                    }.receiveFluid(structureWorld, realWorldPos, blockState, fluidState),
                    fluidState,
                    fluidState.getFluid().getRegistryName(),
                    structureWorld,
                    realWorldPos);
            });

        if (blockStatesIDs.isEmpty())
        {
            runningIterator = fluidloggedIterator;
            return;
        }

        final List<Tuple<BlockPos, BlockState>> fluids = searchStructureBlocksForIDs(blockStatesIDs);

        runningIterator = new ActionIterator<Tuple<BlockPos, BlockState>, IFluidState>(fluids.iterator(), entry -> {
            final IFluidState fluidState = entry.getB().getFluidState();
            final FluidStateComponentPlacer fluidStatePlacer = getFluidStatePlacer(fluidState);
            final BlockPos realWorldPos = structurePosition.transformZeroBasedToReal(entry.getA());

            return new PlaceAction<>(
                () -> fluidStatePlacer.getRequirements(fluidState, structureWorld, realWorldPos, stageData.getData()),
                () -> fluidStatePlacer.place(fluidState, structureWorld, realWorldPos, stageData.getData()),
                fluidState,
                fluidState.getFluid().getRegistryName(),
                structureWorld,
                realWorldPos);
        }) {
            @Override
            public ActionIterator<?, IFluidState> successor()
            {
                return fluidloggedCache.isEmpty() ? null : fluidloggedIterator;
            }
        };
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

        runningIterator = new ActionIterator<CompoundNBT, Entity>(structureEntities.iterator(), entityNBT -> {
            final Tuple<Entity, Vec3d> transformedEntity = transformDataToEntity(entityNBT);
            final Entity entity = transformedEntity.getA();
            final BlockPos entityPos = new BlockPos(transformedEntity.getB());
            final EntityComponentPlacer entityPlacer = getEntityPlacer(entity);

            return new PlaceAction<>(
                () -> entityPlacer.getRequirements(entity, structureWorld, entityPos, stageData.getData()),
                () -> entityPlacer.place(entity, structureWorld, entityPos, stageData.getData()),
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
     * Clears every remaining falling support block.
     */
    public void endStage(final StageData<Null, StagedPlacer> stageData)
    {
        for (final Map.Entry<BlockPos, BlockState> supportCandidate : fallingSupports.entrySet())
        {
            if (structureWorld.getBlockState(supportCandidate.getKey()).getBlock() == ModBlocks.FALLING_SUPPORT)
            {
                getBlockStatePlacer(supportCandidate.getValue()).place(supportCandidate.getValue(), structureWorld, supportCandidate.getKey(), false);
            }
        }
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
            final ActionIterator<?, ?> newIterator = runningIterator.successor();

            if (newIterator != null)
            {
                // Kinda hacky but we assume using ActionIterator#fastConsume() so the action returned by this method ends up there
                runningIterator = newIterator;
            }
            else
            {
                currentStage.endStage(this);
            }
        }

        return result;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"}) // java is incompetent to generic properly
    public void fastConsume(final PlaceAction<?> placeAction)
    {
        runningIterator.fastConsume((PlaceAction) placeAction);
    }
}

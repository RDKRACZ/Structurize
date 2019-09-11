package com.ldtteam.structurize.pipeline.build;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import com.ldtteam.structurize.util.Stage;
import org.apache.commons.lang3.ObjectUtils.Null;
import net.minecraft.block.BlockState;
import net.minecraft.util.Tuple;

public final class Stages
{
    public enum BlockStateStages implements Stage<BlockState, StagedPlacer>
    {
        FIX_FLOOR_WITH(StagedPlacer::fixFloorWith, StagedPlacer::nextStage),
        FIX_CEILING_WITH(StagedPlacer::fixCeilingWith, StagedPlacer::nextStage),
        FIX_FLUIDS_WITH(StagedPlacer::fixFluidsWith, StagedPlacer::nextStage),
        CLEAR_WITH(StagedPlacer::clearWith, StagedPlacer::nextStage);

        private BiConsumer<StagedPlacer, StageData<BlockState, StagedPlacer>> method;
        private BiConsumer<StagedPlacer, Stage<BlockState, StagedPlacer>> then;

        private BlockStateStages(
            BiConsumer<StagedPlacer, StageData<BlockState, StagedPlacer>> method,
            BiConsumer<StagedPlacer, Stage<BlockState, StagedPlacer>> then)
        {
            this.method = method;
            this.then = then;
        }

        @Override
        public BiConsumer<StagedPlacer, StageData<BlockState, StagedPlacer>> getRunMethod()
        {
            return method;
        }

        @Override
        public BiConsumer<StagedPlacer, Stage<BlockState, StagedPlacer>> getThenMethod()
        {
            return then;
        }

        @Override
        public StageData<BlockState, StagedPlacer> createEmptyData()
        {
            throw new IllegalStateException("Must have stageData set.");
        }
    }

    public enum BooleanStages implements Stage<Boolean, StagedPlacer>
    {
        PLACE_SOLID(StagedPlacer::placeSolid, StagedPlacer::nextStage),
        PLACE_NON_SOLID(StagedPlacer::placeNonSolid, StagedPlacer::nextStage),
        PLACE_FLUIDS(StagedPlacer::placeFluids, StagedPlacer::nextStage),
        PLACE_TILE_ENTITIES(StagedPlacer::placeTileEntities, StagedPlacer::nextStage),
        PLACE_ENTITIES(StagedPlacer::placeEntities, StagedPlacer::nextStage);

        private BiConsumer<StagedPlacer, StageData<Boolean, StagedPlacer>> method;
        private BiConsumer<StagedPlacer, Stage<Boolean, StagedPlacer>> then;

        private BooleanStages(BiConsumer<StagedPlacer, StageData<Boolean, StagedPlacer>> method, BiConsumer<StagedPlacer, Stage<Boolean, StagedPlacer>> then)
        {
            this.method = method;
            this.then = then;
        }

        @Override
        public BiConsumer<StagedPlacer, StageData<Boolean, StagedPlacer>> getRunMethod()
        {
            return method;
        }

        @Override
        public BiConsumer<StagedPlacer, Stage<Boolean, StagedPlacer>> getThenMethod()
        {
            return then;
        }

        @Override
        public StageData<Boolean, StagedPlacer> createEmptyData()
        {
            throw new IllegalStateException("Must have stageData set.");
        }
    }

    public enum DoubleBooleanStages implements Stage<Tuple<Boolean, Boolean>, StagedPlacer>
    {
        PLACE_FALLING(StagedPlacer::placeFalling, StagedPlacer::nextStage);

        private BiConsumer<StagedPlacer, StageData<Tuple<Boolean, Boolean>, StagedPlacer>> method;
        private BiConsumer<StagedPlacer, Stage<Tuple<Boolean, Boolean>, StagedPlacer>> then;

        private DoubleBooleanStages(
            BiConsumer<StagedPlacer, StageData<Tuple<Boolean, Boolean>, StagedPlacer>> method,
            BiConsumer<StagedPlacer, Stage<Tuple<Boolean, Boolean>, StagedPlacer>> then)
        {
            this.method = method;
            this.then = then;
        }

        @Override
        public BiConsumer<StagedPlacer, StageData<Tuple<Boolean, Boolean>, StagedPlacer>> getRunMethod()
        {
            return method;
        }

        @Override
        public BiConsumer<StagedPlacer, Stage<Tuple<Boolean, Boolean>, StagedPlacer>> getThenMethod()
        {
            return then;
        }

        @Override
        public StageData<Tuple<Boolean, Boolean>, StagedPlacer> createEmptyData()
        {
            throw new IllegalStateException("Must have stageData set.");
        }
    }

    public enum NoDataStages implements Stage<Null, StagedPlacer>
    {
        CLEAR_FALLING_SUPPORT(StagedPlacer::clearFallingSupport, StagedPlacer::nextStage),
        END_STAGE(StagedPlacer::endStage, null),
        DUMMY_STAGE(null, null);

        private BiConsumer<StagedPlacer, StageData<Null, StagedPlacer>> method;
        private BiConsumer<StagedPlacer, Stage<Null, StagedPlacer>> then;

        private NoDataStages(BiConsumer<StagedPlacer, StageData<Null, StagedPlacer>> method, BiConsumer<StagedPlacer, Stage<Null, StagedPlacer>> then)
        {
            this.method = method;
            this.then = then;
        }

        @Override
        public BiConsumer<StagedPlacer, StageData<Null, StagedPlacer>> getRunMethod()
        {
            return method;
        }

        @Override
        public BiConsumer<StagedPlacer, Stage<Null, StagedPlacer>> getThenMethod()
        {
            return then;
        }

        @Override
        public StageData<Null, StagedPlacer> createData(final Null data)
        {
            throw new IllegalStateException("Can not have stageData set.");
        }
    }

    public enum CustomStage implements Stage<Supplier<Runnable>, StagedPlacer>
    {
        CUSTOM_STAGE(StagedPlacer::customStage, StagedPlacer::nextStage);

        private BiConsumer<StagedPlacer, StageData<Supplier<Runnable>, StagedPlacer>> method;
        private BiConsumer<StagedPlacer, Stage<Supplier<Runnable>, StagedPlacer>> then;

        private CustomStage(
            BiConsumer<StagedPlacer, StageData<Supplier<Runnable>, StagedPlacer>> method,
            BiConsumer<StagedPlacer, Stage<Supplier<Runnable>, StagedPlacer>> then)
        {
            this.method = method;
            this.then = then;
        }

        @Override
        public BiConsumer<StagedPlacer, StageData<Supplier<Runnable>, StagedPlacer>> getRunMethod()
        {
            return method;
        }

        @Override
        public BiConsumer<StagedPlacer, Stage<Supplier<Runnable>, StagedPlacer>> getThenMethod()
        {
            return then;
        }

        @Override
        public StageData<Supplier<Runnable>, StagedPlacer> createEmptyData()
        {
            throw new IllegalStateException("Must have stageData set.");
        }
    }
}
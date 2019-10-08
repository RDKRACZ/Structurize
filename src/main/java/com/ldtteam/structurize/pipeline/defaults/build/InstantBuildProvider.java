package com.ldtteam.structurize.pipeline.defaults.build;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.pipeline.build.BuildProvider;
import com.ldtteam.structurize.pipeline.build.RawPlacer;
import com.ldtteam.structurize.pipeline.build.StagedPlacer;
import com.ldtteam.structurize.util.GenericConfiguration.BooleanConfigValue;
import com.ldtteam.structurize.util.Stage.StageData;

public class InstantBuildProvider extends BuildProvider
{
    private final BooleanConfigValue placeInsteadUsePlaceholders;
    private static final AtomicBoolean stageStop = new AtomicBoolean(false);
    private static StagedPlacer currentPlacer;

    public InstantBuildProvider()
    {
        super("default");
        placeInsteadUsePlaceholders = getConfiguration().newBooleanValue().setDefaultValue(false).setNameKey("").setDescriptionKey("").build();
    }

    @Override
    public void build(final RawPlacer placerIn, final boolean worldLoad)
    {
        final LinkedList<StageData<?, StagedPlacer>> stages = StagedPlacer.createDefaultStages(placeInsteadUsePlaceholders.getValue());
        // stages.removeFirst(); // don't need the clean phase
        currentPlacer = new StagedPlacer(placerIn, stages, (prev, next) -> {
            Instances.getLogger()
                .info("Stage change: FROM {} | TO {}", prev == null ? "NULL" : prev.toString(), next == null ? "NULL" : next.toString());
            stageStop.set(true);
        });
    }

    public static void runStage()
    {
        stageStop.set(false);
        // TODO: hook action counter, probably lower level???
        currentPlacer.forNextRemaining(stageStop::get);
    }
}

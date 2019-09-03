package com.ldtteam.structurize.pipeline;

import com.ldtteam.structurize.pipeline.build.BuildProvider;
import net.minecraftforge.registries.IForgeRegistry;

public class BuildRegistries
{
    private final IForgeRegistry<BuildProvider> buildProviderRegistry;

    public BuildRegistries()
    {
        buildProviderRegistry = ComponentRegistries.buildRegistry(BuildProvider.class, "build_providers").create();
    }

    public IForgeRegistry<BuildProvider> getBuildProviderRegistry()
    {
        return buildProviderRegistry;
    }
}
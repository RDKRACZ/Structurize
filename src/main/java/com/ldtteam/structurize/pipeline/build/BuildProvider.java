package com.ldtteam.structurize.pipeline.build;

import com.ldtteam.structurize.pipeline.defaults.build.InstantBuildProvider;
import com.ldtteam.structurize.util.GenericConfiguration;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class BuildProvider extends ForgeRegistryEntry<BuildProvider>
{
    private final String translationKey;
    private GenericConfiguration configuration;

    public BuildProvider(final String translationKey)
    {
        this.translationKey = translationKey;
        this.configuration = new GenericConfiguration();
    }

    public void freezeConfiguration()
    {
        configuration.freeze();
    }

    public abstract void build(final RawPlacer placer);

    public static void oldInstantPlaceCaller(final EventInfoHolder<?> event)
    {
        InstantBuildProvider.place(event);
    }

    public GenericConfiguration getConfiguration()
    {
        return configuration;
    }

    public String getTranslationKey()
    {
        return translationKey;
    }
}

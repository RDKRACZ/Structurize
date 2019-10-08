package com.ldtteam.structurize.pipeline.build;

import com.ldtteam.structurize.util.GenericConfiguration;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Provides placer consumer for building structures.
 * Implementer is responsible for correct manipulation with placer, as well as correct event ending.
 * TODO: rework configuration
 */
public abstract class BuildProvider extends ForgeRegistryEntry<BuildProvider>
{
    private final String translationKey;
    private GenericConfiguration configuration;

    /**
     * Creates and prepares registry item
     *
     * @param translationKey gui name, registry key
     */
    public BuildProvider(final String translationKey)
    {
        this.translationKey = translationKey;
        this.configuration = new GenericConfiguration();
    }

    /**
     * Every call of this method means that event was passed to you.
     * You should save placer into your build queue as this method can be fired unlimited times per single tick.
     * Placer is saved on world unload. This method is fired again for the same event on world load.
     * //TODO: @param config config value instance from gui
     * 
     * @param placer    event placer
     * @param worldLoad true if event was fired from save in world load
     */
    public abstract void build(final RawPlacer placer, final boolean worldLoad);

    public GenericConfiguration getConfiguration()
    {
        return configuration;
    }

    public String getTranslationKey()
    {
        return translationKey;
    }
}

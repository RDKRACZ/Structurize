package com.ldtteam.structurize.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Mod client configuration.
 * Loaded clientside, not synced.
 */
public class ClientConfiguration extends AbstractConfiguration
{
    /**
     * How many build previews can be rendered at one time.
     */
    public final ForgeConfigSpec.IntValue maxAmountOfRenderedEvents;

    /**
     * How many prerendered structures can be cached at one time.
     */
    public final ForgeConfigSpec.IntValue prerendedStructureCacheSize;

    /**
     * Builds client configuration.
     *
     * @param builder config builder
     */
    protected ClientConfiguration(final ForgeConfigSpec.Builder builder)
    {
        createCategory(builder, "render");
        maxAmountOfRenderedEvents = defineInteger(builder, "maxamountofrenderedevents", 10, 1, Integer.MAX_VALUE);
        prerendedStructureCacheSize = defineInteger(builder.worldRestart(), "prerendedstructurecachesize", 40, 5, Integer.MAX_VALUE);
        finishCategory(builder);
    }
}

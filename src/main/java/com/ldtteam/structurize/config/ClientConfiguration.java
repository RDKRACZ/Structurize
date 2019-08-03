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
     * Should texture of light (anyblock) placeholder be toggleable (visible/invisible states)?
     */
    public final ForgeConfigSpec.BooleanValue toggleableLightTexture;

    /**
     * Builds client configuration.
     *
     * @param builder config builder
     */
    protected ClientConfiguration(final ForgeConfigSpec.Builder builder)
    {
        createCategory(builder, "render");
        maxAmountOfRenderedEvents = defineInteger(builder, "maxamountofrenderedevents", 10, 1, Integer.MAX_VALUE);
        toggleableLightTexture = defineBoolean(builder, "toggleablelighttexture", true);
        finishCategory(builder);
    }
}

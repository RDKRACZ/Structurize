package com.ldtteam.structurize.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Mod common configuration.
 * Loaded everywhere, not synced.
 */
public class CommonConfiguration extends AbstractConfiguration
{
    /**
     * Should BuildTool remains after crafting when being a part of crafting recipe?
     */
    public final ForgeConfigSpec.BooleanValue buildToolSurvivesCrafting;

    /**
     * Builds common configuration.
     *
     * @param builder config builder
     */
    protected CommonConfiguration(final ForgeConfigSpec.Builder builder)
    {
        createCategory(builder, "general");
        buildToolSurvivesCrafting = defineBoolean(builder.worldRestart(), "buildtoolsurvivescrafting", true);
        finishCategory(builder);
    }
}
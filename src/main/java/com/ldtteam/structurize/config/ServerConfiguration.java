package com.ldtteam.structurize.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Mod server configuration.
 * Loaded serverside, synced on connection.
 */
public class ServerConfiguration extends AbstractConfiguration
{
    /**
     * Should BuildTool remains after crafting when being a part of crafting recipe?
     */
    public final ForgeConfigSpec.BooleanValue buildToolSurvivesCrafting;

    /**
     * Builds server configuration.
     *
     * @param builder config builder
     */
    protected ServerConfiguration(final ForgeConfigSpec.Builder builder)
    {
        createCategory(builder, "general");
        buildToolSurvivesCrafting = defineBoolean(builder, "buildtoolsurvivescrafting", true);
        finishCategory(builder);
    }
}

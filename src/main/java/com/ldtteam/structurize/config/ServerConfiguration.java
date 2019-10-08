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
     * Max world IO operations per tick (block, tile entity or entity placing, removing etc.).
     * TODO: create server instance, which will handle this across multiple running BPs
     */
    public final ForgeConfigSpec.IntValue maxWorldIoOperationsPerTick;

    /**
     * Builds server configuration.
     *
     * @param builder config builder
     */
    protected ServerConfiguration(final ForgeConfigSpec.Builder builder)
    {
        createCategory(builder, "general");
        buildToolSurvivesCrafting = defineBoolean(builder, "buildToolSurvivesCrafting", true);
        maxWorldIoOperationsPerTick = defineInteger(builder, "maxWorldIoOperationsPerTick", 1000);
        finishCategory(builder);
    }
}

package com.ldtteam.structurize.pipeline;

import com.ldtteam.structurize.pipeline.build.BlockInfoPlacer;
import com.ldtteam.structurize.pipeline.build.BuildProvider;
import com.ldtteam.structurize.pipeline.build.EntityPlacer;
import com.ldtteam.structurize.pipeline.defaults.build.DefaultPlacers;
import com.ldtteam.structurize.pipeline.defaults.scan.DefaultScanners;
import com.ldtteam.structurize.pipeline.scan.BlockInfoScanner;
import com.ldtteam.structurize.pipeline.scan.EntityScanner;
import com.ldtteam.structurize.util.constant.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

public class ComponentRegistries
{
    private static final ResourceLocation DEFAULT_KEY = new ResourceLocation(Constants.MOD_ID, "default");
    private static final String DEFAULT_KEY_STRING = DEFAULT_KEY.toString();

    private final IForgeRegistry<BlockInfoScanner> blockInfoScannerRegistry;
    private final IForgeRegistry<EntityScanner<Entity>> entityScannerRegistry;
    private final IForgeRegistry<BlockInfoPlacer> blockInfoPlacerRegistry;
    private final IForgeRegistry<EntityPlacer<Entity>> entityPlacerRegistry;
    private final IForgeRegistry<BuildProvider> buildProviderRegistry;

    public ComponentRegistries()
    {
        blockInfoScannerRegistry = buildRegistry(BlockInfoScanner.class, "blockinfo_scanners").create();
        entityScannerRegistry = buildRegistry(EntityScanner.class, "entity_scanners").create();
        blockInfoPlacerRegistry = buildRegistry(BlockInfoPlacer.class, "blockinfo_placers").create();
        entityPlacerRegistry = buildRegistry(EntityPlacer.class, "entity_placers").create();
        buildProviderRegistry = buildRegistry(BuildProvider.class, "build_providers").create();
    }

    protected static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> buildRegistry(final Class<T> clazz, final String name)
    {
        return new RegistryBuilder<T>().setName(new ResourceLocation(Constants.MOD_ID, name))
            .setDefaultKey(DEFAULT_KEY)
            .disableSaving()
            .setType(clazz)
            .setIDRange(0, Integer.MAX_VALUE - 1);
    }

    public void registerDefaultBIS(final IForgeRegistry<BlockInfoScanner> registry)
    {
        registry.register(DefaultScanners.getDefaultBlockInfoScanner().setRegistryName(DEFAULT_KEY_STRING));
    }

    public void registerDefaultES(final IForgeRegistry<EntityScanner<Entity>> registry)
    {
        registry.register(DefaultScanners.getDefaultEntityScanner().setRegistryName(DEFAULT_KEY_STRING));
    }

    public void registerDefaultBIP(final IForgeRegistry<BlockInfoPlacer> registry)
    {
        registry.register(DefaultPlacers.getDefaultBlockInfoPlacer().setRegistryName(DEFAULT_KEY_STRING));
    }

    public void registerDefaultEP(final IForgeRegistry<EntityPlacer<Entity>> registry)
    {
        registry.register(DefaultPlacers.getDefaultEntityPlacer().setRegistryName(DEFAULT_KEY_STRING));
    }

    public void registerDefaultBP(final IForgeRegistry<BuildProvider> registry)
    {
        // registry.registerAll(new InstantBuildProvider().setRegistryName(DEFAULT_KEY_STRING));
    }

    public IForgeRegistry<BlockInfoScanner> getBlockInfoScannerRegistry()
    {
        return blockInfoScannerRegistry;
    }

    public IForgeRegistry<EntityScanner<Entity>> getEntityScannerRegistry()
    {
        return entityScannerRegistry;
    }

    public IForgeRegistry<BlockInfoPlacer> getBlockInfoPlacerRegistry()
    {
        return blockInfoPlacerRegistry;
    }

    public IForgeRegistry<EntityPlacer<Entity>> getEntityPlacerRegistry()
    {
        return entityPlacerRegistry;
    }

    public IForgeRegistry<BuildProvider> getBuildProviderRegistry()
    {
        return buildProviderRegistry;
    }
}

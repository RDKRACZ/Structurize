package com.ldtteam.structurize.pipeline;

import com.ldtteam.structurize.pipeline.build.BuildProvider;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.BlockStateComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.EntityComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.FluidStateComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.TileEntityComponentPlacer;
import com.ldtteam.structurize.pipeline.defaults.build.DefaultPlacers;
import com.ldtteam.structurize.pipeline.defaults.build.InstantBuildProvider;
import com.ldtteam.structurize.pipeline.defaults.scan.DefaultScanners;
import com.ldtteam.structurize.pipeline.scan.ComponentScanner.BlockStateComponentScanner;
import com.ldtteam.structurize.pipeline.scan.ComponentScanner.EntityComponentScanner;
import com.ldtteam.structurize.pipeline.scan.ComponentScanner.TileEntityComponentScanner;
import com.ldtteam.structurize.util.constants.GeneralConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

public class ComponentRegistries
{
    private static final ResourceLocation DEFAULT_KEY = new ResourceLocation(GeneralConstants.MOD_ID, "default");
    private static final String DEFAULT_KEY_STRING = DEFAULT_KEY.toString();

    private final IForgeRegistry<BlockStateComponentScanner> blockStateScannerRegistry;
    private final IForgeRegistry<TileEntityComponentScanner> tileEntityScannerRegistry;
    private final IForgeRegistry<EntityComponentScanner> entityScannerRegistry;
    private final IForgeRegistry<BlockStateComponentPlacer> blockStatePlacerRegistry;
    private final IForgeRegistry<FluidStateComponentPlacer> fluidStatePlacerRegistry;
    private final IForgeRegistry<TileEntityComponentPlacer> tileEntityPlacerRegistry;
    private final IForgeRegistry<EntityComponentPlacer> entityPlacerRegistry;
    private final IForgeRegistry<BuildProvider> buildProviderRegistry;

    public ComponentRegistries()
    {
        blockStateScannerRegistry = buildRegistry(BlockStateComponentScanner.class, "blockstate_scanners").create();
        tileEntityScannerRegistry = buildRegistry(TileEntityComponentScanner.class, "tileentity_scanners").create();
        entityScannerRegistry = buildRegistry(EntityComponentScanner.class, "entity_scanners").create();
        blockStatePlacerRegistry = buildRegistry(BlockStateComponentPlacer.class, "blockstate_placers").create();
        fluidStatePlacerRegistry = buildRegistry(FluidStateComponentPlacer.class, "fluidstate_placers").create();
        tileEntityPlacerRegistry = buildRegistry(TileEntityComponentPlacer.class, "tileentity_placers").create();
        entityPlacerRegistry = buildRegistry(EntityComponentPlacer.class, "entity_placers").create();
        buildProviderRegistry = buildRegistry(BuildProvider.class, "build_providers").create();
    }

    protected static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> buildRegistry(final Class<T> clazz, final String name)
    {
        return new RegistryBuilder<T>().setName(new ResourceLocation(GeneralConstants.MOD_ID, name))
            .setDefaultKey(DEFAULT_KEY)
            .disableSaving()
            .setType(clazz)
            .setIDRange(0, Integer.MAX_VALUE - 1);
    }

    public void registerDefaultBSS(final IForgeRegistry<BlockStateComponentScanner> registry)
    {
        DefaultScanners.getDefaultBlockStateScanner().setRegistryName(DEFAULT_KEY_STRING).buildAndRegister(registry);
    }

    public void registerDefaultTES(final IForgeRegistry<TileEntityComponentScanner> registry)
    {
        DefaultScanners.getDefaultTileEntityScanner().setRegistryName(DEFAULT_KEY_STRING).buildAndRegister(registry);
    }

    public void registerDefaultES(final IForgeRegistry<EntityComponentScanner> registry)
    {
        DefaultScanners.getDefaultEntityScanner().setRegistryName(DEFAULT_KEY_STRING).buildAndRegister(registry);
    }

    public void registerDefaultBSP(final IForgeRegistry<BlockStateComponentPlacer> registry)
    {
        DefaultPlacers.getDefaultBlockStatePlacer().setRegistryName(DEFAULT_KEY_STRING).buildAndRegister(registry);
    }

    public void registerDefaultFSP(final IForgeRegistry<FluidStateComponentPlacer> registry)
    {
        DefaultPlacers.getDefaultFluidStatePlacer().setRegistryName(DEFAULT_KEY_STRING).buildAndRegister(registry);
    }

    public void registerDefaultTEP(final IForgeRegistry<TileEntityComponentPlacer> registry)
    {
        DefaultPlacers.getDefaultTileEntityPlacer().setRegistryName(DEFAULT_KEY_STRING).buildAndRegister(registry);
    }

    public void registerDefaultEP(final IForgeRegistry<EntityComponentPlacer> registry)
    {
        DefaultPlacers.getDefaultEntityPlacer().setRegistryName(DEFAULT_KEY_STRING).buildAndRegister(registry);
    }

    public void registerDefaultBP(final IForgeRegistry<BuildProvider> registry)
    {
        registry.registerAll(new InstantBuildProvider().setRegistryName(DEFAULT_KEY_STRING));
    }

    public void unfreeze()
    {
        ((ForgeRegistry<BlockStateComponentScanner>) blockStateScannerRegistry).unfreeze();
        ((ForgeRegistry<TileEntityComponentScanner>) tileEntityScannerRegistry).unfreeze();
        ((ForgeRegistry<EntityComponentScanner>) entityScannerRegistry).unfreeze();
        ((ForgeRegistry<BlockStateComponentPlacer>) blockStatePlacerRegistry).unfreeze();
        ((ForgeRegistry<FluidStateComponentPlacer>) fluidStatePlacerRegistry).unfreeze();
        ((ForgeRegistry<TileEntityComponentPlacer>) tileEntityPlacerRegistry).unfreeze();
        ((ForgeRegistry<EntityComponentPlacer>) entityPlacerRegistry).unfreeze();
        ((ForgeRegistry<BuildProvider>) buildProviderRegistry).unfreeze();
    }

    public void freeze()
    {
        ((ForgeRegistry<BlockStateComponentScanner>) blockStateScannerRegistry).freeze();
        ((ForgeRegistry<TileEntityComponentScanner>) tileEntityScannerRegistry).freeze();
        ((ForgeRegistry<EntityComponentScanner>) entityScannerRegistry).freeze();
        ((ForgeRegistry<BlockStateComponentPlacer>) blockStatePlacerRegistry).freeze();
        ((ForgeRegistry<FluidStateComponentPlacer>) fluidStatePlacerRegistry).freeze();
        ((ForgeRegistry<TileEntityComponentPlacer>) tileEntityPlacerRegistry).freeze();
        ((ForgeRegistry<EntityComponentPlacer>) entityPlacerRegistry).freeze();
        ((ForgeRegistry<BuildProvider>) buildProviderRegistry).freeze();
    }

    public IForgeRegistry<BlockStateComponentScanner> getBlockStateScannerRegistry()
    {
        return blockStateScannerRegistry;
    }

    public IForgeRegistry<TileEntityComponentScanner> getTileEntityScannerRegistry()
    {
        return tileEntityScannerRegistry;
    }

    public IForgeRegistry<EntityComponentScanner> getEntityScannerRegistry()
    {
        return entityScannerRegistry;
    }

    public IForgeRegistry<BlockStateComponentPlacer> getBlockStatePlacerRegistry()
    {
        return blockStatePlacerRegistry;
    }

    public IForgeRegistry<FluidStateComponentPlacer> getFluidStatePlacerRegistry()
    {
        return fluidStatePlacerRegistry;
    }

    public IForgeRegistry<TileEntityComponentPlacer> getTileEntityPlacerRegistry()
    {
        return tileEntityPlacerRegistry;
    }

    public IForgeRegistry<EntityComponentPlacer> getEntityPlacerRegistry()
    {
        return entityPlacerRegistry;
    }

    public IForgeRegistry<BuildProvider> getBuildProviderRegistry()
    {
        return buildProviderRegistry;
    }
}

package com.ldtteam.structurize.pipeline;

import com.ldtteam.structurize.pipeline.build.ComponentPlacer.BlockStateComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.EntityComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.FluidStateComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.TileEntityComponentPlacer;
import com.ldtteam.structurize.pipeline.defaults.build.DefaultPlacers;
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
    private final IForgeRegistry<BlockStateComponentScanner> blockStateScannerRegistry;
    private final IForgeRegistry<TileEntityComponentScanner> tileEntityScannerRegistry;
    private final IForgeRegistry<EntityComponentScanner> entityScannerRegistry;
    private final IForgeRegistry<BlockStateComponentPlacer> blockStatePlacerRegistry;
    private final IForgeRegistry<FluidStateComponentPlacer> fluidStatePlacerRegistry;
    private final IForgeRegistry<TileEntityComponentPlacer> tileEntityPlacerRegistry;
    private final IForgeRegistry<EntityComponentPlacer> entityPlacerRegistry;

    public ComponentRegistries()
    {
        blockStateScannerRegistry = buildRegistry(BlockStateComponentScanner.class, "blockstate_scanners").create();
        tileEntityScannerRegistry = buildRegistry(TileEntityComponentScanner.class, "tileentity_scanners").create();
        entityScannerRegistry = buildRegistry(EntityComponentScanner.class, "entity_scanners").create();
        blockStatePlacerRegistry = buildRegistry(BlockStateComponentPlacer.class, "blockstate_placers").create();
        fluidStatePlacerRegistry = buildRegistry(FluidStateComponentPlacer.class, "fluidstate_placers").create();
        tileEntityPlacerRegistry = buildRegistry(TileEntityComponentPlacer.class, "tileentity_placers").create();
        entityPlacerRegistry = buildRegistry(EntityComponentPlacer.class, "entity_placers").create();
        freeze();
    }

    protected static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> buildRegistry(final Class<T> clazz, final String name)
    {
        return new RegistryBuilder<T>().setName(new ResourceLocation(GeneralConstants.MOD_ID, name))
            .setDefaultKey(new ResourceLocation(GeneralConstants.MOD_ID, "default"))
            .disableSaving()
            .allowModification()
            .setType(clazz)
            .setIDRange(0, Integer.MAX_VALUE - 1);
    }

    public void registerDefaults()
    {
        // TODO: switch to proper register events
        final String defaultName = new ResourceLocation(GeneralConstants.MOD_ID, "default").toString();
        DefaultScanners.getDefaultBlockStateScanner().setRegistryName(defaultName).buildAndRegister();
        DefaultScanners.getDefaultTileEntityScanner().setRegistryName(defaultName).buildAndRegister();
        DefaultScanners.getDefaultEntityScanner().setRegistryName(defaultName).buildAndRegister();
        DefaultPlacers.getDefaultBlockStatePlacer().setRegistryName(defaultName).buildAndRegister();
        DefaultPlacers.getDefaultTileEntityPlacer().setRegistryName(defaultName).buildAndRegister();
        DefaultPlacers.getDefaultEntityPlacer().setRegistryName(defaultName).buildAndRegister();
    }

    public void unfreeze()
    {
        ((ForgeRegistry<BlockStateComponentScanner>) blockStateScannerRegistry).unfreeze();
        ((ForgeRegistry<TileEntityComponentScanner>) tileEntityScannerRegistry).unfreeze();
        ((ForgeRegistry<EntityComponentScanner>) entityScannerRegistry).unfreeze();
        ((ForgeRegistry<BlockStateComponentPlacer>) blockStatePlacerRegistry).unfreeze();
        ((ForgeRegistry<TileEntityComponentPlacer>) tileEntityPlacerRegistry).unfreeze();
        ((ForgeRegistry<EntityComponentPlacer>) entityPlacerRegistry).unfreeze();
    }

    public void freeze()
    {
        ((ForgeRegistry<BlockStateComponentScanner>) blockStateScannerRegistry).freeze();
        ((ForgeRegistry<TileEntityComponentScanner>) tileEntityScannerRegistry).freeze();
        ((ForgeRegistry<EntityComponentScanner>) entityScannerRegistry).freeze();
        ((ForgeRegistry<BlockStateComponentPlacer>) blockStatePlacerRegistry).freeze();
        ((ForgeRegistry<TileEntityComponentPlacer>) tileEntityPlacerRegistry).freeze();
        ((ForgeRegistry<EntityComponentPlacer>) entityPlacerRegistry).freeze();
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
}

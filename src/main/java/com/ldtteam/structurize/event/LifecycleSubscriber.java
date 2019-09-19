package com.ldtteam.structurize.event;

import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.block.ModBlocks;
import com.ldtteam.structurize.item.ModItems;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.BlockStateComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.EntityComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.FluidStateComponentPlacer;
import com.ldtteam.structurize.pipeline.build.ComponentPlacer.TileEntityComponentPlacer;
import com.ldtteam.structurize.pipeline.scan.ComponentScanner.BlockStateComponentScanner;
import com.ldtteam.structurize.pipeline.scan.ComponentScanner.EntityComponentScanner;
import com.ldtteam.structurize.pipeline.scan.ComponentScanner.TileEntityComponentScanner;
import com.ldtteam.structurize.util.LanguageHandler;
import com.ldtteam.structurize.world.ModDimensions;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

/**
 * Class with methods for receiving various forge events.
 * Methods are sorted according to time of execution.
 */
public class LifecycleSubscriber
{
    /**
     * Private constructor to hide implicit public one.
     */
    private LifecycleSubscriber()
    {
        /**
         * Intentionally left empty
         */
    }

    /**
     * Called when blocks are supposed to be registered.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onBlockRegistry(final RegistryEvent.Register<Block> event)
    {
        Instances.getLogger().warn("RegistryEvent.Register<Block>");
        ModBlocks.registerBlocks(event.getRegistry());
    }

    /**
     * Called when items are supposed to be registered.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onItemRegistry(final RegistryEvent.Register<Item> event)
    {
        Instances.getLogger().warn("RegistryEvent.Register<Item>");
        ModItems.registerItems(event.getRegistry());
    }

    /**
     * Called when dimensions are supposed to be registered.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onModDimensionRegistry(final RegistryEvent.Register<ModDimension> event)
    {
        Instances.getLogger().warn("RegistryEvent.Register<ModDimension>");
        ModDimensions.registerModDimensions(event.getRegistry());
    }

    /**
     * Called when BSS are supposed to be registered.
     *
     * @param event event
     */

    @SubscribeEvent
    public static void onBSSRegistry(final RegistryEvent.Register<BlockStateComponentScanner> event)
    {
        Instances.getLogger().warn("RegistryEvent.Register<BSS>");
        Instances.getComponentRegistries().registerDefaultBSS(event.getRegistry());
    }

    /**
     * Called when TES are supposed to be registered.
     *
     * @param event event
     */

    @SubscribeEvent
    public static void onTESRegistry(final RegistryEvent.Register<TileEntityComponentScanner> event)
    {
        Instances.getLogger().warn("RegistryEvent.Register<TES>");
        Instances.getComponentRegistries().registerDefaultTES(event.getRegistry());
    }

    /**
     * Called when ES are supposed to be registered.
     *
     * @param event event
     */

    @SubscribeEvent
    public static void onESRegistry(final RegistryEvent.Register<EntityComponentScanner> event)
    {
        Instances.getLogger().warn("RegistryEvent.Register<ES>");
        Instances.getComponentRegistries().registerDefaultES(event.getRegistry());
    }

    /**
     * Called when BSP are supposed to be registered.
     *
     * @param event event
     */

    @SubscribeEvent
    public static void onBSPRegistry(final RegistryEvent.Register<BlockStateComponentPlacer> event)
    {
        Instances.getLogger().warn("RegistryEvent.Register<BSP>");
        Instances.getComponentRegistries().registerDefaultBSP(event.getRegistry());
    }

    /**
     * Called when FSP are supposed to be registered.
     *
     * @param event event
     */

    @SubscribeEvent
    public static void onFSPRegistry(final RegistryEvent.Register<FluidStateComponentPlacer> event)
    {
        Instances.getLogger().warn("RegistryEvent.Register<FSP>");
        Instances.getComponentRegistries().registerDefaultFSP(event.getRegistry());
    }

    /**
     * Called when TEP are supposed to be registered.
     *
     * @param event event
     */

    @SubscribeEvent
    public static void onTEPRegistry(final RegistryEvent.Register<TileEntityComponentPlacer> event)
    {
        Instances.getLogger().warn("RegistryEvent.Register<TEP>");
        Instances.getComponentRegistries().registerDefaultTEP(event.getRegistry());
    }

    /**
     * Called when EP are supposed to be registered.
     *
     * @param event event
     */

    @SubscribeEvent
    public static void onEPRegistry(final RegistryEvent.Register<EntityComponentPlacer> event)
    {
        Instances.getLogger().warn("RegistryEvent.Register<EP>");
        Instances.getComponentRegistries().registerDefaultEP(event.getRegistry());
    }

    /**
     * Called when mod is being initialized.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onModInit(final FMLCommonSetupEvent event)
    {
        Instances.getLogger().warn("FMLCommonSetupEvent");
        Instances.getNetwork().registerCommonMessages();
    }

    /**
     * Called when client app is initialized.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onClientInit(final FMLClientSetupEvent event)
    {
        Instances.getLogger().warn("FMLClientSetupEvent");
    }

    /**
     * Called when server app is initialized.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onDediServerInit(final FMLDedicatedServerSetupEvent event)
    {
        Instances.getLogger().warn("FMLDedicatedServerSetupEvent");
    }

    /**
     * Called when mod is able to send IMCs.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void enqueueIMC(final InterModEnqueueEvent event)
    {
        Instances.getLogger().warn("InterModEnqueueEvent");
        /*
         * InterModComms.sendTo("structurize", "helloworld", () -> {
         * return "Hello world";
         * });
         */
    }

    /**
     * Called when mod is able to receive IMCs.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void processIMC(final InterModProcessEvent event)
    {
        Instances.getLogger().warn("InterModProcessEvent");
        /*
         * LOGGER.info("Got IMC {}", event.getIMCStream().map(m -> m.getMessageSupplier().get()).collect(Collectors.toList()));
         */
    }

    /**
     * Called when MC loading is about to finish.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onLoadComplete(final FMLLoadCompleteEvent event)
    {
        Instances.getLogger().warn("FMLLoadCompleteEvent");
        LanguageHandler.setMClanguageLoaded();
    }
}

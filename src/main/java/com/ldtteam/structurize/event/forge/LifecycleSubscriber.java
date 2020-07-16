package com.ldtteam.structurize.event.forge;

import com.ldtteam.structurize.Structurize;
import com.ldtteam.structurize.block.ModBlocks;
import com.ldtteam.structurize.client.gui.OverlayGuiManager;
import com.ldtteam.structurize.item.ModItems;
import com.ldtteam.structurize.pipeline.build.BlockInfoPlacer;
import com.ldtteam.structurize.pipeline.build.BuildProvider;
import com.ldtteam.structurize.pipeline.build.EntityPlacer;
import com.ldtteam.structurize.pipeline.scan.BlockInfoScanner;
import com.ldtteam.structurize.pipeline.scan.EntityScanner;
import com.ldtteam.structurize.util.LanguageHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
        Structurize.getLogger().warn("RegistryEvent.Register<Block>");
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
        Structurize.getLogger().warn("RegistryEvent.Register<Item>");
        ModItems.registerItems(event.getRegistry());
    }

    /**
     * Called when dimensions are supposed to be registered.
     *
     * @param event event
     */
    /*
     * @SubscribeEvent
     * public static void onModDimensionRegistry(final RegistryEvent.Register<ModDimension> event)
     * {
     * Structurize.getLogger().warn("RegistryEvent.Register<ModDimension>");
     * ModDimensions.registerModDimensions(event.getRegistry());
     * }
     */

    /**
     * Called when BP are supposed to be registered.
     *
     * @param event event
     */

    @SubscribeEvent
    public static void onBPRegistry(final RegistryEvent.Register<BuildProvider> event)
    {
        Structurize.getLogger().warn("RegistryEvent.Register<BP>");
        Structurize.getComponentRegistries().registerDefaultBP(event.getRegistry());
    }

    /**
     * Called when BIS are supposed to be registered.
     *
     * @param event event
     */

    @SubscribeEvent
    public static void onBISRegistry(final RegistryEvent.Register<BlockInfoScanner> event)
    {
        Structurize.getLogger().warn("RegistryEvent.Register<BSS>");
        Structurize.getComponentRegistries().registerDefaultBIS(event.getRegistry());
    }

    /**
     * Called when ES are supposed to be registered.
     *
     * @param event event
     */

    @SubscribeEvent
    public static void onESRegistry(final RegistryEvent.Register<EntityScanner<Entity>> event)
    {
        Structurize.getLogger().warn("RegistryEvent.Register<ES>");
        Structurize.getComponentRegistries().registerDefaultES(event.getRegistry());
    }

    /**
     * Called when BIP are supposed to be registered.
     *
     * @param event event
     */

    @SubscribeEvent
    public static void onBIPRegistry(final RegistryEvent.Register<BlockInfoPlacer> event)
    {
        Structurize.getLogger().warn("RegistryEvent.Register<BSP>");
        Structurize.getComponentRegistries().registerDefaultBIP(event.getRegistry());
    }

    /**
     * Called when EP are supposed to be registered.
     *
     * @param event event
     */

    @SubscribeEvent
    public static void onEPRegistry(final RegistryEvent.Register<EntityPlacer<Entity>> event)
    {
        Structurize.getLogger().warn("RegistryEvent.Register<EP>");
        Structurize.getComponentRegistries().registerDefaultEP(event.getRegistry());
    }

    /**
     * Called when mod is being initialized.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onModInit(final FMLCommonSetupEvent event)
    {
        Structurize.getLogger().warn("FMLCommonSetupEvent");
        Structurize.getNetwork().registerCommonMessages();
    }

    /**
     * Called when client app is initialized.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onClientInit(final FMLClientSetupEvent event)
    {
        Structurize.getLogger().warn("FMLClientSetupEvent");
        ClientRegistry.registerKeyBinding(OverlayGuiManager.KEY_BINDING);
        OverlayGuiManager.setup();
        // Structurize.getOptifineCompat().intialize();
    }

    /**
     * Called when server app is initialized.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onDediServerInit(final FMLDedicatedServerSetupEvent event)
    {
        Structurize.getLogger().warn("FMLDedicatedServerSetupEvent");
    }

    /**
     * Called when mod is able to send IMCs.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void enqueueIMC(final InterModEnqueueEvent event)
    {
        Structurize.getLogger().warn("InterModEnqueueEvent");
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
        Structurize.getLogger().warn("InterModProcessEvent");
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
        Structurize.getLogger().warn("FMLLoadCompleteEvent");
        LanguageHandler.setMClanguageLoaded();
    }
}

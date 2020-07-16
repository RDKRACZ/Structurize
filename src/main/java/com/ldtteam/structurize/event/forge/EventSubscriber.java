package com.ldtteam.structurize.event.forge;

import com.ldtteam.structurize.Structurize;
import com.ldtteam.structurize.command.EntryPoint;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

/**
 * Class with methods for receiving various forge events
 */
public class EventSubscriber
{
    /**
     * Private constructor to hide implicit public one.
     */
    private EventSubscriber()
    {
        /**
         * Intentionally left empty
         */
    }

    /**
     * Called when server will load a world (client: play button, dedi server: after {@link LifecycleSubscriber#processIMC()}).
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onServerAboutToStart(final FMLServerAboutToStartEvent event)
    {
        Structurize.getLogger().warn("FMLServerAboutToStartEvent");
        // ModDimensions.registerDimensionTypes();
    }

    /**
     * Called when world is about to load.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onServerStarting(final FMLServerStartingEvent event)
    {
        Structurize.getLogger().warn("FMLServerStartingEvent");
        EntryPoint.register(event.getCommandDispatcher());
    }

    /**
     * Called when world is loaded.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onServerStarted(final FMLServerStartedEvent event)
    {
        Structurize.getLogger().warn("FMLServerStartedEvent");
    }

    /**
     * Called when world is about to stop.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onServerStopping(final FMLServerStoppingEvent event)
    {
        Structurize.getLogger().warn("FMLServerStoppingEvent");
    }

    /**
     * Called when world is stopped.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onServerStopped(final FMLServerStoppedEvent event)
    {
        Structurize.getLogger().warn("FMLServerStoppedEvent");
    }

    /**
     * Called when player joins world.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerLoggedInEvent event)
    {
        // PlayerEvents.onPlayerJoinedSchematicWorldType(event.getPlayer());
    }

    /**
     * Called when any entity is trying to change dimension.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onEntityTravelToDimension(final EntityTravelToDimensionEvent event)
    {
        if (false)// !PlayerEvents.canEntityTravelToDimension(event.getEntity(), event.getDimension()))
        {
            event.setCanceled(true);
        }
    }
}

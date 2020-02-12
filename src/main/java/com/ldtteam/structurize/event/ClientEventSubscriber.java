package com.ldtteam.structurize.event;

import com.ldtteam.structurize.Structurize;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Class with methods for receiving various client-side only forge events
 */
public class ClientEventSubscriber
{
    /**
     * Private constructor to hide implicit public one.
     */
    private ClientEventSubscriber()
    {
        /**
         * Intentionally left empty
         */
    }

    /**
     * Called after world and before player's hand a gui overlays rendering.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onRenderWorldLast(final RenderWorldLastEvent event)
    {
        Structurize.getEventRenderer().renderActiveEvents(event.getContext(), event.getPartialTicks());
    }
}

package com.ldtteam.structurize.event.forge;

import com.ldtteam.structurize.Structurize;
import com.ldtteam.structurize.client.gui.GuiKeybindManager;
import com.ldtteam.structurize.event.ClientEventManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
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
     * Called after world and before player's hand gui rendering.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onRenderWorldLast(final RenderWorldLastEvent event)
    {
        ((ClientEventManager) Structurize.getApi().getEventManager())
            .render(event.getContext(), event.getMatrixStack(), event.getPartialTicks());
    }

    /**
     * Called before client tick starts.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onClientTickStart(final ClientTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            GuiKeybindManager.tick();
        }
    }
}

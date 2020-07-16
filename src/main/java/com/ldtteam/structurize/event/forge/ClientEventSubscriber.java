package com.ldtteam.structurize.event.forge;

import com.ldtteam.structurize.Structurize;
import com.ldtteam.structurize.client.gui.OverlayGuiManager;
import com.ldtteam.structurize.event.ClientEventManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
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
        ((ClientEventManager) Structurize.getApi().getEventManager()).render(event);
    }

    /**
     * Called on any keyboard input.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onKeyInputEvent(final KeyInputEvent event)
    {
        OverlayGuiManager.tickKeyBinding();
    }

    /**
     * Called after in-game overlay rendering.
     *
     * @param event event
     */
    @SubscribeEvent
    public static void onPostOverlayRender(final Post event)
    {
        if (event.getType() == ElementType.ALL)
        {
            OverlayGuiManager.tickGui(event.getWindow());
        }
    }
}

package com.ldtteam.structurize.client.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.structure.PlaceEventInfoHolder;
import net.minecraft.client.renderer.WorldRenderer;

/**
 * Static class for rendering active events.
 */
public class StructureRenderer
{
    private final static List<PlaceEventInfoHolder<?>> activeEvents = new ArrayList<>();

    /**
     * Private constructor to hide implicit public one.
     */
    private StructureRenderer()
    {
        /**
         * Intentionally left empty
         */
    }

    /**
     * Adds active event for rendering. Use
     *
     * @param event new event
     * @return whether addition succeeded or not
     */
    public static boolean addActiveEvent(final PlaceEventInfoHolder<?> event)
    {
        if (!event.isCanceled())
        {
            if (activeEvents.size() < Instances.getConfig().getClient().maxAmountOfRenderedEvents.get())
            {
                activeEvents.add(event);
                return true;
            }
        }
        return false;
    }

    public static void renderActiveEvents(final WorldRenderer worldRenderer, final float partialTicks)
    {
        // TODO: should we not render remaining events if we cause tick lag?
        final Iterator<PlaceEventInfoHolder<?>> iterator = activeEvents.iterator();
        while (iterator.hasNext())
        {
            final PlaceEventInfoHolder<?> event = iterator.next();
            if (event.isCanceled())
            {
                iterator.remove();
                continue;
            }

            renderEvent(event, worldRenderer, partialTicks);
        }
    }

    private static void renderEvent(final PlaceEventInfoHolder<?> event, final WorldRenderer worldRenderer, final float partialTicks)
    {

    }
}

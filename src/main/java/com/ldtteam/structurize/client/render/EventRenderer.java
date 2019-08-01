package com.ldtteam.structurize.client.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.ldtteam.structurize.Instances;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.Vec3d;

/**
 * Static class for rendering active events.
 */
public class EventRenderer
{
    private static final List<RenderEventWrapper<?, ?>> activeEvents = new ArrayList<>();
    private static final Cache<RenderEventWrapper<?, ?>, StructureRenderer> eventCache = CacheBuilder.newBuilder()
        .maximumSize(Instances.getConfig().getClient().prerendedStructureCacheSize.get())
        .removalListener(
            (RemovalListener<RenderEventWrapper<?, ?>, StructureRenderer>) notification -> notification.getValue()
                .getTessellator()
                .getBuffer()
                .deleteGlBuffers())
        .build();
    private static long highestDiffSingleEvent = 0;
    private static long highestDiffAllEvents = 0;
    private static boolean recompileTesselators = false;

    /**
     * Private constructor to hide implicit public one.
     */
    private EventRenderer()
    {
        /**
         * Intentionally left empty
         */
    }

    public static void cancelAllActiveEvents()
    {
        for (final RenderEventWrapper<?, ?> e : activeEvents)
        {
            e.getEvent().cancel();
        }
    }

    public static void resetLagStats()
    {
        highestDiffAllEvents = 0;
        highestDiffSingleEvent = 0;
    }

    public static void recompileTesselators()
    {
        recompileTesselators = true;
    }

    /**
     * Adds active event for rendering. Use
     *
     * @param event new event
     * @return whether addition succeeded or not
     */
    public static boolean addActiveEvent(final RenderEventWrapper<?, ?> event)
    {
        if (!event.getEvent().isCanceled())
        {
            if (activeEvents.size() < Instances.getConfig().getClient().maxAmountOfRenderedEvents.get())
            {
                activeEvents.add(event);
                return true;
            }
        }
        return false;
    }

    /**
     * Renders all active events onto player's screen.
     *
     * @param worldRenderer event data
     * @param partialTicks  event data
     */
    public static void renderActiveEvents(final WorldRenderer worldRenderer, final float partialTicks)
    {
        final long start = System.nanoTime();
        // should we not render remaining events if we cause tick lag?
        final Iterator<RenderEventWrapper<?, ?>> iterator = activeEvents.iterator();
        while (iterator.hasNext())
        {
            // TODO: should get proper culling
            final RenderEventWrapper<?, ?> event = iterator.next();
            if (event.getEvent().isCanceled())
            {
                iterator.remove();
                continue;
            }

            renderEvent(event);
        }
        recompileTesselators = false;
        final long diff = System.nanoTime() - start;
        if (diff > highestDiffAllEvents)
        {
            highestDiffAllEvents = diff;
            Instances.getLogger().info("New all events top {}ms", diff / 1_000_000);
        }
    }

    /**
     * Renders event onto player's screen.
     *
     * @param event event to render
     */
    private static void renderEvent(final RenderEventWrapper<?, ?> event)
    {
        final long start = System.nanoTime();
        final Vec3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        try
        {
            eventCache.get(event, () -> new StructureRenderer(event, true)).draw(projectedView, recompileTesselators || event.shouldRedraw());
        }
        catch (final ExecutionException e)
        {
            Instances.getLogger().error(e);
        }

        renderStructureBB(event, projectedView);
        final long diff = System.nanoTime() - start;
        if (diff > highestDiffSingleEvent)
        {
            highestDiffSingleEvent = diff;
            Instances.getLogger().info("New single event top {}ms", diff / 1_000_000);
        }
    }

    /**
     * Renders white structure BB.
     *
     * @param event event to render
     * @param view  screen view
     */
    private static void renderStructureBB(final RenderEventWrapper<?, ?> event, final Vec3d view)
    {
        GlStateManager.lineWidth(2.0F);
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        WorldRenderer.drawSelectionBoundingBox(event.getEvent().getPosition().toAABB().expand(1, 1, 1).offset(view.scale(-1)), 1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
    }
}

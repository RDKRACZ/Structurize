package com.ldtteam.structurize.client.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.pipeline.build.EventInfoHolder;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.Vec3d;

/**
 * Static class for rendering active events.
 */
public class EventRenderer
{
    private static final float FALLBACK_PARTIAL_TICKS = 0.5f;

    private final List<EventInfoHolder<?>> activeEvents = new ArrayList<>();
    private boolean recompileTesselators = false;

    /**
     * Creates new instance.
     */
    public EventRenderer()
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
    public boolean addActiveEvent(final EventInfoHolder<?> event)
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

    /**
     * Marks all active events as canceled.
     */
    public void cancelAllActiveEvents()
    {
        for (final EventInfoHolder<?> e : activeEvents)
        {
            e.cancel();
        }
    }

    /**
     * Forces every renderer tessellator to recompile.
     */
    public void recompileTessellators()
    {
        recompileTesselators = true;
    }

    /**
     * Renders all active events onto player's screen.
     *
     * @param worldRenderer event data
     * @param partialTicks  event data
     */
    public void renderActiveEvents(final WorldRenderer worldRenderer, final float partialTicks)
    {
        // TODO: should we not render remaining events if we cause tick lag?
        final float modifiedPartialTicks = Instances.getConfig().getClient().structurePartialTicks.get() ? partialTicks : FALLBACK_PARTIAL_TICKS;
        final Iterator<EventInfoHolder<?>> iterator = activeEvents.iterator();
        while (iterator.hasNext())
        {
            // TODO: proper rendering order would be great, need to determine fronts and backs
            final EventInfoHolder<?> event = iterator.next();
            if (event.isCanceled())
            {
                iterator.remove();
                continue;
            }

            renderEvent(event, modifiedPartialTicks);
        }
        recompileTesselators = false;
    }

    /**
     * Renders event onto player's screen.
     *
     * @param event event to render
     */
    private void renderEvent(final EventInfoHolder<?> event, final float partialTicks)
    {
        final Vec3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();

        event.getRenderer().draw(projectedView, recompileTesselators, partialTicks);

        renderStructureBB(event, projectedView);
    }

    /**
     * Renders white structure BB.
     *
     * @param event event to render
     * @param view  screen view
     */
    private void renderStructureBB(final EventInfoHolder<?> event, final Vec3d view)
    {
        GlStateManager.lineWidth(2.0F);
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        WorldRenderer.drawSelectionBoundingBox(event.getPosition().toAABB().expand(1, 1, 1).offset(view.scale(-1)), 1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
    }
}

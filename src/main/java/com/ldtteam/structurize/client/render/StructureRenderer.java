package com.ldtteam.structurize.client.render;

import com.ldtteam.structurize.structure.PlaceEventInfoHolder;
import com.ldtteam.structurize.structure.providers.IStructureDataProvider;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.WorldRenderer;

public class StructureRenderer
{
    private static PlaceEventInfoHolder<?> activeBuildToolEvent;
    private static PlaceEventInfoHolder<?> activeShapeToolEvent;

    /**
     * Sets active buildtool event for rendering.
     *
     * @param event new event, null for not rendering
     * @return previous event, might be null
     */
    public static PlaceEventInfoHolder<?> setActiveBuildToolEvent(final PlaceEventInfoHolder<?> event)
    {
        final PlaceEventInfoHolder<?> previous = activeBuildToolEvent;
        activeBuildToolEvent = event;
        return previous;
    }

    /**
     * Sets active buildtool event for rendering.
     *
     * @param event new event, null for not rendering
     * @return previous event, might be null
     */
    public static PlaceEventInfoHolder<?> setActiveShapeToolEvent(final PlaceEventInfoHolder<?> event)
    {
        final PlaceEventInfoHolder<?> previous = activeShapeToolEvent;
        activeShapeToolEvent = event;
        return previous;
    }

    public static void renderActiveEvents(final WorldRenderer worldRenderer, final float partialTicks)
    {
        if (activeBuildToolEvent != null)
        {
            renderStructureDataProvider(activeBuildToolEvent.getStructure(), worldRenderer, partialTicks);
        }
        if (activeShapeToolEvent != null)
        {
            renderStructureDataProvider(activeShapeToolEvent.getStructure(), worldRenderer, partialTicks);
        }
    }

    private static void renderStructureDataProvider(final IStructureDataProvider dataProvider, final WorldRenderer worldRenderer, final float partialTicks)
    {
        GlStateManager.enableDepthTest();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);
        GlStateManager.enableCull();
    }
}

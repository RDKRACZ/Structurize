package com.ldtteam.structurize.client.gui;

import com.ldtteam.structurize.client.render.util.RenderUtils;
import com.ldtteam.structurize.client.render.util.RenderUtils.BuiltBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.BufferBuilder;

public class PlacementPositionerRenderer
{
    private BuiltBuffer builtBuffer;

    public void render(final MainWindow renderInWindow)
    {
        rebuild();
        RenderSystem.pushMatrix();
        RenderSystem.translatef(renderInWindow.getScaledWidth() / 2.0f, renderInWindow.getScaledHeight() / 2.0f, 0);
        RenderSystem.rotatef(RenderUtils.getVanillaRenderInfo().getPitch(), -1.0F, 0.0F, 0.0F);
        RenderSystem.rotatef(RenderUtils.getVanillaRenderInfo().getYaw(), 0.0F, 1.0F, 0.0F);
        RenderSystem.scalef(-1.0F, -1.0F, -1.0F);
        RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        RenderUtils.drawBuiltBuffer(builtBuffer);
        RenderSystem.popMatrix();
    }

    public void rebuild()
    {
        final BufferBuilder mainArrow = RenderUtils.createAndBeginBuffer(RenderUtils.COLORED_SHAPE);
        mainArrow.pos(-100, -100, 0).color(255, 255, 255, 255).endVertex();
        mainArrow.pos(100, -100, 0).color(255, 255, 255, 255).endVertex();
        mainArrow.pos(0, 100, 0).color(255, 255, 255, 255).endVertex();
        builtBuffer = RenderUtils.finishBuffer(mainArrow, RenderUtils.COLORED_SHAPE);
    }
}

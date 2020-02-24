package com.ldtteam.structurize.client.render;

import com.ldtteam.structurize.client.render.RenderUtils.BuiltBuffer;
import com.ldtteam.structurize.structure.StructureBB;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;

public class BoxRenderer implements IRenderer
{
    private final StructureBB structureBB;
    private BuiltBuffer builtBuffer;

    public BoxRenderer(final StructureBB structureBBin)
    {
        structureBB = structureBBin;
    }

    @Override
    public void render(final WorldRenderer context, final MatrixStack matrixStack, final float partialTicks)
    {
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(matrixStack.getLast().getMatrix());
        RenderUtils.drawBuiltBuffer(builtBuffer);
        RenderSystem.popMatrix();
    }

    @Override
    public void rebuild()
    {
        final BufferBuilder bufferBuilder = RenderUtils.createAndBeginBuffer(RenderType.getLines());
        WorldRenderer.drawBoundingBox(new MatrixStack(), bufferBuilder, structureBB.toZeroBasedAABB(), 1.0f, 1.0f, 1.0f, 1.0f);
        builtBuffer = RenderUtils.finishBuffer(bufferBuilder, RenderType.getLines());
    }
}

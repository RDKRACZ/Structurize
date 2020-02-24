package com.ldtteam.structurize.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.WorldRenderer;

public interface IRenderableEvent
{
    void render(WorldRenderer context, MatrixStack matrixStack, float partialTicks);
}

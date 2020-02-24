package com.ldtteam.structurize.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.WorldRenderer;

public interface IRenderStateModifier
{
    void run(WorldRenderer context, MatrixStack matrixStack, float partialTicks);
}

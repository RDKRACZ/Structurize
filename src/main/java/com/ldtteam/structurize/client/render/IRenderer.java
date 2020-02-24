package com.ldtteam.structurize.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.WorldRenderer;

public interface IRenderer
{
    void render(WorldRenderer context, MatrixStack matrixStack, float partialTicks);

    void rebuild();
}

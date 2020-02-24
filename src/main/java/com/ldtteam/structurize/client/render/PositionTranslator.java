package com.ldtteam.structurize.client.render;

import java.util.function.Supplier;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class PositionTranslator implements IRenderStateModifier
{
    public static class Apply extends PositionTranslator
    {
        private final Supplier<BlockPos> absolutePosSupplier;

        public Apply(final Supplier<BlockPos> absolutePosSupplierIn)
        {
            absolutePosSupplier = absolutePosSupplierIn;
        }

        @Override
        public void run(final WorldRenderer context, final MatrixStack matrixStack, final float partialTicks)
        {
            final Vec3d camera = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
            final BlockPos anchor = absolutePosSupplier.get();

            matrixStack.push();
            matrixStack.translate(anchor.getX() - camera.getX(), anchor.getY() - camera.getY(), anchor.getZ() - camera.getZ());
        }
    }

    public static class Reset extends PositionTranslator
    {
        @Override
        public void run(final WorldRenderer context, final MatrixStack matrixStack, final float partialTicks)
        {
            matrixStack.pop();
        }
    }
}

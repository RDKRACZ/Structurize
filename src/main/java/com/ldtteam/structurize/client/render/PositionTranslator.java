package com.ldtteam.structurize.client.render;

import java.util.function.Supplier;
import com.ldtteam.structurize.client.render.util.RenderUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class PositionTranslator extends EventRenderer
{
    private final Supplier<BlockPos> absolutePosSupplier;

    public PositionTranslator(final Supplier<BlockPos> absolutePosSupplier, final Builder builder)
    {
        super(builder);
        this.absolutePosSupplier = absolutePosSupplier;
    }

    @Override
    public void render(final RenderWorldLastEvent context)
    {
        final Vector3d camera = RenderUtils.getVanillaRenderInfo().getProjectedView();
        final BlockPos anchor = absolutePosSupplier.get();

        context.getMatrixStack().push();
        context.getMatrixStack().translate(anchor.getX() - camera.getX(), anchor.getY() - camera.getY(), anchor.getZ() - camera.getZ());
        super.render(context);
        context.getMatrixStack().pop();
    }
}

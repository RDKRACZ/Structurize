package com.ldtteam.structurize.client.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import com.ldtteam.structurize.structure.Structure;
import com.ldtteam.structurize.structure.StructureBB;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;

/**
 * Renderer holder for attaching few renderers into one.
 */
public class EventRenderer implements IRenderer
{
    private final List<Object> renderPipeline;

    private EventRenderer(final Builder builder)
    {
        renderPipeline = builder.renderPipeline;
    }

    @Override
    public void rebuild()
    {
        for (final Object action : renderPipeline)
        {
            if (action instanceof IRenderer)
            {
                ((IRenderer) action).rebuild();
            }
        }
    }

    @Override
    public void render(final WorldRenderer context, final MatrixStack matrixStack, final float partialTicks)
    {
        for (final Object action : renderPipeline)
        {
            if (action instanceof IRenderer)
            {
                ((IRenderer) action).render(context, matrixStack, partialTicks);
            }
            else if (action instanceof IRenderStateModifier)
            {
                ((IRenderStateModifier) action).run(context, matrixStack, partialTicks);
            }
        }
    }

    /**
     * @return new EventRenderer builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<Object> renderPipeline = new ArrayList<>();

        private Builder()
        {
        }

        /**
         * Adds structure renderer.
         *
         * @param structure   rendered structure
         * @param structureBB its bounding box
         * @return updated builder instance
         */
        public Builder structure(final Structure structure, final StructureBB structureBB)
        {
            renderPipeline.add(new StructureRenderer(structure, structureBB));
            return this;
        }

        /**
         * Adds box renderer.
         *
         * @param structureBB rendered box
         * @return updated builder instance
         */
        public Builder box(final StructureBB structureBB)
        {
            renderPipeline.add(new BoxRenderer(structureBB));
            return this;
        }

        /**
         * Translate rendering from player relative to world absolute using pos from supplier.
         *
         * @param absolutePosSupplier supplier of world absolute position
         * @return updated builder instance
         */
        public Builder absolutePos(final Supplier<BlockPos> absolutePosSupplier)
        {
            renderPipeline.add(new PositionTranslator.Apply(absolutePosSupplier));
            return this;
        }

        /**
         * Restores world absolute rendering to player relative.
         *
         * @return updated builder instance
         */
        public Builder absolutePosRestore()
        {
            renderPipeline.add(new PositionTranslator.Reset());
            return this;
        }

        /**
         * Builds new EventRenderer based on this builder.
         *
         * @return new EventRenderer
         */
        public EventRenderer build()
        {
            final EventRenderer built = new EventRenderer(this);
            built.rebuild();
            return built;
        }
    }
}

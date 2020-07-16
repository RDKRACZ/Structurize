package com.ldtteam.structurize.client.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import com.ldtteam.structurize.structure.Structure;
import com.ldtteam.structurize.structure.StructureBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;

/**
 * Renderer holder for attaching few renderers into one.
 */
public class EventRenderer implements IWorldRenderer
{
    private final List<IWorldRenderer> renderPipeline;

    protected EventRenderer(final Builder builder)
    {
        renderPipeline = builder.renderPipeline;
    }

    @Override
    public void rebuild()
    {
        for (final IWorldRenderer renderer : renderPipeline)
        {
            renderer.rebuild();
        }
    }

    @Override
    public void render(final RenderWorldLastEvent context)
    {
        for (final IWorldRenderer renderer : renderPipeline)
        {
            renderer.render(context);
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
        private final List<IWorldRenderer> renderPipeline = new ArrayList<>();

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
         * @param translatedRenderer  builder of renderer which should be translated to absolutePos
         * @return updated builder instance
         */
        public Builder absolutePos(final Supplier<BlockPos> absolutePosSupplier, final Builder translatedRenderer)
        {
            renderPipeline.add(new PositionTranslator(absolutePosSupplier, translatedRenderer));
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

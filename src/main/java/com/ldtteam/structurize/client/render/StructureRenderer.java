package com.ldtteam.structurize.client.render;

import com.ldtteam.structurize.structure.Structure;
import com.ldtteam.structurize.structure.StructureBB;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.WorldRenderer;

public class StructureRenderer implements IRenderer
{
    private final Structure structure;
    private final StructureBB structureBB;

    public StructureRenderer(Structure structure, StructureBB structureBB)
    {
        this.structure = structure;
        this.structureBB = structureBB;
    }

    @Override
    public void render(final WorldRenderer context, final MatrixStack matrixStack, final float partialTicks)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void rebuild()
    {
        // TODO Auto-generated method stub
    }
}

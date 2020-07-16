package com.ldtteam.structurize.client.render.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.ldtteam.structurize.client.render.util.RenderUtils.BuiltBuffer;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;

public class RenderBuffers implements IRenderTypeBuffer
{
    private Map<RenderType, BufferBuilder> startedBuffers = new HashMap<>(16);
    private List<BuiltBuffer> finishedBuffers = new ArrayList<>(16);

    public RenderBuffers()
    {
    }

    @Override
    public IVertexBuilder getBuffer(final RenderType renderType)
    {
        if (!startedBuffers.containsKey(renderType))
        {
            startedBuffers.put(renderType, RenderUtils.createAndBeginBuffer(renderType));
        }

        return startedBuffers.get(renderType);
    }

    public void finish()
    {
        for (final Map.Entry<RenderType, BufferBuilder> entry : startedBuffers.entrySet())
        {
            finishedBuffers.add(RenderUtils.finishBuffer(entry.getValue(), entry.getKey()));
        }
        startedBuffers.clear();
    }

    public void finish(final RenderType renderType)
    {
        if (startedBuffers.containsKey(renderType))
        {
            finishedBuffers.add(RenderUtils.finishBuffer(startedBuffers.remove(renderType), renderType));
        }
    }

    public void finishForVBO(final RenderType renderType, final String name)
    {
        if (startedBuffers.containsKey(renderType))
        {
            finishedBuffers.add(RenderUtils.finishBufferForVBO(startedBuffers.remove(renderType), renderType, name));
        }
    }

    public void render()
    {
        if (finishedBuffers != null)
        {
            finishedBuffers.forEach(RenderUtils::drawBuiltBuffer);
        }
    }
}

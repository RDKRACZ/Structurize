package com.ldtteam.structurize.client.render.util;

import java.nio.ByteBuffer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import org.lwjgl.system.MemoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderTimeManager;
import net.minecraft.client.renderer.BufferBuilder.DrawState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.World;

public class RenderUtils
{
    public static final RenderType COLORED_SHAPE = RenderTypes.COLORED_SHAPE;

    public static final RenderTimeManager renderTimeManager = new RenderTimeManager(100);
    private static World tileEntityRendererDispatcherWorld;
    private static ActiveRenderInfo tileEntityRendererDispatcherActiveRenderInfo;
    private static World entityRendererManagerWorld;
    private static ActiveRenderInfo entityRendererManagerActiveRenderInfo;
    public static Matrix4f latestProjectionMatrix;

    public static void saveVanillaState()
    {
        tileEntityRendererDispatcherWorld = TileEntityRendererDispatcher.instance.world;
        tileEntityRendererDispatcherActiveRenderInfo = TileEntityRendererDispatcher.instance.renderInfo;
        entityRendererManagerWorld = Minecraft.getInstance().getRenderManager().world;
        entityRendererManagerActiveRenderInfo = Minecraft.getInstance().getRenderManager().info;
    }

    public static void loadVanillaState()
    {
        TileEntityRendererDispatcher.instance.world = tileEntityRendererDispatcherWorld;
        TileEntityRendererDispatcher.instance.renderInfo = tileEntityRendererDispatcherActiveRenderInfo;
        Minecraft.getInstance().getRenderManager().info = entityRendererManagerActiveRenderInfo;
        Minecraft.getInstance().getRenderManager().world = entityRendererManagerWorld;
    }

    public static ActiveRenderInfo getVanillaRenderInfo()
    {
        return tileEntityRendererDispatcherActiveRenderInfo;
    }

    public static BufferBuilder createAndBeginBuffer(final RenderType renderType)
    {
        final BufferBuilder bufferBuilder = new BufferBuilder(renderType.getBufferSize());
        bufferBuilder.begin(renderType.getDrawMode(), renderType.getVertexFormat());
        return bufferBuilder;
    }

    private static void checkAndFinishBuffer(final BufferBuilder bufferBuilder, final RenderType renderType)
    {
        if (!bufferBuilder.isDrawing())
        {
            throw new RuntimeException("bufferbuilder not in drawing state");
        }
        if (renderType.needsSorting)
        {
            throw new RuntimeException("bufferbuilder is not cacheable");
        }

        bufferBuilder.finishDrawing();
    }

    /**
     * @see RenderType#finish()
     */
    public static BuiltBuffer finishBuffer(final BufferBuilder bufferBuilder, final RenderType renderType)
    {
        checkAndFinishBuffer(bufferBuilder, renderType);
        return new BuiltBuffer(bufferBuilder.getNextBuffer(), renderType);
    }

    /**
     * @see RenderType#finish()
     */
    public static BuiltBuffer finishBufferForVBO(final BufferBuilder bufferBuilder, final RenderType renderType, final String name)
    {
        checkAndFinishBuffer(bufferBuilder, renderType);
        return new VBOBuiltBuffer(bufferBuilder.getNextBuffer(), renderType, name);
    }

    /**
     * @see RenderType#finish()
     */
    public static void drawBuiltBuffer(final BuiltBuffer builtBuffer)
    {
        if (builtBuffer == null)
        {
            return;
        }

        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        if (builtBuffer instanceof VBOBuiltBuffer)
        {
            // todo: cache
            return;
        }

        builtBuffer.getRenderType().setupRenderState();
        builtBuffer.getByteBuffer().clear();
        if (builtBuffer.getDrawState().getVertexCount() > 0)
        {
            builtBuffer.getDrawState().getFormat().setupBufferState(MemoryUtil.memAddress(builtBuffer.getByteBuffer()));
            GlStateManager.drawArrays(builtBuffer.getDrawState().getDrawMode(), 0, builtBuffer.getDrawState().getVertexCount());
            builtBuffer.getDrawState().getFormat().clearBufferState();
        }
        builtBuffer.getRenderType().clearRenderState();
    }

    public static void drawBuiltBufferAtMatrix(final BuiltBuffer builtBuffer, final Matrix4f rawProjectionMatrix)
    {
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(rawProjectionMatrix);
        drawBuiltBuffer(builtBuffer);
        RenderSystem.popMatrix();
    }

    public static class BuiltBuffer extends Pair<DrawState, ByteBuffer>
    {
        private final RenderType renderType;

        public BuiltBuffer(final Pair<DrawState, ByteBuffer> pair, final RenderType renderTypeIn)
        {
            super(pair.getFirst(), pair.getSecond());
            renderType = renderTypeIn;
        }

        public RenderType getRenderType()
        {
            return renderType;
        }

        public ByteBuffer getByteBuffer()
        {
            return super.getSecond();
        }

        public DrawState getDrawState()
        {
            return super.getFirst();
        }
    }

    public static class VBOBuiltBuffer extends BuiltBuffer
    {
        private final String name;

        public VBOBuiltBuffer(final Pair<DrawState, ByteBuffer> pair, final RenderType renderTypeIn, final String name)
        {
            super(pair, renderTypeIn);
            this.name = name;
        }

        @Override
        public int hashCode()
        {
            return name.hashCode();
        }

        @Override
        public boolean equals(final Object obj)
        {
            return obj instanceof VBOBuiltBuffer && ((VBOBuiltBuffer) obj).name.equals(this.name);
        }
    }
}

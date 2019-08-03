package com.ldtteam.structurize.client.render;

import com.ldtteam.structurize.Instances;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import static org.lwjgl.opengl.GL11.*;

public class StructureTessellator
{
    private enum State
    {
        NEW,
        BUILDING,
        READY,
        BUILT;
    }

    private static final int VERTEX_COMPONENT_SIZE = 3;
    private static final int COLOR_COMPONENT_SIZE = 4;
    private static final int TEX_COORD_COMPONENT_SIZE = 2;
    private static final int LIGHT_TEX_COORD_COMPONENT_SIZE = TEX_COORD_COMPONENT_SIZE;
    private static final int VERTEX_SIZE = 28;
    private static final int VERTEX_COMPONENT_OFFSET = 0;
    private static final int COLOR_COMPONENT_OFFSET = 12;
    private static final int TEX_COORD_COMPONENT_OFFSET = 16;
    private static final int LIGHT_TEXT_COORD_COMPONENT_OFFSET = 24;
    private static final int DEFAULT_BUFFER_SIZE = 2097152;

    private final BufferBuilder builder;
    private final VertexBuffer buffer = new VertexBuffer(DefaultVertexFormats.BLOCK);
    private final VertexBufferUploader vboUploader = new VertexBufferUploader();
    private State state = State.NEW;

    public StructureTessellator()
    {
        this.builder = new BufferBuilder(DEFAULT_BUFFER_SIZE);
        this.vboUploader.setVertexBuffer(buffer);
    }

    /**
     * Draws the data set up in this tessellator and resets the state to prepare for new drawing.
     */
    public void draw()
    {
        GlStateManager.pushMatrix();

        this.buffer.bindBuffer();

        preBlueprintDraw();

        GlStateManager.bindTexture(Minecraft.getInstance().getTextureMap().getGlTextureId());

        this.buffer.drawArrays(GL_QUADS);

        postBlueprintDraw();

        VertexBuffer.unbindBuffer();

        GlStateManager.popMatrix();
    }

    private static void preBlueprintDraw()
    {
        Instances.getOptifineCompat().preBlueprintDraw();

        GlStateManager.enableClientState(GL_VERTEX_ARRAY);

        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
        GlStateManager.enableClientState(GL_TEXTURE_COORD_ARRAY);
        GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
        GlStateManager.enableClientState(GL_TEXTURE_COORD_ARRAY);
        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
        GlStateManager.enableClientState(GL_COLOR_ARRAY);

        // Optifine uses its one vertexformats.
        // It handles the setting of the pointers itself.
        if (Instances.getOptifineCompat().setupArrayPointers())
        {
            return;
        }

        GlStateManager.vertexPointer(VERTEX_COMPONENT_SIZE, GL_FLOAT, VERTEX_SIZE, VERTEX_COMPONENT_OFFSET);
        GlStateManager.colorPointer(COLOR_COMPONENT_SIZE, GL_UNSIGNED_BYTE, VERTEX_SIZE, COLOR_COMPONENT_OFFSET);
        GlStateManager.texCoordPointer(TEX_COORD_COMPONENT_SIZE, GL_FLOAT, VERTEX_SIZE, TEX_COORD_COMPONENT_OFFSET);
        GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
        GlStateManager.texCoordPointer(LIGHT_TEX_COORD_COMPONENT_SIZE, GL_SHORT, VERTEX_SIZE, LIGHT_TEXT_COORD_COMPONENT_OFFSET);
        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);

        // GlStateManager.disableCull(); no need to disable cull
    }

    private void postBlueprintDraw()
    {
        // GlStateManager.enableCull();

        for (final VertexFormatElement vertexformatelement : DefaultVertexFormats.BLOCK.getElements())
        {
            final VertexFormatElement.Usage vfeUsage = vertexformatelement.getUsage();
            final int formatIndex = vertexformatelement.getIndex();

            switch (vfeUsage)
            {
                case POSITION:
                    GlStateManager.disableClientState(GL_VERTEX_ARRAY);
                    break;
                case UV:
                    GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + formatIndex);
                    GlStateManager.disableClientState(GL_TEXTURE_COORD_ARRAY);
                    GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
                    break;
                case COLOR:
                    GlStateManager.disableClientState(GL_COLOR_ARRAY);
                    GlStateManager.clearCurrentColor();
                    break;
                default:
                    // NOOP
                    break;
            }
        }

        // Disable the pointers again.
        Instances.getOptifineCompat().postBlueprintDraw();
    }

    /**
     * Method to start the building of the blueprint VBO.
     * Can only be called once.
     */
    public void startBuilding()
    {
        if (state != State.NEW)
        {
            throw new IllegalStateException("Tessellator already build before");
        }

        state = State.BUILDING;
        builder.begin(GL_QUADS, DefaultVertexFormats.BLOCK);
    }

    public BufferBuilder getBuilder()
    {
        if (state != State.BUILDING)
        {
            throw new IllegalStateException("Cannot retrieve BufferBuilder when Tessellator is in readonly.");
        }
        return this.builder;
    }

    /**
     * Method to end the building of the blueprint VBO.
     * Can only be called once.
     */
    public void finishBuilding()
    {
        if (state != State.BUILDING)
        {
            throw new IllegalStateException("Tessellator already build before");
        }
        this.builder.finishDrawing();
        state = State.READY;
    }

    public void ensureUploaded()
    {
        if (state == State.READY)
        {
            // Tell optifine that we are loading a new instance into the GPU.
            // This ensures that normals are calculated so that we know in which direction a face is facing. (Aka what is outside and what
            // inside)
            Instances.getOptifineCompat().beforeBuilderUpload(this);
            this.vboUploader.draw(this.builder);
            state = State.BUILT;
        }
    }

    public VertexBuffer getBuffer()
    {
        return buffer;
    }

    public boolean isBuilt()
    {
        return state == State.READY || state == State.BUILT;
    }
}

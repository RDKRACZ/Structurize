package com.ldtteam.structurize.client.render.util;

import com.ldtteam.structurize.util.Utils;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderTypes extends RenderType
{
    /**
     * Private constructor to hide implicit public one.
     */
    private RenderTypes()
    {
        super(null, null, 0, 0, false, false, null, null);
        /**
         * Intentionally left empty
         */
    }

    protected static final RenderType COLORED_SHAPE = makeType(Utils.createLocationFor("triangles_colored").toString(),
        DefaultVertexFormats.POSITION_COLOR,
        GL11.GL_TRIANGLES,
        256,
        RenderType.State.getBuilder().writeMask(COLOR_WRITE).texture(NO_TEXTURE).transparency(TRANSLUCENT_TRANSPARENCY).build(false));
}

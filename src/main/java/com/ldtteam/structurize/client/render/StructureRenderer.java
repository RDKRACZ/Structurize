package com.ldtteam.structurize.client.render;

import com.ldtteam.structurize.client.render.util.RenderUtils;
import com.ldtteam.structurize.structure.Structure;
import com.ldtteam.structurize.structure.StructureBB;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11C;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class StructureRenderer implements IWorldRenderer
{
    private static final Minecraft mc = Minecraft.getInstance();

    private final Structure structure;
    private final StructureBB structureBB;
    private final StructureWorld world;

    public StructureRenderer(final Structure structure, final StructureBB structureBB)
    {
        this.structure = structure;
        this.structureBB = structureBB;
        this.world = null;
    }

    @Override
    public void render(final RenderWorldLastEvent context)
    {
        final long finishTimeNano = context.getFinishTimeNano();
        final MatrixStack matrixStack = context.getMatrixStack();

        final IProfiler profiler = mc.getProfiler();
        final Matrix4f curMatrix = matrixStack.getLast().getMatrix();
        final ActiveRenderInfo activeRenderInfo = mc.gameRenderer.getActiveRenderInfo();
        final Vec3d projectedView = activeRenderInfo.getProjectedView();
        final double camX = projectedView.getX();
        final double camY = projectedView.getY();
        final double camZ = projectedView.getZ();
        final float renderDistance = mc.gameRenderer.getFarPlaneDistance();

        profiler.startSection("culling");
        final ClippingHelperImpl clippinghelperimpl = new ClippingHelperImpl(curMatrix, context.getProjectionMatrix());
        clippinghelperimpl.setCameraPosition(camX, camY, camZ);

        profiler.endStartSection("terrain_setup");
        // setupTerrain(activeRenderInfo, clippinghelperimpl, false, context.getContext().frameId, false);

        profiler.endStartSection("updatechunks");
        final int framerateLimit = mc.gameSettings.framerateLimit;
        final long nanoPerFrame = framerateLimit == AbstractOption.FRAMERATE_LIMIT.getMaxValue() ? 0L : 1_000_000_000L / framerateLimit;
        final long timeDiff = Util.nanoTime() - finishTimeNano;
        final long recentAvarage = RenderUtils.renderTimeManager.nextValue(timeDiff);
        final long shoundEndIn = MathHelper.clamp(recentAvarage * 3L / 2L, nanoPerFrame, 33_333_333L);
        // updateChunks(finishTimeNano + shoundEndIn);

        profiler.endStartSection("terrain");
        // renderBlockLayer(RenderType.getSolid(), matrixStack, camX, camY, camZ);
        // FORGE: fix flickering leaves when mods mess up the blurMipmap settings
        mc.getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, mc.gameSettings.mipmapLevels > 0);
        // renderBlockLayer(RenderType.getCutoutMipped(), matrixStack, camX, camY, camZ);
        mc.getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        // renderBlockLayer(RenderType.getCutout(), matrixStack, camX, camY, camZ);

        profiler.endStartSection("entities");
        RenderHelper.setupLevelDiffuseLighting(matrixStack.getLast().getMatrix());

        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(curMatrix);
        RenderUtils.drawBuiltBuffer(null);
        RenderSystem.popMatrix();
    }

    @Override
    public void rebuild()
    {
        TileEntityRendererDispatcher.instance.world = null;
        TileEntityRendererDispatcher.instance.renderInfo = null;
        mc.getRenderManager().info = null;
        mc.getRenderManager().world = null;
    }
}

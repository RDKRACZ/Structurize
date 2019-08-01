package com.ldtteam.structurize.client.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.pipeline.PlaceEventInfoHolder;
import com.ldtteam.structurize.structure.providers.IStructureDataProvider;
import com.ldtteam.structurize.util.CubeCoordinateIterator;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import org.jetbrains.annotations.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.ModelDataManager;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

/**
 * The renderer for blueprint.
 * Holds all information required to render a blueprint.
 */
public class StructureRenderer
{
    private final StructureWorld structureWorld;
    private final List<TileEntity> tileEntities;
    private final List<Entity> entities;
    private StructureTessellator tessellator;
    private final BlockPos mirrorRotationAnchor;
    private final RenderEventWrapper<?, ?> event;

    /**
     * Static factory utility method to handle the extraction of the values from the blueprint.
     *
     * @param event The blueprint to create an instance for.
     */
    public StructureRenderer(final RenderEventWrapper<?, ?> event)
    {
        this.event = event;
        this.structureWorld = new StructureWorld(event);
        this.tileEntities = instantiateTileEntities();
        this.entities = instantiateEntities();
        this.tessellator = new StructureTessellator();
        this.mirrorRotationAnchor = event.getEvent().getStructure().getZeroBasedMirrorRotationAnchor();

        this.setup();
    }

    /**
     * Sets up the renders VBO.
     */
    private void setup()
    {
        tessellator.startBuilding();

        final Random random = new Random();

        for (int y = 0; y < structureWorld.getStructure().getYsize(); y++)
        {
            for (int z = 0; z < structureWorld.getStructure().getZsize(); z++)
            {
                for (int x = 0; x < structureWorld.getStructure().getXsize(); x++)
                {
                    final BlockPos bp = new BlockPos(x, y, z);
                    final BlockState bs = structureWorld.getBlockState(bp);

                    Minecraft.getInstance()
                        .getBlockRendererDispatcher()
                        .renderBlock(bs, bp, structureWorld, tessellator.getBuilder(), random, ModelDataManager.getModelData(structureWorld, bp));
                }
            }
        }

        tessellator.finishBuilding();
    }

    /**
     * Draws an instance of the blueprint at the given position, with the given rotation, and mirroring.
     *
     * @param event        event to draw
     * @param view         drawing offset
     * @param recompTessel whether tesselator should be recompiled or not
     */
    public void draw(final Vec3d view, final boolean recompTessel)
    {
        if (recompTessel)
        {
            this.tessellator = new StructureTessellator();
            setup();
        }

        // Handle things like mirror, rotation and offset.
        preBlueprintDraw(view);

        // Draw normal blocks.
        tessellator.draw();

        RenderHelper.enableStandardItemLighting();

        // Draw tile entities.
        tileEntities.forEach(tileEntity -> {
            GlStateManager.pushMatrix();
            final int combinedLight = tileEntity.getWorld().getCombinedLight(tileEntity.getPos(), 0);
            final int lightMapX = combinedLight % 65536;
            final int lightMapY = combinedLight / 65536;
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) lightMapX, (float) lightMapY);

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            TileEntityRendererDispatcher.instance.render(tileEntity, tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), 1f);
            GlStateManager.popMatrix();
        });

        RenderHelper.disableStandardItemLighting();

        // Draw entities
        entities.forEach(entity -> {
            GlStateManager.pushMatrix();
            int brightnessForRender = entity.getBrightnessForRender();

            if (entity.isBurning())
            {
                brightnessForRender = 15728880;
            }

            final int lightMapX = brightnessForRender % 65536;
            final int lightMapY = brightnessForRender / 65536;
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) lightMapX, (float) lightMapY);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            Minecraft.getInstance().getRenderManager().renderEntity(entity, entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0, true);

            GlStateManager.popMatrix();
        });

        postBlueprintDraw();
    }

    private void preBlueprintDraw(final Vec3d view)
    {
        final ITextureObject textureObject = Minecraft.getInstance().getTextureMap();
        GlStateManager.bindTexture(textureObject.getGlTextureId());

        GlStateManager.pushMatrix();
        final Vec3d translatedView = new Vec3d(event.getEvent().getPosition().getAnchor()).subtract(view);
        GlStateManager.translated(translatedView.getX(), translatedView.getY(), translatedView.getZ());

        RenderUtil.applyRotationToYAxis(event.getRotation(), mirrorRotationAnchor);
        RenderUtil.applyMirror(event.isMirrored() ? Mirror.FRONT_BACK : Mirror.NONE, mirrorRotationAnchor);

        // GlStateManager.scaled(HALF_PERCENT_SHRINK, HALF_PERCENT_SHRINK, HALF_PERCENT_SHRINK); testing without clipping fix
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.clearCurrentColor();
        GlStateManager.pushMatrix();
    }

    private void postBlueprintDraw()
    {
        GlStateManager.popMatrix();
        GlStateManager.clearCurrentColor();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public StructureTessellator getTessellator()
    {
        return tessellator;
    }

    /**
     * Creates list of tileentities from given structure, sets their world to given one.
     *
     * @param structure structure provider
     * @return list of tileentities
     */

    private List<TileEntity> instantiateTileEntities()
    {
        final List<TileEntity> result = new ArrayList<>();
        final BlockPos end = new BlockPos(
            event.getEvent().getStructure().getXsize() - 1,
            event.getEvent().getStructure().getYsize() - 1,
            event.getEvent().getStructure().getZsize() - 1);

        for (final BlockPos bp : new CubeCoordinateIterator(BlockPos.ZERO, end))
        {
            final TileEntity te = structureWorld.getTileEntity(bp);
            if (te != null)
            {
                result.add(te);
            }
        }

        return result;
    }

    /**
     * Creates a list of entities located in the blueprint, placed inside that blueprints block access world.
     *
     * @param structure The blueprint whos entities need to be instantiated.
     * @return A list of entities in the blueprint
     */

    public List<Entity> instantiateEntities()
    {
        return event.getEvent()
            .getStructure()
            .getEntities()
            .stream()
            .map(entityInfo -> RenderTransformers.transformEntity(entityInfo))
            .map(entityInfo -> constructEntity(entityInfo, structureWorld))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Nullable
    private Entity constructEntity(@Nullable final CompoundNBT entityData, final StructureWorld structureWorld)
    {
        if (entityData == null)
        {
            return null;
        }

        final String entityId = entityData.getString("id");

        try
        {
            final CompoundNBT compound = entityData.copy();
            compound.putUniqueId("UUID", UUID.randomUUID());
            final Optional<EntityType<?>> type = EntityType.readEntityType(compound);
            if (type.isPresent())
            {
                final Entity entity = type.get().create(structureWorld);
                if (entity != null)
                {
                    entity.deserializeNBT(compound);
                    return entity;
                }
            }
            return null;
        }
        catch (final Exception ex)
        {
            Instances.getLogger().error("Could not create entity: " + entityId + " with nbt: " + entityData.toString(), ex);
            return null;
        }
    }
}

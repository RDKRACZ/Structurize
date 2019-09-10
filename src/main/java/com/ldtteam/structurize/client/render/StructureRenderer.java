package com.ldtteam.structurize.client.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.pipeline.build.EventInfoHolder;
import com.ldtteam.structurize.util.CubePosIterator;
import com.mojang.blaze3d.platform.GlStateManager;
import org.jetbrains.annotations.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
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
    private List<TileEntity> tileEntities;
    private List<Entity> entities;
    private StructureTessellator tessellator;
    private final EventInfoHolder<?> event;
    private Object updateLock = new Object();
    private Thread currentUpdateThread;
    private boolean keepRecompiling = false;

    /**
     * Static factory utility method to handle the extraction of the values from the blueprint.
     *
     * @param event       The blueprint to create an instance for.
     * @param shouldSetup if setup thread should be created
     */
    public StructureRenderer(final EventInfoHolder<?> event, final boolean shouldSetup)
    {
        this.event = event;
        this.structureWorld = new StructureWorld(event);

        if (shouldSetup)
        {
            this.setup();
        }
    }

    /**
     * Marks tessellator for recompiling, usually because of updating structure data.
     */
    public void recompile()
    {
        keepRecompiling = true;
    }

    /**
     * Creates thread for building new values from actual structure data.
     * Updates this instance with new values once thread completes its job.
     */
    private void setup()
    {
        keepRecompiling = false;
        final StructureTessellator newTessellator = new StructureTessellator();
        currentUpdateThread = new Thread(() -> {
            final StructureRenderer struct = new StructureRenderer(event, false);
            struct.setup0(newTessellator);
            synchronized (updateLock)
            {
                this.tileEntities = struct.tileEntities;
                this.entities = struct.entities;
                this.tessellator = newTessellator;
            }
        });
        currentUpdateThread.setPriority(Thread.NORM_PRIORITY + 1);
        currentUpdateThread.start();
    }

    /**
     * Builds new values and compiles blocks into given tessellator.
     *
     * @param tess tessellator to compile into
     */
    private void setup0(final StructureTessellator tess)
    {
        tileEntities = instantiateTileEntities();
        entities = instantiateEntities();

        tess.startBuilding();

        final Random random = new Random();
        final BlockPos structEnd = new BlockPos(
            structureWorld.getStructure().getXsize() - 1,
            structureWorld.getStructure().getYsize() - 1,
            structureWorld.getStructure().getZsize() - 1);

        for (final BlockPos bp : new CubePosIterator(BlockPos.ZERO, structEnd))
        {
            final BlockState bs = structureWorld.getBlockState(bp);

            Minecraft.getInstance()
                .getBlockRendererDispatcher()
                .renderBlock(bs, bp, structureWorld, tess.getBuilder(), random, ModelDataManager.getModelData(structureWorld, bp));

            if (!bs.getFluidState().isEmpty())
            {
                Minecraft.getInstance().getBlockRendererDispatcher().renderFluid(bp, structureWorld, tess.getBuilder(), bs.getFluidState());
            }
        }

        tess.finishBuilding();
    }

    /**
     * Draws an instance of the blueprint at the given position, with the given rotation, and mirroring.
     *
     * @param view         drawing offset
     * @param recompTessel whether tesselator should be recompiled or not
     */
    public void draw(final Vec3d view, final boolean recompTessel)
    {
        synchronized (updateLock)
        {
            if (tessellator == null || !tessellator.isBuilt())
            {
                return;
            }

            // Is tessellator uploaded to gpu
            tessellator.ensureUploaded();

            // Should render data be updated
            if (recompTessel || keepRecompiling)
            {
                if (currentUpdateThread != null && currentUpdateThread.isAlive())
                {
                    keepRecompiling = true;
                }
                else
                {
                    setup();
                }
            }

            // Translate view
            GlStateManager.pushMatrix();
            final Vec3d translatedView = new Vec3d(event.getPosition().getAnchor()).subtract(view);
            GlStateManager.translated(translatedView.getX(), translatedView.getY(), translatedView.getZ());

            // Setup rendering
            GlStateManager.bindTexture(Minecraft.getInstance().getTextureMap().getGlTextureId());
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.clearCurrentColor();

            // Draw tile entities.
            Minecraft.getInstance().gameRenderer.disableLightmap();

            RenderHelper.enableStandardItemLighting();
            TileEntityRendererDispatcher.instance.preDrawBatch();
            tileEntities.forEach(tileEntity -> {
                TileEntityRendererDispatcher.instance
                    .render(tileEntity, tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), 1f);
            });
            TileEntityRendererDispatcher.instance.drawBatch();
            RenderHelper.disableStandardItemLighting();

            // Draw entities
            entities.forEach(entity -> {
                Minecraft.getInstance().getRenderManager().renderEntity(entity, entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0, true);
                Minecraft.getInstance().gameRenderer.disableLightmap();
            });

            // Draw normal blocks.
            tessellator.draw();

            // Setdown rendering
            GlStateManager.clearCurrentColor();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    /**
     * Creates list of tileentities from given structure, sets their world to given one.
     *
     * @return list of tileentities
     */
    private List<TileEntity> instantiateTileEntities()
    {
        final List<TileEntity> result = new ArrayList<>();
        final BlockPos end = new BlockPos(event.getStructure().getXsize() - 1, event.getStructure().getYsize() - 1, event.getStructure().getZsize() - 1);

        for (final BlockPos bp : new CubePosIterator(BlockPos.ZERO, end))
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
     * Creates list of entities located in the blueprint, placed inside that blueprints block access world.
     *
     * @return list of entities
     */
    public List<Entity> instantiateEntities()
    {
        return event.getStructure()
            .getEntities()
            .stream()
            .map(entityInfo -> RenderTransformers.transformEntity(entityInfo))
            .map(entityInfo -> constructEntity(entityInfo))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Creates new Entity from given nbt data.
     *
     * @param entityData entity nbt data
     * @return entity if success, null otherwise
     */
    @Nullable
    private Entity constructEntity(@Nullable final CompoundNBT entityData)
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

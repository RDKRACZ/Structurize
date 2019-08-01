package com.ldtteam.structurize.client.render;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.structure.providers.IStructureDataProvider;
import org.jetbrains.annotations.NotNull;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.MapData;

/**
 * Simulated structure world.
 */
public class StructureWorld extends World implements IBlockReader
{
    private final RenderEventWrapper<?, ?> event;

    /**
     * Constructor to create a new world/blockAccess.
     *
     * @param event event
     */
    public StructureWorld(final RenderEventWrapper<?, ?> event)
    {
        super(
            Minecraft.getInstance().world.getWorldInfo(),
            Minecraft.getInstance().world.dimension.getType(),
            new BiFunction<World, Dimension, AbstractChunkProvider>(){
                @Override
                public AbstractChunkProvider apply(final World world, final Dimension dimension)
                {
                    return Minecraft.getInstance().world.getChunkProvider();
                }
            },
            Minecraft.getInstance().world.getProfiler(),
            true);
        this.event = event;
    }

    /**
     * Getter for structure reference.
     *
     * @return structure
     */
    public IStructureDataProvider getStructure()
    {
        return event.getEvent().getStructure();
    }

    @NotNull
    @Override
    public BlockState getBlockState(@NotNull final BlockPos pos)
    {
        if (Minecraft.getInstance().world.getBlockState(pos.add(event.getEvent().getPosition().getAnchor())).isSolid())
        {
            return Blocks.VOID_AIR.getDefaultState();
        }
        final short index;
        try
        {
            index = getStructure().getBlocks()[pos.getY()][pos.getZ()][pos.getX()];
        }
        catch (final ArrayIndexOutOfBoundsException e)
        {
            return Blocks.VOID_AIR.getDefaultState();
        }
        return RenderTransformers.transformBlockState(getStructure().getBlockPalette().get(index));
        // TODO: cache removed, should be back?
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(@NotNull final BlockPos pos)
    {
        CompoundNBT teData;
        try
        {
            teData = getStructure().getTileEntities()[pos.getY()][pos.getZ()][pos.getX()];
        }
        catch (final ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
        if (teData == null)
        {
            return null;
        }
        teData = RenderTransformers.transformTileEntity(teData);
        final String entityId = teData.getString("id");

        try
        {
            final CompoundNBT compound = teData.copy();
            compound.putInt("x", pos.getX());
            compound.putInt("y", pos.getY());
            compound.putInt("z", pos.getZ());

            final TileEntity te = TileEntity.create(compound);

            if (te != null)
            {
                te.setWorld(this);
                return te;
            }
        }
        catch (final Exception ex)
        {
            Instances.getLogger().error("Could not create tile entity: " + entityId + " with nbt: " + teData.toString(), ex);
        }
        return null;
        // TODO: cache removed, should be back?
    }

    @NotNull
    @Override
    public Chunk getChunk(final int chunkX, final int chunkZ)
    {
        return new StructureChunk(this, chunkX, chunkZ);
    }

    @Nullable
    @Override
    public Entity getEntityByID(final int id)
    {
        return null;
    }

    @Override
    public int getCombinedLight(@NotNull final BlockPos pos, final int lightValue)
    {
        return 15 << 20 | 15 << 4;
    }

    @Override
    public int getLight(@NotNull final BlockPos pos)
    {
        return 15;
    }

    @Override
    public float getBrightness(@NotNull final BlockPos pos)
    {
        return 1f;
    }

    @Override
    public void playSound(
        @Nullable final PlayerEntity player,
        final double x,
        final double y,
        final double z,
        final SoundEvent soundIn,
        final SoundCategory category,
        final float volume,
        final float pitch)
    {

    }

    @Override
    public void playMovingSound(
        @Nullable final PlayerEntity player,
        final Entity entity,
        final SoundEvent event,
        final SoundCategory category,
        final float volume,
        final float pitch)
    {

    }

    @Override
    public boolean isAirBlock(@NotNull final BlockPos pos)
    {
        return getBlockState(pos).getBlock() instanceof AirBlock;
    }

    @Override
    public boolean isAreaLoaded(final int fromX, final int fromY, final int fromZ, final int toX, final int toY, final int toZ)
    {
        return true;
    }

    @NotNull
    @Override
    public Biome getBiome(@NotNull final BlockPos pos)
    {
        return Biomes.PLAINS;
    }

    @NotNull
    @Override
    public Chunk getChunk(final BlockPos pos)
    {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Override
    public void notifyBlockUpdate(@NotNull final BlockPos pos, @NotNull final BlockState oldState, @NotNull final BlockState newState, final int flags)
    {

    }

    @NotNull
    @Override
    public ITickList<Block> getPendingBlockTicks()
    {
        return Minecraft.getInstance().world.getPendingBlockTicks();
    }

    @NotNull
    @Override
    public ITickList<Fluid> getPendingFluidTicks()
    {
        return Minecraft.getInstance().world.getPendingFluidTicks();
    }

    @NotNull
    @Override
    public AbstractChunkProvider getChunkProvider()
    {
        return Minecraft.getInstance().world.getChunkProvider();
    }

    @Override
    public void playEvent(@Nullable final PlayerEntity player, final int type, final BlockPos pos, final int data)
    {

    }

    @Nullable
    @Override
    public MapData func_217406_a(@NotNull final String stringIn)
    {
        return null;
    }

    @Override
    public void func_217399_a(@NotNull final MapData mapDataIn)
    {

    }

    @Override
    public int getNextMapId()
    {
        return 0;
    }

    @Override
    public void sendBlockBreakProgress(final int breakerId, @NotNull final BlockPos pos, final int progress)
    {

    }

    @NotNull
    @Override
    public Scoreboard getScoreboard()
    {
        return Minecraft.getInstance().world.getScoreboard();
    }

    @NotNull
    @Override
    public RecipeManager getRecipeManager()
    {
        return Minecraft.getInstance().world.getRecipeManager();
    }

    @NotNull
    @Override
    public NetworkTagManager getTags()
    {
        return Minecraft.getInstance().world.getTags();
    }

    @Override
    public int getStrongPower(@NotNull final BlockPos pos, @NotNull final Direction direction)
    {
        return 0;
    }

    @NotNull
    @Override
    public WorldType getWorldType()
    {
        return WorldType.DEFAULT;
    }

    @NotNull
    @Override
    public List<? extends PlayerEntity> getPlayers()
    {
        return Collections.emptyList();
    }
}

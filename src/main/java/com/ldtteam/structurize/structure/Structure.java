package com.ldtteam.structurize.structure;

import java.util.List;
import com.ldtteam.structurize.structure.data.AttributeMap;
import com.ldtteam.structurize.structure.data.BlockStatePalette;
import com.ldtteam.structurize.structure.data.Chunk;
import com.ldtteam.structurize.util.constant.NBTConstants;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.Constants.NBT;

public class Structure implements IStructure
{
    private static final BlockInfo EMPTY_BLOCK_INFO = new BlockInfo(Blocks.AIR.getDefaultState(), null);
    private final BlockStatePalette palette;
    private final AttributeMap attributeMap;
    private Long2ObjectMap<Chunk> chunks = new Long2ObjectOpenHashMap<>();
    private BlockPos center;

    public Structure(final CompoundNBT nbt)
    {
        palette = new BlockStatePalette(nbt.getList(NBTConstants.STRUCTURE_PALETTE, NBT.TAG_COMPOUND));
        attributeMap = new AttributeMap(nbt.getCompound(NBTConstants.STRUCTURE_ATTRIBUTE_MAP));
        for (final INBT chunkNbt : nbt.getList(NBTConstants.STRUCTURE_CHUNKS, NBT.TAG_COMPOUND))
        {
            final Chunk chunk = new Chunk((CompoundNBT) chunkNbt, palette);
            chunks.put(chunk.getPos().asLong(), chunk);
        }
        center = attributeMap.getAttributeValue(Attribute.DEFAULT_CENTER);
    }

    public CompoundNBT serializeNBT()
    {
        final ListNBT chunksNBT = new ListNBT();
        for (final Chunk chunk : chunks.values())
        {
            chunksNBT.add(chunk.serializeNBT());
        }

        final CompoundNBT nbt = new CompoundNBT();
        nbt.put(NBTConstants.STRUCTURE_PALETTE, palette.serializeNBT());
        nbt.put(NBTConstants.STRUCTURE_CHUNKS, chunksNBT);
        nbt.put(NBTConstants.STRUCTURE_ATTRIBUTE_MAP, attributeMap.serializeNBT());
        return nbt;
    }

    public BlockState getBlockState(final BlockPos pos)
    {
        final Chunk chunk = getChunk(pos.getX(), pos.getZ());
        return chunk != null ? chunk.getBlockState(pos) : Blocks.AIR.getDefaultState();
    }

    public FluidState getFluidState(final BlockPos pos)
    {
        final Chunk chunk = getChunk(pos.getX(), pos.getZ());
        return chunk != null ? chunk.getFluidState(pos) : Fluids.EMPTY.getDefaultState();
    }

    public TileEntity getTileEntity(final BlockPos pos)
    {
        final Chunk chunk = getChunk(pos.getX(), pos.getZ());
        return chunk != null ? chunk.getTileEntity(pos) : null;
    }

    public Chunk getChunk(final int x, final int z)
    {
        return chunks.get(ChunkPos.asLong(x, z));
    }

    @Override
    public BlockInfo getBlockInfo(final BlockPos pos)
    {
        final Chunk chunk = chunks.get(ChunkPos.asLong(pos.getX(), pos.getZ()));
        return chunk != null ? new BlockInfo(chunk.getBlockState(pos), chunk.getTileEntity(pos)) : EMPTY_BLOCK_INFO;
    }

    @Override
    public List<Entity> getEntitiesInChunk(final BlockPos pos)
    {
        final Chunk chunk = chunks.get(ChunkPos.asLong(pos.getX(), pos.getZ()));
        return chunk != null ? chunk.getEntities() : new ObjectArrayList<>();
    }

    @Override
    public StructureBB createBoundingBox(final BlockPos where)
    {
        return new StructureBB(where, where.add(attributeMap.getAttributeValue(Attribute.SIZE)));
    }

    @Override
    public AttributeMap getAttributeMap()
    {
        return attributeMap;
    }

    public BlockPos getCenter()
    {
        return center;
    }

    /**
     * Rotates data using given rotation and 2x2 center mode.
     * To use 1x1 center mode shift structure BB according to rotation.
     *
     * @param rotation rotation to use
     */
    public void rotate(final Rotation rotation)
    {
        final Long2ObjectMap<Chunk> newChunks = new Long2ObjectOpenHashMap<>();
        for (final Long2ObjectMap.Entry<Chunk> oldChunk : chunks.long2ObjectEntrySet())
        {
            final long newChunkPos = rotateChunkPos(oldChunk.getLongKey(), rotation);
            newChunks.put(newChunkPos, oldChunk.getValue());
            oldChunk.getValue().rotate(rotation, new ChunkPos(newChunkPos));
        }
        chunks = newChunks;
        palette.rotate(rotation);
    }

    /**
     * Mirrors data using given mirror and 2x2 center mode.
     * To use 1x1 center mode shift structure BB according to mirror.
     *
     * @param mirror mirror to use
     */
    public void mirror(final Mirror mirror)
    {
        final Long2ObjectMap<Chunk> newChunks = new Long2ObjectOpenHashMap<>();
        for (final Long2ObjectMap.Entry<Chunk> oldChunk : chunks.long2ObjectEntrySet())
        {
            final long newChunkPos = mirrorChunkPos(oldChunk.getLongKey(), mirror);
            newChunks.put(newChunkPos, oldChunk.getValue());
            oldChunk.getValue().mirror(mirror, new ChunkPos(newChunkPos));
        }
        chunks = newChunks;
        palette.mirror(mirror);
    }

    private long rotateChunkPos(final long chunkPos, final Rotation rotation)
    {
        final int x = ChunkPos.getX(chunkPos);
        final int z = ChunkPos.getZ(chunkPos);

        switch (rotation)
        {
            case NONE:
            default:
                return chunkPos;

            case CLOCKWISE_90:
                return ChunkPos.asLong(-z, x);

            case CLOCKWISE_180:
                return ChunkPos.asLong(-x, -z);

            case COUNTERCLOCKWISE_90:
                return ChunkPos.asLong(z, -x);
        }
    }

    private long mirrorChunkPos(final long chunkPos, final Mirror mirror)
    {
        final int x = ChunkPos.getX(chunkPos);
        final int z = ChunkPos.getZ(chunkPos);

        switch (mirror)
        {
            case NONE:
            default:
                return chunkPos;

            case FRONT_BACK:
                return ChunkPos.asLong(-x, z);

            case LEFT_RIGHT:
                return ChunkPos.asLong(x, -z);
        }
    }
}

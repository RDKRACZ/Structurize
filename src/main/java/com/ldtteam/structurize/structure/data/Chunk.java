package com.ldtteam.structurize.structure.data;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.Set;
import java.util.stream.Stream;
import com.ldtteam.structurize.util.constant.NBTConstants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.ITickList;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraftforge.common.util.Constants.NBT;

public class Chunk implements IChunk
{
    private ChunkPos chunkPos;
    private final ChunkSection[] chunkSections = new ChunkSection[16];
    private Int2ObjectMap<TileEntity> tileEntities = new Int2ObjectOpenHashMap<>();
    private final ObjectList<Entity> entities = new ObjectArrayList<>();
    private final BlockStatePalette palette;

    public Chunk(final int chunkX, final int chunkY, final BlockStatePalette paletteIn)
    {
        this.chunkPos = new ChunkPos(chunkX, chunkY);
        this.palette = paletteIn;
    }

    public Chunk(final CompoundNBT nbt, final BlockStatePalette paletteIn)
    {
        this.chunkPos = new ChunkPos(nbt.getLong(NBTConstants.CHUNK_POS));
        this.palette = paletteIn;

        for (final INBT chunkSectionNbt : nbt.getList(NBTConstants.CHUNK_SECTIONS, NBT.TAG_COMPOUND))
        {
            final ChunkSection chunkSection = new ChunkSection((CompoundNBT) chunkSectionNbt, paletteIn);
            chunkSections[chunkSection.getYPos()] = chunkSection;
        }

        for (final INBT tileEntityNbt0 : nbt.getList(NBTConstants.CHUNK_TILE_ENTITIES, NBT.TAG_COMPOUND))
        {
            final CompoundNBT tileEntityNbt = (CompoundNBT) tileEntityNbt0;
            final BlockPos pos = new BlockPos(tileEntityNbt.getInt("x"), tileEntityNbt.getInt("y"), tileEntityNbt.getInt("z"));
            final TileEntity tileEntity = TileEntity.readTileEntity(this.getBlockState(pos), tileEntityNbt);
            tileEntities.put(packBlockPos(tileEntity.getPos()), tileEntity);
        }

        for (final INBT entityNbt : nbt.getList(NBTConstants.CHUNK_ENTITIES, NBT.TAG_COMPOUND))
        {
            entities.add(EntityType.func_220335_a((CompoundNBT) entityNbt, null, Function.identity()));
        }
    }

    public CompoundNBT serializeNBT()
    {
        final ListNBT chunkSectionsNbt = new ListNBT();
        for (final ChunkSection chunkSection : chunkSections)
        {
            if (chunkSection != null)
            {
                chunkSectionsNbt.add(chunkSection.serializeNBT());
            }
        }

        final ListNBT tileEntitiesNbt = new ListNBT();
        for (final Int2ObjectMap.Entry<TileEntity> entry : tileEntities.int2ObjectEntrySet())
        {
            final CompoundNBT teNbt = entry.getValue().serializeNBT();
            final BlockPos pos = unpackBlockPos(entry.getIntKey());
            teNbt.putInt("x", pos.getX());
            teNbt.putInt("y", pos.getY());
            teNbt.putInt("z", pos.getZ());
            tileEntitiesNbt.add(teNbt);
        }

        final ListNBT entitiesNbt = new ListNBT();
        for (final Entity entity : entities)
        {
            final CompoundNBT entNbt = entity.serializeNBT();
            entNbt.remove("UUID");
            entitiesNbt.add(entNbt);
        }

        final CompoundNBT nbt = new CompoundNBT();
        nbt.putLong(NBTConstants.CHUNK_POS, chunkPos.asLong());
        nbt.put(NBTConstants.CHUNK_SECTIONS, chunkSectionsNbt);
        nbt.put(NBTConstants.CHUNK_TILE_ENTITIES, tileEntitiesNbt);
        nbt.put(NBTConstants.CHUNK_ENTITIES, entitiesNbt);
        return nbt;
    }

    @Override
    public void addTileEntity(final BlockPos pos, final TileEntity tileEntityIn)
    {
        tileEntities.put(packBlockPos(pos), tileEntityIn);
    }

    @Override
    public TileEntity getTileEntity(final BlockPos pos)
    {
        return tileEntities.get(packBlockPos(pos));
    }

    @Override
    public void removeTileEntity(final BlockPos pos)
    {
        tileEntities.remove(packBlockPos(pos));
    }

    private int packBlockPos(final BlockPos pos)
    {
        return ((pos.getX() & 15) << 8) + ((pos.getY() & 15) << 4) + (pos.getZ() & 15);
    }

    private BlockPos unpackBlockPos(final int pos)
    {
        return new BlockPos((pos >> 8) & 15, (pos >> 4) & 15, pos & 15);
    }

    public void setBlockState(final BlockPos pos, final BlockState state)
    {
        final int chunkSectionPos = pos.getY() >> 4;
        ChunkSection chunkSection = chunkSections[chunkSectionPos];
        if (chunkSection == null)
        {
            chunkSection = new ChunkSection(chunkSectionPos, palette);
            chunkSections[chunkSectionPos] = chunkSection;
        }
        chunkSection.setBlockState(pos, state);
    }

    @Override
    public BlockState setBlockState(final BlockPos pos, final BlockState state, final boolean isMoving)
    {
        // we don't care about isMoving flag
        final BlockState old = getBlockState(pos);
        setBlockState(pos, state);
        return old;
    }

    @Override
    public BlockState getBlockState(final BlockPos pos)
    {
        final ChunkSection chunkSection = chunkSections[pos.getY() >> 4];
        return chunkSection != null ? chunkSection.getBlockState(pos) : Blocks.AIR.getDefaultState();
    }

    @Override
    public FluidState getFluidState(final BlockPos pos)
    {
        return getBlockState(pos).getFluidState();
    }

    @Override
    public void addEntity(final Entity entityIn)
    {
        entities.add(entityIn);
    }

    public ObjectList<Entity> getEntities()
    {
        return entities;
    }

    public ChunkSection[] getOurSections()
    {
        return chunkSections;
    }

    @Override
    public ChunkPos getPos()
    {
        return chunkPos;
    }

    @Override
    public Set<BlockPos> getTileEntitiesPos()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompoundNBT getTileEntityNBT(final BlockPos pos)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void rotate(final Rotation rotation, final ChunkPos newChunkPos)
    {
        chunkPos = newChunkPos;

        for (int i = 0; i < chunkSections.length; i++)
        {
            if (chunkSections != null)
            {
                chunkSections[i].rotate(rotation);
            }
        }
    }

    public void mirror(final Mirror mirror, final ChunkPos newChunkPos)
    {
        chunkPos = newChunkPos;

        for (int i = 0; i < chunkSections.length; i++)
        {
            if (chunkSections != null)
            {
                chunkSections[i].mirror(mirror);
            }
        }

        final Int2ObjectMap<TileEntity> newTileEntities = new Int2ObjectOpenHashMap<>();

        tileEntities = newTileEntities;
    }

    @Override
    public void func_230343_a_(final Structure<?> p_230343_1_, final long p_230343_2_)
    {
        // Noop
    }

    @Override
    public Map<Structure<?>, LongSet> getStructureReferences()
    {
        // Noop
        return null;
    }

    @Override
    public LongSet func_230346_b_(final Structure<?> p_230346_1_)
    {
        // Noop
        return null;
    }

    @Override
    public StructureStart<?> func_230342_a_(final Structure<?> p_230342_1_)
    {
        // Noop
        return null;
    }

    @Override
    public void func_230344_a_(final Structure<?> p_230344_1_, final StructureStart<?> p_230344_2_)
    {
        // Noop
    }

    @Override
    public void setStructureReferences(final Map<Structure<?>, LongSet> p_201606_1_)
    {
        // Noop
    }

    @Override
    public net.minecraft.world.chunk.ChunkSection[] getSections()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Entry<Type, Heightmap>> getHeightmaps()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setHeightmap(final Type type, final long[] data)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public Heightmap getHeightmap(final Type typeIn)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTopBlockY(final Type heightmapType, final int x, final int z)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setLastSaveTime(final long saveTime)
    {
        // Noop
    }

    @Override
    public Map<Structure<?>, StructureStart<?>> getStructureStarts()
    {
        // Noop
        return null;
    }

    @Override
    public void setStructureStarts(final Map<Structure<?>, StructureStart<?>> structureStartsIn)
    {
        // Noop
    }

    @Override
    public BiomeContainer getBiomes()
    {
        // Noop
        return null;
    }

    @Override
    public void setModified(final boolean modified)
    {
        // Noop
    }

    @Override
    public boolean isModified()
    {
        // Noop
        return false;
    }

    @Override
    public ChunkStatus getStatus()
    {
        return ChunkStatus.FULL;
    }

    @Override
    public ShortList[] getPackedPositions()
    {
        // Noop
        return null;
    }

    @Override
    public CompoundNBT getDeferredTileEntity(final BlockPos pos)
    {
        // Noop
        return null;
    }

    @Override
    public Stream<BlockPos> getLightSources()
    {
        // Noop
        return null;
    }

    @Override
    public ITickList<Block> getBlocksToBeTicked()
    {
        // Noop
        return null;
    }

    @Override
    public ITickList<Fluid> getFluidsToBeTicked()
    {
        // Noop
        return null;
    }

    @Override
    public UpgradeData getUpgradeData()
    {
        // Noop
        return null;
    }

    @Override
    public void setInhabitedTime(final long newInhabitedTime)
    {
        // Noop
    }

    @Override
    public long getInhabitedTime()
    {
        // Noop
        return 0;
    }

    @Override
    public boolean hasLight()
    {
        // Noop
        return false;
    }

    @Override
    public void setLight(final boolean lightCorrectIn)
    {
        // Noop
    }
}
